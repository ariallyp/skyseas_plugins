package com.skyseas.openfireplugins.push;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * 推送XMPP消息的servlet。 Created by zhangzhi on 2014/11/11.
 */
public class PushServlet extends HttpServlet {
	private final static Logger LOGGER = LoggerFactory.getLogger(PushServlet.class);
	public static String PACKET_CONTENT_PARAMETER_NAME = "packet_content";
	public final static String ALLOW_IP_LIST_KEY = "push.allow_ip_list";
	public final static String IS_NEED_IP_CHECK = "push.ischeckip";
	private final PacketSender sender;

	public PushServlet() {
		this(new DefaultPacketSender());
	}

	PushServlet(PacketSender sender) {
		this.sender = sender;
	}

	/**
	 * 调用API之前需要先验证，客户端是否有权调用。
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//super.service(request, response);
		if (doCheck(request)) {
			//LOGGER.info("推送消息--");
			super.service(request, response);
		} else {
			//LOGGER.info("不推送消息--unauthorized");
			finish(response, HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");
		}
	}

	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 检测客户端是否有权限访问。
	 * 
	 * @param request
	 * @return
	 */
	protected boolean doCheck(HttpServletRequest request) {
		//String ip = request.getRemoteAddr();
		String ip =getIpAddr(request);
		String allowIps = JiveGlobals.getProperty(ALLOW_IP_LIST_KEY);
		String ischekip=JiveGlobals.getProperty(IS_NEED_IP_CHECK);
		boolean allow = false;
		
		//LOGGER.info("ischekip--" + ischekip);
		//LOGGER.info("ip--" + ip);
		if(ischekip!=null&&!ischekip.toLowerCase().equals("true")){
			return true;
		}

		if (allowIps != null) {
			//LOGGER.info("allowIps--" + allowIps);
			/* 例如: 192.168.1.102;192.168.1.104 */
			StringTokenizer tokenizer = new StringTokenizer(allowIps, ";");
			while (tokenizer.hasMoreTokens()) {
				if (ip.equals(tokenizer.nextToken())) {
					allow = true;
					break;
				}
			}
		}
		//LOGGER.info("allow--" + allow);
		return allow;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/**
		 * 接收来自客户端的post请求， 从请求中取得XMPP数据包，并将数据包通过PacketSender发送出去。
		 */
		String rawPacket = getRawPacketContent(request);
		Packet packet = createPacket(rawPacket);
		if (doCheck(request)&&packet != null && sendPacket(packet)) {
			finish(response, HttpServletResponse.SC_OK, "ok");
		} else {
			finish(response, HttpServletResponse.SC_BAD_REQUEST, "invalid xmpp data.");
		}
	}

	/**
	 * 获得原始的XMPP包内容。
	 *
	 * @param request
	 * @return
	 */
	private String getRawPacketContent(HttpServletRequest request) {
		//String packetContent = request.getParameter(PACKET_CONTENT_PARAMETER_NAME);
		String packetContent = request.getParameter(PACKET_CONTENT_PARAMETER_NAME);
		System.out.println("packetContent--" + packetContent);
		LOGGER.info("packetContent--" + packetContent);
		return packetContent != null ? packetContent.trim() : null;
	}

	/**
	 * 用原始的packet xml文本创建包对象实例。
	 *
	 * @param rawPacket
	 * @return
	 */
	private Packet createPacket(String rawPacket) {
		if (rawPacket == null) {
			return null;
		}

		Element element;
		try {
			/* 将内容主题分析为XML元素 */
			element = DocumentHelper.parseText(rawPacket).getRootElement();
		} catch (DocumentException e) {
			LOGGER.error("parse xml fail", e);
			return null;
		}

		if ("message".equals(element.getName())) {
			return new Message(element);
		} else if ("iq".equals(element.getName())) {
			return new IQ(element);
		} else if ("presence".equals(element.getName())) {
			return new Presence(element);
		} else {
			return null;
		}
	}

	/**
	 * 完成当前请求，并设置响应状态信息。
	 *
	 * @param response
	 * @param status
	 * @param message
	 * @throws IOException
	 */
	protected void finish(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		if (message != null) {
			try {
				response.getWriter().write(message);
				response.getWriter().flush();
			} finally {
				response.getWriter().close();
			}
		}
	}

	/**
	 * 发送数据包。
	 *
	 * @param packet
	 * @return
	 */
	private boolean sendPacket(Packet packet) {
		try {
			//LOGGER.info("sendPacket--" + packet.toXML());
			sender.send(packet);
			return true;
		} catch (Exception exp) {
			LOGGER.error("send packet fail.", packet);
		}
		return false;
	}

	// public void pushAppMsg(String appid, String appName, String body, String
	// title, int MessageType,
	// List<String> toUsers) throws PublishException {
	//
	// PushInfrastructure factory = new XMPPPushInfrastructure("skysea.com", /*
	// xmppdomain */
	// "http://localhost:9090/plugins/push/packet" /* pushGatewayUrl */);
	//
	// /* 获得事件发布器(可以单例保存) */
	// MessagePublisher messagePublisher = factory.getMessagePublisher();
	// HashMap<String, String> arti = new HashMap<String, String>();
	//
	// // baseRequest
	// AppPushEntity.BaseRequestEntity baseRequest = new
	// AppPushEntity.BaseRequestEntity();
	// baseRequest.setDeviceID("");
	// baseRequest.setToken(appid);
	// baseRequest.setUid("");
	//
	// List<AppPushEntity.ObjectContentEntity.BodyEntity> bodys = new
	// ArrayList<AppPushEntity.ObjectContentEntity.BodyEntity>();
	//
	// AppPushEntity.ObjectContentEntity.BodyEntity bodyEntity1 = new
	// AppPushEntity.ObjectContentEntity.BodyEntity();
	// bodyEntity1.setContent(body);
	// bodys.add(bodyEntity1);
	//
	// AppPushEntity.ObjectContentEntity.HeadEntity headEntity1 = new
	// AppPushEntity.ObjectContentEntity.HeadEntity();
	// headEntity1.setContent(title);
	// headEntity1.setPubTime(System.currentTimeMillis());
	//
	// AppPushEntity.ObjectContentEntity objectContent = new
	// AppPushEntity.ObjectContentEntity();
	// objectContent.setBody(bodys);
	// objectContent.setAppId(appid);
	// objectContent.setHead(headEntity1);
	//
	// List<String> sessions = new ArrayList<String>();
	// sessions.add("all");
	//
	// AppPushEntity entity = new AppPushEntity();
	// entity.setBaseRequest(baseRequest);
	// entity.setContent(title);
	// entity.setExpire(3600000L);
	// entity.setMsgType(MessageType);
	// entity.setObjectContent(objectContent);
	// entity.setSessions(sessions);
	// entity.setStatusId("9999");
	//
	// List<String> touserNames = new ArrayList<String>();
	// List<String> touserIds = new ArrayList<String>();
	// for (String name : toUsers) {
	// String id = sendGet("http://localhost/app/client/device/getUserIdByName/"
	// + name);
	// if (!id.equals("")) {
	// touserNames.add(id + "@user");
	// touserIds.add(id);
	// }
	// }
	//
	// entity.setToUserNames(touserNames);
	//
	// arti.put("app", new Gson().toJson(entity));
	// for (String uid : touserIds) {
	// Message msg = new Message();
	// msg.setType(Message.Type.headline);
	// msg.setFrom(newJidForUser(appid));
	// /* 如果消息不是广播，则要设置收件人jid。 */
	// msg.setTo(newJidForUser(uid));
	// msg.setBody(new Gson().toJson(arti));
	// String message = msg.toXML();
	// }
	//
	// }

	public JID newJidForUser(String user) {
		return new JID(user, "skysea.com", null);
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
}
