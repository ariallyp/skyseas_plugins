package com.skyseas.openfireplugins.pushofflinemsg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Element;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.disco.IQDiscoInfoHandler;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.PacketError.Condition;
import org.xmpp.resultsetmanagement.ResultSet;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;

import javapns.communication.exceptions.KeystoreException;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.transmission.NotificationProgressListener;
import javapns.notification.transmission.NotificationThread;
import javapns.notification.transmission.NotificationThreads;
import nsp.NSPClient;
import nsp.OAuth2Client;
import nsp.support.common.AccessToken;
import nsp.support.common.NSPException;

import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

public class OfflinePushPlugin implements Component, Plugin, PropertyEventListener, PacketInterceptor {

	private static final Logger Log = LoggerFactory.getLogger(OfflinePushPlugin.class);

	
	
	private final static String INSERT_PUSHTOKEN = "INSERT INTO sky_offlinepushtoken (os,jid,username,token,brand) "
			+ "VALUES (?,?,?,?,?)";
	private final static String DELETE_PUSHTOKEN = "DELETE FROM `sky_offlinepushtoken` WHERE username = ?";

	private final static String SELECT_PUSHTOKEN = "SELECT `id`, `os`,`jid`,`username`,`token`,`brand`  FROM `sky_offlinepushtoken`";

	public static final String NAMESPACE_JABBER_IQ_TOKEN_BIND = "jabber:iq:token:bind";
	public static final String NAMESPACE_JABBER_IQ_TOKEN_UNBUND = "jabber:iq:token:unbind";

	public static final String SERVICENAME = "plugin.offlinepush.serviceName";
	public static final String SERVICEENABLED = "plugin.offlinepush.serviceEnabled";

	public final static String CERT_HOME = "offlinepush.cert_home";
	public final static String CERT_ANDROID_HUAWEI_PWD = "offlinepush.cert_android_huawei_pwd";
	public final static String CERT_ANDROID_HUAWEI_APPID = "offlinepush.cert_android_huawei_appid";
	public final static String CERT_ANDROID_HUAWEI_APPKEY = "offlinepush.cert_android_huawei_appkey";
	public final static String OFFLINEPUSH_ANDROID_ENABLED = "offlinepush.android_enabled";
	public final static String CERT_ANDROID_HUAWEI_NAME = "offlinepush.cert_android_huawei_name";

	public final static String ANDROID_APP_PACKAGE_NAME = "offlinepush.android_app_package_name";
	public final static String CERT_ANDROID_XIAOMI_APPKEY = "offlinepush.cert_android_xiaomi_appkey";

	// public final static String CERT_IOS_HUAWEI_APPID =
	// "offlinepush.cert_ios_appid";
	public final static String CERT_IOS_APPKEY = "offlinepush.cert_ios_appkey";
	public final static String OFFLINEPUSH_IOS_ENABLED = "offlinepush.ios_enabled";

	public final static String CERT_IOS_NAME = "offlinepush.cert_ios_name";
	public final static String CERT_IOS_PWD = "offlinepush.cert_ios_pwd";
	public final static String CERT_IOS_IS_PRODUCTION = "offlinepush.cert_ios_is_production";

	private ComponentManager componentManager;
	private PluginManager pluginManager;
	
	private NewHuaWeiPush newHuaWeiPush ;

	private String serviceName;

	// 推送证书存放目录（IOS、Android）
	private static String cert_Home;

	// 华为推送token
	private String huaweiToken = null;

	// 华为推送token过期时间
	private long huaweiTokenExpire = System.currentTimeMillis();

	public static String getCert_Home() {
		return cert_Home;
	}

	public static void setCert_Home(String cert_Home) {
		JiveGlobals.setProperty(CERT_HOME, cert_Home);
		OfflinePushPlugin.cert_Home = cert_Home;
	}

	// android app包名
	private String android_app_package_name;

	public String getAndroid_app_package_name() {
		return android_app_package_name;
	}

	public void setAndroid_app_package_name(String android_app_package_name) {
		JiveGlobals.setProperty(ANDROID_APP_PACKAGE_NAME, android_app_package_name);
		this.android_app_package_name = android_app_package_name;
	}

	private static Map<String, Device> map = new ConcurrentHashMap<String, Device>(20);
	private static Map<String, Integer> count = new ConcurrentHashMap<String, Integer>(20);
	private static List<String> groupMsgIdlist = new ArrayList<String>(20);

	/// ###开始IOS相关蚕食定义#######################################
	private boolean serviceEnabled;

	// IOS推送证书名称
	private String cert_Ios_Name;
	// IOS推送证书密码
	private String cert_Ios_Pwd;
	// 是否允许IOS推送
	private boolean ios_Enabled;

	// IOS推送证书是否为生产环境 默认为生产环境true
	private boolean cert_ios_is_production;

	public boolean isCert_ios_is_production() {
		return cert_ios_is_production;
	}

	public void setCert_ios_is_production(boolean cert_ios_is_production) {
		this.cert_ios_is_production = cert_ios_is_production;
		JiveGlobals.setProperty(CERT_IOS_IS_PRODUCTION, cert_ios_is_production ? "true" : "false");
	}

	// IOS推送服务对象
	private static AppleNotificationServer appleServer = null;
	private static List<PayloadPerDevice> list;

	public String getCert_Ios_Name() {
		return cert_Ios_Name;
	}

	public void setCert_Ios_Name(String cert_Ios_Name) {
		JiveGlobals.setProperty(CERT_IOS_NAME, cert_Ios_Name);
		this.cert_Ios_Name = cert_Ios_Name;
	}

	public String getCert_Ios_Pwd() {
		return cert_Ios_Pwd;
	}

	public void setCert_Ios_Pwd(String cert_Ios_Pwd) {
		JiveGlobals.setProperty(CERT_IOS_PWD, cert_Ios_Pwd);
		this.cert_Ios_Pwd = cert_Ios_Pwd;
	}

	public boolean getIos_Enabled() {
		return ios_Enabled;
	}

	public void setIos_Enabled(boolean enabled) {
		this.ios_Enabled = enabled;
		JiveGlobals.setProperty(OFFLINEPUSH_IOS_ENABLED, enabled ? "true" : "false");
	}
	/// ###结束IOS相关蚕食定义#######################################

	/// ###开始android 华为sdk推送相关蚕食定义#######################################

	// android 华为推送证书名称
	private String cert_android_huawei_name;
	// android 华为推送证书密码
	private String cert_android_huawei_pwd;
	// 是否允许android 华为 推送
	private boolean android_enabled;

	// 华为 推送应用程序id
	private String android_huawei_appid;

	// 华为 推送应用程序key
	private String android_huawei_appkey;

	public String getCert_Android_Huawei_Name() {
		return cert_android_huawei_name;
	}

	public void setCert_Android_Huawei_Name(String cert_android_huawei_name) {
		this.cert_android_huawei_name = cert_android_huawei_name;
	}

	public String getCert_Android_Huawei_Pwd() {
		return cert_android_huawei_pwd;
	}

	public void setCert_Android_Auawei_Pwd(String cert_android_huawei_pwd) {
		JiveGlobals.setProperty(CERT_ANDROID_HUAWEI_PWD, cert_android_huawei_pwd);
		this.cert_android_huawei_pwd = cert_android_huawei_pwd;
	}

	public boolean getAndroid_Enabled() {
		return android_enabled;
	}

	public void setAndroid_Enabled(boolean android_enabled) {
		JiveGlobals.setProperty(OFFLINEPUSH_ANDROID_ENABLED, android_enabled ? "true" : "false");
		this.android_enabled = android_enabled;
	}

	public String getAndroid_Huawei_Appid() {
		return android_huawei_appid;
	}

	public void setAndroid_Huawei_Appid(String android_huawei_appid) {
		JiveGlobals.setProperty(CERT_ANDROID_HUAWEI_APPID, android_huawei_appid);
		this.android_huawei_appid = android_huawei_appid;
	}

	public String getAndroid_Huawei_Appkey() {
		return android_huawei_appkey;
	}

	public void setAndroid_Huawei_Appkey(String android_huawei_appkey) {
		JiveGlobals.setProperty(CERT_ANDROID_HUAWEI_APPKEY, android_huawei_appkey);
		this.android_huawei_appkey = android_huawei_appkey;
	}

	/// ###结束android 华为sdk推送相关蚕食定义#######################################

	/// ###开始android 小米sdk推送相关蚕食定义#######################################

	// android 小米推送证书key
	private String cert_android_xiaomi_key;

	public String getCert_android_xiaomi_key() {
		return cert_android_xiaomi_key;
	}

	public void setCert_android_xiaomi_key(String cert_android_xiaomi_key) {
		JiveGlobals.setProperty(CERT_ANDROID_XIAOMI_APPKEY, cert_android_xiaomi_key);
		this.cert_android_xiaomi_key = cert_android_xiaomi_key;
	}

	/// ###结束android 小米sdk推送相关蚕食定义#######################################

	public OfflinePushPlugin() {
		serviceName = JiveGlobals.getProperty(SERVICENAME, "offlinepush");
		serviceEnabled = JiveGlobals.getBooleanProperty(SERVICEENABLED, true);
	}

	@Override
	public void xmlPropertySet(String property, Map<String, Object> params) {

	}

	@Override
	public void xmlPropertyDeleted(String property, Map<String, Object> params) {
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {

		cert_Home = JiveGlobals.getProperty(CERT_HOME, "");
		if (cert_Home.equals("")) {
			setCert_Home("/var/lib/openfire/plugins/pushofflinemsg/resource/");
		}

		/// ######开始获取IOS推送相关参数信息###########################
		cert_Ios_Name = JiveGlobals.getProperty(CERT_IOS_NAME, "");
		// If no secret key has been assigned to the user service yet, assign a
		// random one.
		if (cert_Ios_Name.equals("")) {
			setCert_Ios_Name("DevPush.p12");
		}
		String iosCertPath = cert_Home + cert_Ios_Name;
		Log.info("iosCertPath ============="+iosCertPath);
		cert_Ios_Pwd = JiveGlobals.getProperty(CERT_IOS_PWD, "");
		if (cert_Ios_Pwd.equals("")) {
			setCert_Ios_Pwd("pengrui0912");
		}
		if (JiveGlobals.getProperty(OFFLINEPUSH_IOS_ENABLED, "") == "") {
			ios_Enabled = JiveGlobals.getBooleanProperty(OFFLINEPUSH_IOS_ENABLED, false);
			this.setIos_Enabled(ios_Enabled);
		} else {
			ios_Enabled = JiveGlobals.getBooleanProperty(OFFLINEPUSH_IOS_ENABLED);
		}

		if (JiveGlobals.getProperty(CERT_IOS_IS_PRODUCTION, "") == "") {
			cert_ios_is_production = JiveGlobals.getBooleanProperty(CERT_IOS_IS_PRODUCTION, true);
			this.setCert_ios_is_production(cert_ios_is_production);
		} else {
			cert_ios_is_production = JiveGlobals.getBooleanProperty(CERT_IOS_IS_PRODUCTION);
		}

		/// ######结束获取IOS推送相关参数信息###########################

		/// ######开始获取android 华为推送相关参数信息###########################

		cert_android_huawei_pwd = JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_PWD, "");
		if (cert_android_huawei_pwd.equals("")) {
			this.setCert_Android_Auawei_Pwd("123456");
		}
		android_huawei_appid = JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_APPID, "");// "10793221";
		if (android_huawei_appid.equals("")) {
			this.setAndroid_Huawei_Appid("10776817");
		}
		android_huawei_appkey = JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_APPKEY, "");// "a032c305631ca3a24ca3692d8380067d";
		if (android_huawei_appkey.equals("")) {
			this.setAndroid_Huawei_Appkey("c566135546298108a4fd4145a1e6421d");
		}
		cert_android_huawei_name = JiveGlobals.getProperty(CERT_ANDROID_HUAWEI_NAME, "");// "mykeystorebj.jks"
		if (cert_android_huawei_name.equals("")) {
			this.setCert_Android_Huawei_Name("mykeystorebj.jks");
		}

		if (JiveGlobals.getProperty(OFFLINEPUSH_ANDROID_ENABLED, "") == "") {
			android_enabled = JiveGlobals.getBooleanProperty(OFFLINEPUSH_ANDROID_ENABLED, false);
			this.setAndroid_Enabled(android_enabled);
		} else {
			android_enabled = JiveGlobals.getBooleanProperty(OFFLINEPUSH_ANDROID_ENABLED);
		}

		/// ######结束获取android 华为推送相关参数信息###########################

		/// ######开始获取android 小米推送相关参数信息###########################
		cert_android_xiaomi_key = JiveGlobals.getProperty(CERT_ANDROID_XIAOMI_APPKEY, "");//
		if (cert_android_xiaomi_key.equals("")) {
			this.setCert_android_xiaomi_key("Vexzeg1a0T9i308Bgj8JDQ==");
		}
		Log.info("cert_android_xiaomi_key :"+cert_android_xiaomi_key);
		
		
		/// ######结束获取android 小米推送相关参数信息###########################

		android_app_package_name = JiveGlobals.getProperty(ANDROID_APP_PACKAGE_NAME, "");
		if (android_app_package_name.equals("")) {
			this.setAndroid_app_package_name("com.wiz.dev.wiztalk");
		}
		Log.info("iosCertPath: " + iosCertPath);
		Log.info("cert_Ios_Pwd: " + cert_Ios_Pwd);
		Log.info("ios_Enabled: " + ios_Enabled);

		try {
			appleServer = new AppleNotificationServerBasicImpl(iosCertPath, cert_Ios_Pwd, cert_ios_is_production);
			if (list == null) {
				list = new ArrayList<PayloadPerDevice>();
			}

		} catch (KeystoreException e1) {
			Log.error("KeystoreException: " + e1.getMessage());
		}

		List<Device> devicelist = getPushtokens();
		Log.info("token number：" + devicelist.size());
		for (Device device : devicelist) {
			if (!map.containsKey(device.getUser())) {
				map.put(device.getUser().toLowerCase(), device);
			}
		}

		pluginManager = manager;

		componentManager = ComponentManagerFactory.getComponentManager();
		try {
			componentManager.addComponent(serviceName, this);
		} catch (ComponentException e) {
			Log.error(e.getMessage(), e);
		}

		InterceptorManager.getInstance().addInterceptor(this);
		PropertyEventDispatcher.addListener(this);

	}

	@Override
	public void destroyPlugin() {
		InterceptorManager.getInstance().removeInterceptor(this);
		PropertyEventDispatcher.removeListener(this);
		pluginManager = null;
		try {
			componentManager.removeComponent(serviceName);
			componentManager = null;
		} catch (Exception e) {
			if (componentManager != null) {
				Log.error(e.getMessage(), e);
			}
		}
		serviceName = null;
	}

	@Override
	public String getName() {
		return pluginManager.getName(this);
	}

	@Override
	public String getDescription() {
		return pluginManager.getDescription(this);
	}

	@Override
	public void processPacket(Packet p) {
		if (!(p instanceof IQ)) {
			return;
		}
		final IQ packet = (IQ) p;

		if (packet.getType().equals(IQ.Type.error) || packet.getType().equals(IQ.Type.result)) {
			return;
		}
		final IQ replyPacket = handleIQRequest(packet);

		try {
			componentManager.sendPacket(this, replyPacket);
		} catch (ComponentException e) {
			Log.error(e.getMessage(), e);
		}
	}

	private IQ handleIQRequest(IQ iq) {
		final IQ replyPacket; // 'final' to ensure that it is set.
		Log.info("Push IQ:" + iq.toXML());
		final IQ.Type type = iq.getType();
		if (type != IQ.Type.get && type != IQ.Type.set) {
			throw new IllegalArgumentException("Argument 'iq' must be of type 'get' or 'set'");
		}

		final Element childElement = iq.getChildElement();

		if (childElement == null) {
			replyPacket = IQ.createResultIQ(iq);
			replyPacket.setError(new PacketError(Condition.bad_request, org.xmpp.packet.PacketError.Type.modify,
					"IQ stanzas of type 'get' and 'set' MUST contain one and only one child element (RFC 3920 section 9.2.3)."));
			return replyPacket;
		}

		final String namespace = childElement.getNamespaceURI();

		if (namespace == null) {
			replyPacket = IQ.createResultIQ(iq);
			replyPacket.setError(Condition.feature_not_implemented);
			return replyPacket;
		}

		if (namespace.equals(NAMESPACE_JABBER_IQ_TOKEN_BIND)) {
			replyPacket = processSetUUID(iq, true);
		} else if (namespace.equals(NAMESPACE_JABBER_IQ_TOKEN_UNBUND)) {
			replyPacket = processSetUUID(iq, false);
		} else if (namespace.equals(IQDiscoInfoHandler.NAMESPACE_DISCO_INFO)) {
			replyPacket = handleDiscoInfo(iq);
		} else {
			replyPacket = IQ.createResultIQ(iq);
			replyPacket.setError(Condition.feature_not_implemented);
		}

		return replyPacket;
	}

	private static IQ handleDiscoInfo(IQ iq) {
		if (iq == null) {
			throw new IllegalArgumentException("Argument 'iq' cannot be null.");
		}

		if (!iq.getChildElement().getNamespaceURI().equals(IQDiscoInfoHandler.NAMESPACE_DISCO_INFO)
				|| iq.getType() != Type.get) {
			throw new IllegalArgumentException("This is not a valid disco#info request.");
		}

		final IQ replyPacket = IQ.createResultIQ(iq);

		final Element responseElement = replyPacket.setChildElement("query", IQDiscoInfoHandler.NAMESPACE_DISCO_INFO);
		responseElement.addElement("identity").addAttribute("category", "directory").addAttribute("type", "user")
				.addAttribute("name", "Offline Push");
		responseElement.addElement("feature").addAttribute("var", NAMESPACE_JABBER_IQ_TOKEN_BIND);
		responseElement.addElement("feature").addAttribute("var", IQDiscoInfoHandler.NAMESPACE_DISCO_INFO);
		responseElement.addElement("feature").addAttribute("var", ResultSet.NAMESPACE_RESULT_SET_MANAGEMENT);

		return replyPacket;
	}

	private IQ processSetUUID(IQ packet, boolean isSet) {
		Element rsmElement = null;
		if (!packet.getType().equals(IQ.Type.set)) {
			throw new IllegalArgumentException("This method only accepts 'set' typed IQ stanzas as an argument.");
		}

		final IQ resultIQ;

		final Element incomingForm = packet.getChildElement();

		resultIQ = IQ.createResultIQ(packet);
		if (incomingForm != null) {
			rsmElement = incomingForm.element("info");

			String osElement = rsmElement.attributeValue("os");
			String jidElement = rsmElement.attributeValue("jid");

			String username = rsmElement.attributeValue("user").toLowerCase();// new

			if (osElement == null || jidElement == null) {
				resultIQ.setError(Condition.bad_request);
				return resultIQ;
			}

			if (username == null || username.equals("")) {
				String[] arr = jidElement.split("@");
				username = arr[0];
			}

			if (isSet) {
				Device device = new Device();
				device.setJid(jidElement);
				device.setToken(rsmElement.attributeValue("token"));
				device.setOS(rsmElement.attributeValue("os"));
				device.setBrand(rsmElement.attributeValue("brand"));
				device.setUser(username);
				map.put(username, device);
				count.put(username, 0);
				try {
					this.deletePushToken(username);
					this.addPushToken(device);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.info("set token,username:" + username + " ,token:" + device.getToken());
			} else {
				map.remove(username);
				count.remove(username);
				try {
					Log.info("delete token,username:" + username);
					
					this.deletePushToken(username);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.info("remove token,username:" + username);
			}
		} else {
			resultIQ.setError(Condition.bad_request);
		}

		return resultIQ;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String name) {
		JiveGlobals.setProperty(SERVICENAME, name);
	}

	public boolean getServiceEnabled() {
		return serviceEnabled;
	}

	public void setServiceEnabled(boolean enabled) {
		serviceEnabled = enabled;
		JiveGlobals.setProperty(SERVICEENABLED, enabled ? "true" : "false");
	}

	public void propertySet(String property, Map<String, Object> params) {
		if (property.equals(SERVICEENABLED)) {
			this.serviceEnabled = Boolean.parseBoolean((String) params.get("value"));
		}
		if (property.equals(CERT_HOME)) {
			this.cert_Home = (String) params.get("value");
		} else if (property.equals(OFFLINEPUSH_IOS_ENABLED)) {
			this.ios_Enabled = Boolean.parseBoolean((String) params.get("value"));
		} else if (property.equals(CERT_IOS_PWD)) {
			this.cert_Ios_Pwd = (String) params.get("value");
		} else if (property.equals(CERT_ANDROID_HUAWEI_PWD)) {
			this.cert_android_huawei_pwd = (String) params.get("value");
		} else if (property.equals(CERT_ANDROID_HUAWEI_APPID)) {
			this.android_huawei_appid = (String) params.get("value");
		} else if (property.equals(CERT_ANDROID_HUAWEI_APPKEY)) {
			this.android_huawei_appkey = (String) params.get("value");
		} else if (property.equals(OFFLINEPUSH_ANDROID_ENABLED)) {
			this.android_enabled = (Boolean) params.get("value");
		} else if (property.equals(CERT_ANDROID_HUAWEI_NAME)) {
			this.cert_android_huawei_name = (String) params.get("value");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.util.PropertyEventListener#propertyDeleted(java.lang.
	 * String, java.util.Map)
	 */
	public void propertyDeleted(String property, Map<String, Object> params) {
		if (property.equals(SERVICEENABLED)) {
			this.serviceEnabled = true;
		}
		if (property.equals(CERT_HOME)) {
			this.cert_Home = "delementtest.p12";
		} else if (property.equals(OFFLINEPUSH_IOS_ENABLED)) {
			this.ios_Enabled = false;
		} else if (property.equals(CERT_IOS_PWD)) {
			this.cert_Ios_Pwd = "123456";
		} else if (property.equals(CERT_ANDROID_HUAWEI_PWD)) {
			this.cert_android_huawei_pwd = "123456";
		} else if (property.equals(CERT_ANDROID_HUAWEI_APPID)) {
			this.android_huawei_appid = "10793221";
		} else if (property.equals(CERT_ANDROID_HUAWEI_APPKEY)) {
			this.android_huawei_appkey = "a032c305631ca3a24ca3692d8380067d";
		} else if (property.equals(OFFLINEPUSH_ANDROID_ENABLED)) {
			this.android_enabled = false;
		} else if (property.equals(CERT_ANDROID_HUAWEI_NAME)) {
			this.cert_android_huawei_name = "mykeystorebj.jks";
		}

	}

	@Override
	public void initialize(JID jid, ComponentManager componentManager) throws ComponentException {

	}

	@Override
	public void start() {

	}

	@Override
	public void shutdown() {

	}

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {
		if (processed && incoming) {
			if (packet instanceof Message) {
				if (((Message) packet).getBody() == null) {
					return;
				}
				Packet copyPacket = packet.createCopy();
				Message message = (Message) copyPacket;
				if (message.getType() == Message.Type.groupchat) {
					JID jid = packet.getFrom();
					// 获取用户的设备标志id
					List<Device> deviceList = getGroupMembersTokens(jid.getNode().toLowerCase());
					String groupName = message.getElement().element("msgExtension").attributeValue("displayName");
					String id = message.getID();
					//System.out.println("群组jid:" + jid.getNode().toLowerCase() + message.toXML() + "群组jid:" + id);
					Log.info("group jid:" + jid.getNode().toLowerCase() + message.toXML() + "group jid:" + id);
					if (groupMsgIdlist.contains(id)) {
						groupMsgIdlist.remove(id);
						return;
					}
					groupMsgIdlist.add(id);
					for (Device device : deviceList) {
						
						  if (device.getOS().equals("0")&&device.getUser().toCharArray().equals(jid.getNode().toLowerCase())) {
						  continue; }
						 
						JID j = new JID(device.getJid());
						copyPacket.setTo(j);
						pushSingleOfflineMsg(copyPacket, device, groupName);
					}
				} else {
					//System.out.println("message:" + message.toXML());
					Log.info("message:" + message.toXML());
					String nickName = message.getElement().element("msgExtension").attributeValue("displayName");
					JID jid = packet.getTo();
					// 获取用户的设备标志id
					Device device = map.get(jid.getNode().toLowerCase());
					//System.out.println(device == null ? "空对象" : device.toString());
					Log.info(device == null ? "空对象" : device.toString());
					pushSingleOfflineMsg(packet, device, nickName);
				}
			}
		}
	}

	public void addPushToken(Device device) {
		assert device != null;
		Connection con = null;
		PreparedStatement pstmt = null;
		java.sql.ResultSet rs = null;

		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(INSERT_PUSHTOKEN, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, device.getOS());
			pstmt.setString(2, device.getJid());
			pstmt.setString(3, device.getUser().toLowerCase());
			pstmt.setString(4, device.getToken());
			pstmt.setString(5, device.getBrand());

			if (pstmt.executeUpdate() > 0) {
				rs = pstmt.getGeneratedKeys();
				rs.next();

			}
		} catch (SQLException e) {
			Log.error("推送token写入数据库失败", e);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
	}

	public boolean deletePushToken(String userName) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean success = false;

		try {
			con = DbConnectionManager.getConnection();
			con.setAutoCommit(false);

			pstmt = con.prepareStatement(DELETE_PUSHTOKEN);
			pstmt.setString(1, userName);
			success = pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			Log.error("删除token失败", e);
		} finally {
			try {
				if (success) {
					con.commit();
				} else {
					con.rollback();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			DbConnectionManager.closeConnection(pstmt, con);
		}

		return success;
	}

	// 根据推送登录设备（token）列表
	private List<Device> getPushtokens() {
		Connection con = null;
		PreparedStatement pstmt = null;
		java.sql.ResultSet rs = null;
		List<Device> devicelist = new ArrayList<Device>();
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SELECT_PUSHTOKEN);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Device device = new Device();
				device.setOS(rs.getString("os"));
				device.setJid(rs.getString("jid"));
				device.setBrand(rs.getString("brand"));
				device.setToken(rs.getString("token"));
				device.setUser(rs.getString("username"));
				devicelist.add(device);
			}
			return devicelist;
		} catch (Exception e) {
			Log.error("获取推送token失败", e);
			return devicelist;
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
	}

	// 根据groupid获取群组成员推送登录设备（token）列表
	private List<Device> getGroupMembersTokens(String groupid) {
		Connection con = null;
		PreparedStatement pstmt = null;
		java.sql.ResultSet rs = null;
		List<Device> devicelist = new ArrayList<Device>();
		try {
			Log.info("groupid：" + groupid);
			String sql = "SELECT `id`,`groupId`,`userName`,`nickName`,`joinTime` FROM sky_GroupMembers where groupId= '"
					+ groupid + "'";
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String userid = rs.getString("userName").toLowerCase();
				if (map.containsKey(userid)) {
					devicelist.add(map.get(userid));
				}
			}
			Log.info("群组token数量：" + devicelist.size());
			return devicelist;
		} catch (Exception e) {
			Log.error("获取群组用户推送信息token失败", e);
			return devicelist;
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
	}

	/// 推送单条消息
	private void pushSingleOfflineMsg(Packet packet, Device device, String title) {

		JID jid = packet.getTo();

		if (device != null) {
			String uuid = device.getToken();
			if (uuid != null && !"".equals(uuid)) {
				User user = null;
				try {
					user = XMPPServer.getInstance().getUserManager().getUser(jid.getNode());
				} catch (UserNotFoundException e2) {
					e2.printStackTrace();
				}
				PresenceManager presenceManager = XMPPServer.getInstance().getPresenceManager();
				org.xmpp.packet.Presence presence = presenceManager.getPresence(user);
				
				Log.info("333333:" + (presence != null ? "online" : "offline") + "##brand:" + device.getBrand());
				if (presence == null || !presence.isAvailable()) {

					Log.info("offline msg push");
					String body = ((Message) packet).getBody();
					Log.info("body : "+body);
					String mold = ((Message) packet).getElement().element("msgExtension").attributeValue("mold");
					Log.info("mold : "+mold);
					if (mold.equals("10") || mold.equals("0x214") || mold.equals("222") || mold.equals("0x925")
							|| mold.equals("0x924") || mold.equals("819") || mold.equals("404")) {
						return;
					}
					if (mold.equals("9")) {
						body = "[文件]";
					} else if (mold.equals("530")) {
						body = "[位置]";
					} else if (mold.equals("980")) {
						body = "[名片]";
					} else if (mold.equals("528")) {
						body = "[分享]";
					} else if (mold.equals("104") || mold.equals("102") || mold.equals("105")) {
						body = ((Message) packet).getElement().element("body").getText();
						try {
							//Log.info("RUN IN  ============102-104-105");
							JSONObject obj = new JSONObject(body);
							String jsonstr = obj.getString("app").replace("\\\"", "\"");
							JSONObject jsonObj = new JSONObject(jsonstr);
							JSONArray jsonArr = jsonObj.getJSONObject("objectContent").getJSONArray("body");
							String content = "";
							for (int i = 0; i < jsonArr.length(); i++) {
								jsonObj = (JSONObject) jsonArr.get(i);
								content += jsonObj.getString("content") + System.getProperty("line.separator");
							}
							body = content;

						} catch (JSONException e) {
							Log.error(e.getMessage());
						}
					} else if (mold.equals("103")) {
						body = ((Message) packet).getElement().element("body").getText();
						try {
							JSONObject obj = new JSONObject(body);
							String jsonstr = obj.getString("app").replace("\\\"", "\"");
							JSONObject jsonObj = new JSONObject(jsonstr);
							body = jsonObj.getString("content");

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					//Log.info("RUN IN  ============OFFLINE");
					pushOfflineMsg(device, body, jid, title);

				}
			}
		}
	}

	private void pushOfflineMsg(Device device, String pushCont, JID jid, String title) {
		Log.info("push to offline message: ========================" );
		//PushMessageHttp.httpURLConnectionPOST(device.getUser(), pushCont, title);//使用推送中心推送
		if (device.getOS().equals("0") && this.getAndroid_Enabled()) {
			Integer size = 1;
			if (count.containsKey(jid.getNode())) {
				size = count.get(jid.getNode()) + 1;
			}
			if (size <= 1000)
				count.put(jid.getNode(), size);
			title += " (有" + size + "条未读消息)";
			try {
				pushAndroidOfflineMsg(device, pushCont, jid, title);
			} catch (IOException e) {
				Log.error(e.getMessage());
			}
		} else if (device.getOS().equals("1") && this.getIos_Enabled()) {
			pushAppleOfflineMsg(device, pushCont, jid, title);
		}
	}

	private void pushAppleOfflineMsg(Device device, String pushCont, JID jid, String title) {
		Log.info("push to offline message FOR IOS : ========================" );
		NotificationThreads work = null;
		try {
			Integer size = 0;
			if (count.containsKey(jid.getNode())) {
				size = count.get(jid.getNode()) + 1;
			}
			if (size <= 1000)
				count.put(jid.getNode(), size);
			List<PayloadPerDevice> list = new ArrayList<PayloadPerDevice>();
			PushNotificationPayload payload = new PushNotificationPayload();
			String context = title + ":" + pushCont;
			if ((context).length()>=45) {
				context=context.substring(0,40)+"...";
			}
			payload.addAlert(context);
			payload.addSound("default");
			payload.addBadge(size);
			payload.addCustomDictionary("jid", jid.toString());
			PayloadPerDevice pay = new PayloadPerDevice(payload, device.getToken());
			list.add(pay);
			work = new NotificationThreads(appleServer, list, 1);
			work.setListener(DEBUGGING_PROGRESS_LISTENER);
			work.start();
		} catch (JSONException e) {
			Log.error("JSONException:" + e.getMessage());
		} catch (InvalidDeviceTokenFormatException e) {
			Log.error("InvalidDeviceTokenFormatException:" + e.getMessage());
		} finally {
			//Log.info("push to apple: username: " + jid.getNode() + " ,context" + pushCont);
			work.destroy();
			
		}
	}

	private void pushAndroidOfflineMsg(Device device, String pushCont, JID jid, String title) throws IOException {
		if (device.getBrand().toLowerCase().equals("huawei")) {
			//pushAndroidOfflineMsgUseHuaweiSDK(device, pushCont, jid, title);
			newHuaWeiPush.sendPushMessage(device, pushCont, jid, title);//replace new huaiwei push
			
		} else {
			pushAndroidOfflineMsgUseXiaomiSDK(device, pushCont, jid, title);
		}

	}

	private void pushAndroidOfflineMsgUseHuaweiSDK(Device device, String pushCont, JID jid, String title) {
		try {
			OAuth2Client oauth2Client = new OAuth2Client();
			/// var/lib/openfire/plugins/pushofflinemsg/resource/
			//Log.info("huawei push==start");
			String certpath = this.cert_Home + this.getCert_Android_Huawei_Name();
			//Log.info("certpath"+certpath);
			oauth2Client.initKeyStoreStream(new FileInputStream(certpath), this.getCert_Android_Huawei_Pwd());

			AccessToken access_token = oauth2Client.getAccessToken("client_credentials", this.getAndroid_Huawei_Appid(),
					this.getAndroid_Huawei_Appkey());

			if (System.currentTimeMillis() >= huaweiTokenExpire) {
				huaweiToken = access_token.getAccess_token();
				huaweiTokenExpire = System.currentTimeMillis() + access_token.getExpires_in() * 1000;
				
				//Log.info("time passed "+"access token :" + huaweiToken + ",expires time[access token 过期时间]:" + huaweiTokenExpire);
			}
			NSPClient client = new NSPClient(huaweiToken);
			client.initHttpConnections(30, 50);// 设置每个路由的连接数和最大连接数

			client.initKeyStoreStream(new FileInputStream(certpath), this.getCert_Android_Huawei_Pwd());// 如果访问https必须导入证书流和密码
			// 调用push单发接口
			huaweipush.ps_single_send(client, pushCont, device.getToken(), title);
		} catch (NSPException e) {
			if (e.getCode() == 6) {
				Log.info("e.getcode"+e.getCode());
				try {
					OAuth2Client oauth2Client = new OAuth2Client();
					AccessToken access_token = oauth2Client.getAccessToken("client_credentials",
							this.getAndroid_Huawei_Appid(), this.getAndroid_Huawei_Appkey());
					huaweiToken = access_token.getAccess_token();
					huaweiTokenExpire = System.currentTimeMillis() + access_token.getExpires_in() * 1000;
				} catch (NSPException e1) {
					e1.printStackTrace();
				}

			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void pushAndroidOfflineMsgUseXiaomiSDK(Device device, String pushCont, JID jid, String title) {
		try {
			Constants.useOfficial();
			Sender sender = new Sender(this.getCert_android_xiaomi_key());// APP_SECRET_KEY
			String description = "";
			Random random = new Random();
			Integer notifyId = random.nextInt(10000000);
			com.xiaomi.xmpush.server.Message message = new com.xiaomi.xmpush.server.Message.Builder().title(title)
					.description(pushCont).payload(pushCont).passThrough(0)
					.restrictedPackageName(this.getAndroid_app_package_name())// MY_PACKAGE_NAME
					.notifyType(-1) // 使用默认提示音提示
					.notifyId(notifyId).extra(Constants.EXTRA_PARAM_NOTIFY_EFFECT, Constants.NOTIFY_LAUNCHER_ACTIVITY)
					.build();
			Result result = sender.send(message, device.getToken(), 3);// regId
			Log.info("Server response: ", "MessageId: " + result.getMessageId() + " ErrorCode: "
					+ result.getErrorCode().toString() + " Reason: " + result.getReason());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void pushOfflineMsg(String token, String pushCont, JID jid) {
		NotificationThreads work = null;
		try {
			
			Log.info("pushCont is ============"+ pushCont);
			Integer size = count.get(jid.getNode()) + 1;
			if (size <= 1000)
				count.put(jid.getNode(), size);
			List<PayloadPerDevice> list = new ArrayList<PayloadPerDevice>();
			PushNotificationPayload payload = new PushNotificationPayload();
			payload.addAlert(pushCont);
			payload.addSound("default");
			payload.addBadge(size);
			payload.addCustomDictionary("jid", jid.toString());
			PayloadPerDevice pay = new PayloadPerDevice(payload, token);
			list.add(pay);
			work = new NotificationThreads(appleServer, list, 1);
			work.setListener(DEBUGGING_PROGRESS_LISTENER);
			work.start();
		} catch (JSONException e) {
			Log.error("JSONException:" + e.getMessage());
		} catch (InvalidDeviceTokenFormatException e) {
			Log.error("InvalidDeviceTokenFormatException:" + e.getMessage());
		} finally {
			work.destroy();
			//Log.info("push to apple: username: " + jid.getNode() + " ,context" + pushCont);
		}
	}

	public Runnable createTask(final String token, final String msgType, final JID jid) {
		return new Runnable() {
			@Override
			public void run() {
				PushNotificationPayload payload = new PushNotificationPayload();
				try {
					String pushCont = LocaleUtils.getLocalizedString(msgType, "offlinepush");
					List<PayloadPerDevice> list = new ArrayList<PayloadPerDevice>();
					payload.addAlert(pushCont);
					payload.addSound("default");
					payload.addBadge(1);
					payload.addCustomDictionary("jid", jid.toString());
					PayloadPerDevice pay = new PayloadPerDevice(payload, token);
					list.add(pay);
					NotificationThreads work = new NotificationThreads(appleServer, list, 1);
					work.setListener(DEBUGGING_PROGRESS_LISTENER);
					work.start();
				} catch (JSONException e) {
					Log.error("JSONException:" + e.getMessage());
				} catch (InvalidDeviceTokenFormatException e) {
					Log.error("InvalidDeviceTokenFormatException:" + e.getMessage());
				}
			}
		};

	}

	public static final NotificationProgressListener DEBUGGING_PROGRESS_LISTENER = new NotificationProgressListener() {
		public void eventThreadStarted(NotificationThread notificationThread) {
			Log.info("   [EVENT]: thread #" + notificationThread.getThreadNumber() + " started with "
					+ " devices beginning at message id #" + notificationThread.getFirstMessageIdentifier());
		}

		public void eventThreadFinished(NotificationThread thread) {
			Log.info("   [EVENT]: thread #" + thread.getThreadNumber() + " finished: pushed messages #"
					+ thread.getFirstMessageIdentifier() + " to " + thread.getLastMessageIdentifier() + " toward "
					+ " devices");
		}

		public void eventConnectionRestarted(NotificationThread thread) {
			Log.info(
					"   [EVENT]: connection restarted in thread #" + thread.getThreadNumber() + " because it reached "
							+ thread.getMaxNotificationsPerConnection() + " notifications per connection");
		}

		public void eventAllThreadsStarted(NotificationThreads notificationThreads) {
			Log.info("   [EVENT]: all threads started: " + notificationThreads.getThreads().size());
		}

		public void eventAllThreadsFinished(NotificationThreads notificationThreads) {
			Log.info("   [EVENT]: all threads finished: " + notificationThreads.getThreads().size());
		}

		public void eventCriticalException(NotificationThread notificationThread, Exception exception) {
			Log.info("   [EVENT]: critical exception occurred: " + exception);
		}
	};
}
