package com.skyseas.openfireplugins.group;

import com.skyseas.openfireplugins.group.spi.GroupServiceImpl;

import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import java.io.File;
import java.util.TimeZone;

/**
 * 圈子插件。
 * Created by zhangzhi on 2014/9/30.
 */
public final class GroupPlugin implements PacketInterceptor,Plugin {
    private final static Logger LOG = LoggerFactory.getLogger(GroupPlugin.class);
    private GroupServiceImpl groupService;
	// Hook for intercpetorn
	private InterceptorManager interceptorManager;
    public GroupPlugin() {
    	interceptorManager = InterceptorManager.getInstance();
	}
    
    @Override
    public void initializePlugin(PluginManager pluginManager, File file) {
        String serviceName = JiveGlobals.getProperty("group.servicename", "group"); // conference
        GroupServiceImpl service = createService(serviceName);
        // GroupServiceImpl 需要实现 PropertyEventListener接口 ，此为触发对应的事件监听器。
        PropertyEventDispatcher.addListener(service);
        installService(service);
        interceptorManager.addInterceptor(this);
    }
    

    
    //拦截包
	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		
		System.out.println(packet.toXML());
		// TODO Auto-generated method stub
		if (packet instanceof Message) {
			Message message = (Message) packet;
//			TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
//			TimeZone.setDefault(tz);
//			Element ele=message.getElement().element("msgExtension");
//			if(ele!=null)
//				ele.attribute("createTime").setValue(System.currentTimeMillis()+"");
			// 一对一聊天，单人模式
			if ((message.getType() == Message.Type.chat||message.getType() == Message.Type.groupchat)){
    	        UserManager userManager = UserManager.getInstance();
    	        	String userName = message.getTo().getNode().toString();
    	        	try {
    	        		userManager.getUser(userName);
					} catch (UserNotFoundException e) {
						try {
							userManager.createUser(userName, "1", userName, null);
						} catch (UserAlreadyExistsException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
			}
		}
	}

    private GroupServiceImpl createService(String serviceName) {
        return new GroupServiceImpl(serviceName,
                "this is skysea multi user chat service.",
                XMPPServer.getInstance());
    }

    @Override
    public void destroyPlugin() {
        if(groupService != null) {
            uninstallService();
        }
        interceptorManager.removeInterceptor(this);
    }

    private void installService(GroupServiceImpl groupService) {
        try {
        	
        	// GroupServiceImpl 需要实现 Component接口  
            ComponentManagerFactory.getComponentManager().addComponent(groupService.getServiceName(), groupService);
            this.groupService = groupService;
        } catch (ComponentException e) {
            LOG.error("安装GroupService到组件管理器失败。", e);
        }
    }
    private void uninstallService() {
        try {
            ComponentManagerFactory.getComponentManager().removeComponent(groupService.getServiceName());
            PropertyEventDispatcher.removeListener(groupService);
        } catch (ComponentException e) {
            LOG.error("从组件管理器卸载GroupService失败。", e);
        }
    }

}
