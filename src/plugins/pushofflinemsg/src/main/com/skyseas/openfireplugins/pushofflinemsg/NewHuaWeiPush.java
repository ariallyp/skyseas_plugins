package com.skyseas.openfireplugins.pushofflinemsg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;



import org.apache.commons.io.IOUtils;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class NewHuaWeiPush {
	private static String appSecret = "c566135546298108a4fd4145a1e6421d";
    private static  String appId = "10776817";//用户在华为开发者联盟申请的appId和appSecret（会员中心->我的产品，点击产品对应的Push服务，点击“移动应用详情”获取）
    private static  String tokenUrl = "https://login.cloud.huawei.com/oauth2/v2/token"; //获取认证Token的URL
    private static  String apiUrl = "https://api.push.hicloud.com/pushsend.do"; //应用级消息下发API
    private static  String accessToken;//下发通知消息的认证Token
    private static  long tokenExpiredTime;  //accessToken的过期时间
    private static  String package_name = "com.wiz.dev.wiztalk"; //package name;
    
	public final static String CERT_ANDROID_HUAWEI_PWD = "offlinepush.cert_android_huawei_pwd";
	public final static String CERT_ANDROID_HUAWEI_APPID = "offlinepush.cert_android_huawei_appid";
	public final static String CERT_ANDROID_HUAWEI_APPKEY = "offlinepush.cert_android_huawei_appkey";
	public final static String ANDROID_APP_PACKAGE_NAME = "offlinepush.android_app_package_name";
	private static final Logger Log = LoggerFactory.getLogger(NewHuaWeiPush.class);
    
	public void initializePlugin(PluginManager manager, File pluginDirectory) {


		/// ######开始获取android 华为推送相关参数信息###########################

		String cert_android_huawei_pwd = JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_PWD, "");
		
		appId = JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_APPID, "");// "10793221";
		if (appId.equals("")) {
			appId="10776817";
		}
		appSecret = JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_APPKEY, "");// "a032c305631ca3a24ca3692d8380067d";
		if (appSecret.equals("")) {
			appSecret="c566135546298108a4fd4145a1e6421d";
		}
		
		package_name = JiveGlobals.getProperty(ANDROID_APP_PACKAGE_NAME, "");
		if (package_name.equals("")) {
			package_name="com.wiz.dev.wiztalk";
		}

		
	}
    
    
    //获取下发通知消息的认证Token
    private static  void refreshToken() throws IOException
    {
        String msgBody = MessageFormat.format(
         "grant_type=client_credentials&client_secret={0}&client_id={1}", 
         URLEncoder.encode(appSecret, "UTF-8"), appId);
        String response = httpPost(tokenUrl, msgBody, 5000, 5000);
        JSONObject obj = JSONObject.parseObject(response);
        accessToken = obj.getString("access_token");
        tokenExpiredTime = System.currentTimeMillis() + obj.getLong("expires_in") - 5*60*1000;
    }
    
    
    //发送Push消息
    public   void sendPushMessage(Device device,String  pushCont, JID jid, String title) throws IOException
    {
        if (tokenExpiredTime <= System.currentTimeMillis())
        {
            refreshToken();
        }      
        /*PushManager.requestToken为客户端申请token的方法，可以调用多次以防止申请token失败*/
        /*PushToken不支持手动编写，需使用客户端的onToken方法获取*/
        JSONArray deviceTokens = new JSONArray();//目标设备Token
        deviceTokens.add(device.getToken());
      
          
        JSONObject body = new JSONObject();//仅通知栏消息需要设置标题和内容，透传消息key和value为用户自定义
        body.put("title", title);//消息标题
        body.put("content", pushCont);//消息内容体
        
        JSONObject param = new JSONObject();
        param.put("appPkgName", package_name);//定义需要打开的appPkgName
        
        JSONObject action = new JSONObject();
        action.put("type", 3);//类型3为打开APP，其他行为请参考接口文档设置
        action.put("param", param);//消息点击动作参数	
        
        JSONObject msg = new JSONObject();
        msg.put("type", 3);//3: 通知栏消息，异步透传消息请根据接口文档设置
        msg.put("action", action);//消息点击动作
        msg.put("body", body);//通知栏消息body内容
        
        JSONObject ext = new JSONObject();//扩展信息，含BI消息统计，特定展示风格，消息折叠。
        ext.put("biTag", "Trump");//设置消息标签，如果带了这个标签，会在回执中推送给CP用于检测某种类型消息的到达率和状态
        ext.put("icon", "http://pic.qiantucdn.com/58pic/12/38/18/13758PIC4GV.jpg");//自定义推送消息在通知栏的图标,value为一个公网可以访问的URL
        
        JSONObject hps = new JSONObject();//华为PUSH消息总结构体
        hps.put("msg", msg);
        hps.put("ext", ext);
        
        JSONObject payload = new JSONObject();
        payload.put("hps", hps);
        
        String postBody = MessageFormat.format(
         "access_token={0}&nsp_svc={1}&nsp_ts={2}&device_token_list={3}&payload={4}",
            URLEncoder.encode(accessToken,"UTF-8"),
            URLEncoder.encode("openpush.message.api.send","UTF-8"),
            URLEncoder.encode(String.valueOf(System.currentTimeMillis() / 1000),"UTF-8"),
            URLEncoder.encode(deviceTokens.toString(),"UTF-8"),
            URLEncoder.encode(payload.toString(),"UTF-8"));
        
        String postUrl = apiUrl + "?nsp_ctx=" + URLEncoder.encode("{\"ver\":\"1\", \"appId\":\"" + appId + "\"}", "UTF-8");
        httpPost(postUrl, postBody, 5000, 5000);
    }
    
    public static String httpPost(String httpUrl, String data, int connectTimeout, int readTimeout) throws IOException
    {
        OutputStream outPut = null;
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        
        try
        {
            URL url = new URL(httpUrl);
            urlConnection = (HttpURLConnection)url.openConnection();          
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.connect();
            
            // POST data
            outPut = urlConnection.getOutputStream();
            outPut.write(data.getBytes("UTF-8"));
            outPut.flush();
            
            // read response
            if (urlConnection.getResponseCode() < 400)
            {
                in = urlConnection.getInputStream();
            }
            else
            {
                in = urlConnection.getErrorStream();
            }
            
            List<String> lines = IOUtils.readLines(in, urlConnection.getContentEncoding());
            StringBuffer strBuf = new StringBuffer();
            for (String line : lines)
            {
                strBuf.append(line);
            }
            System.out.println(strBuf.toString());
            Log.info("str buff for huawei :"+strBuf.toString());
            return strBuf.toString();
        }
        finally
        {
            IOUtils.closeQuietly(outPut);
            IOUtils.closeQuietly(in);
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
    }
}
