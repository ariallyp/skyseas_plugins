package com.skyseas.openfireplugins.pushofflinemsg;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import nsp.NSPClient;
import nsp.OAuth2Client;
import nsp.support.common.AccessToken;
import nsp.support.common.NSPException;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.Log;


/**
 * <b>function:</b> send offline msg plugin
 * @author newjueqi
 * @createDate 2013-12-19 
 * @project OpenfirePlugin
 * @blog http://blog.csdn.net/newjueqi
 * @email h6k65@126.com
 * @version 1.0
 */
public class pushofflinemsg implements PacketInterceptor, Plugin {

	private static final Logger log = LoggerFactory.getLogger(pushofflinemsg.class);
	
	public final static String CERT_HOME = "offlinepush.cert_home";
	public final static String CERT_ANDROID_HUAWEI_PWD = "offlinepush.cert_android_pwd";
	public final static String CERT_ANDROID_HUAWEI_APPID = "offlinepush.cert_android_huawei_appid";
	public final static String CERT_ANDROID_HUAWEI_APPKEY = "offlinepush.cert_android_huawei_appkey";
	
    //Hook for intercpetorn
    private InterceptorManager interceptorManager;   
    private static PluginManager pluginManager;
    private UserManager userManager;
    private PresenceManager presenceManager;
    
    public pushofflinemsg() {
        
    }
    
    public void debug(String str){
    	if( true ){
//    		System.out.println(str);
    	}
    }
    
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
    	interceptorManager = InterceptorManager.getInstance();
        interceptorManager.addInterceptor(this);

        XMPPServer server = XMPPServer.getInstance();
        userManager = server.getUserManager();
        presenceManager = server.getPresenceManager();        
        
        pluginManager = manager;
        
        this.debug("start offline 1640");
    }

    public void destroyPlugin() {
    	 this.debug("start offline 1640");
    	 interceptorManager.removeInterceptor(this);
    }

    /**
     * intercept message
     */
    @Override
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {

        JID recipient = packet.getTo();
       // this.doAction(packet, incoming, processed, session);
        if (recipient != null) {

            String username = recipient.getNode();
            
            // if broadcast message or user is not exist
            if (username == null || !UserManager.getInstance().isRegisteredUser(recipient)) {

                return;

            } else if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(recipient.getDomain())) {
            	//not from the same domain

                return;

            } else if ("".equals(recipient.getResource())) {

            }

        }

        this.doAction(packet, incoming, processed, session);

    } 
    
    
    /**
     * <b>send offline msg from this function </b>
     */
    private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {
        Packet copyPacket = packet.createCopy();
        if (packet instanceof Message) {

            Message message = (Message) copyPacket;

            if (message.getType() == Message.Type.chat) {

                if (processed || !incoming) {
                    return;
                }

                Message sendmessage = (Message) packet;
                String content= sendmessage.getBody();
                JID recipient = sendmessage.getTo();
                
                //get message
                try
                {
                	
	                if (recipient.getNode() == null ||
	                        !UserManager.getInstance().isRegisteredUser(recipient.getNode())) {
	                    // Sender is requesting presence information of an anonymous user
	                    throw new UserNotFoundException("Username is null");
	                }
	                
	                Presence status=presenceManager.getPresence(userManager.getUser(recipient.getNode()));
	                
	                if( status!=null ){
	                	this.debug(recipient.getNode()+" online111"+",message: "+content);
	                }else{
	                	System.out.println(recipient.getNode()+" offline111"+",message: "+content);
	                	log.info(recipient.getNode()+" offline111"+",message: "+content);
	                	this.debug(recipient.getNode()+" offline111"+",message: "+content);
	                	
	                	/*
	                	 * add your code here to send offline msg
	                	 * recipient.getNode() : receive's id,for example,if  receive's jid is "23@localhost", receive's id is "23"
	                	 * content: message content
	                	 */
	                	  OAuth2Client oauth2Client = new OAuth2Client();
	                	  System.out.println("begin get serc");
	                	  log.info("begin get serc");
	                      String certhome= JiveGlobals.getProperty(CERT_HOME);
	                      String certpwd=JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_PWD);
	                      ///var/lib/openfire/plugins/pushofflinemsg/resource/
	                      oauth2Client.initKeyStoreStream(new FileInputStream(certhome+"mykeystorebj.jks"), certpwd);
	                      System.out.println("end get serc");
	                	  log.info("end get serc");

	                      String appId =JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_APPID);// "10793221";
	                      String appKey = JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_APPKEY);//"a032c305631ca3a24ca3692d8380067d";
	                      
	                      AccessToken access_token = oauth2Client.getAccessToken("client_credentials", appId, appKey);
	         
	                      System.err.println("access token :" + access_token.getAccess_token() + ",expires time[access token 过期时间]:"
	                          + access_token.getExpires_in());
	                      log.info("access token :" + access_token.getAccess_token() + ",expires time[access token 过期时间]:"
		                          + access_token.getExpires_in());
	                      System.out.println("get token："+access_token.getAccess_token() );
	                      log.info("get token："+access_token.getAccess_token());
	                      NSPClient client = new NSPClient(access_token.getAccess_token());
	                      client.initHttpConnections(30, 50);//设置每个路由的连接数和最大连接数

	                      client.initKeyStoreStream(new FileInputStream(certhome+"mykeystorebj.jks"), certpwd);//如果访问https必须导入证书流和密码
	                      System.out.println("begin push msg");
	                      //调用push单发接口
	                      log.info("begin push msg");
	                 	 huaweipush.single_send(client,content);
	                 	System.out.println("end push msg");
	                 	log.info("end push msg");
	                }//end if
	                
	            }
                catch (UserNotFoundException e) {
                	this.debug("exceptoin "+recipient.getNode()+" not find"+",full jid: "+recipient.toFullJID());
                } catch (NSPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            } else if (message.getType() ==  Message.Type.groupchat) {

                List<?> els = message.getElement().elements("x");
                if (els != null && !els.isEmpty()) {

                } else {
                }
            } else {

            }

        } else if (packet instanceof IQ) {

            IQ iq = (IQ) copyPacket;

            if (iq.getType() == IQ.Type.set && iq.getChildElement() != null && "session".equals(iq.getChildElement().getName())) {

            }

        } else if (packet instanceof Presence) {

            Presence presence = (Presence) copyPacket;

            if (presence.getType() == Presence.Type.unavailable) {


            }

        } 

    } 
    
}
