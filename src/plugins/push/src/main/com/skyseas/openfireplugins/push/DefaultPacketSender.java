package com.skyseas.openfireplugins.push;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.JiveConstants;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * 默认的XMPP包发送器。
 * Created by zhangzhi on 2014/11/11.
 */
public class DefaultPacketSender implements PacketSender {
	private static final Logger Log = LoggerFactory.getLogger(DefaultPacketSender.class);
    private final PacketRouter packetRouter;
    private final RoutingTable routingTable;

    public DefaultPacketSender() {
        this(XMPPServer.getInstance().getPacketRouter(),
             XMPPServer.getInstance().getRoutingTable());
    }

    DefaultPacketSender(PacketRouter packetRouter, RoutingTable routingTable) {
        if (packetRouter == null) {
            throw new NullPointerException("packetRouter");
        }
        if (routingTable == null) {
            throw new NullPointerException("routingTable");
        }

        this.packetRouter = packetRouter;
        this.routingTable = routingTable;
    }

    @Override
    public void send(Packet packet) {

        /**
         * 如果packet收件人地址不为空，则将该packet单独路由。
         *
         * 否则，认为packet是广播消息，广播需要将packet发送至
         * 当前在线的所有客户端，并且系统只接收message类型的广播消息。
         */
        if (packet.getTo() != null) {
        	
            //在不在线
        	User user = null;
			try {
				user = XMPPServer.getInstance().getUserManager().getUser(packet.getTo().getNode());
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(null == user){
				//没有用户
				return;
			}
            Presence p =XMPPServer.getInstance().getPresenceManager().getPresence(user);
            if(p==null){
            //  store((Message)packet);//不在线
              //不在线先保存消息，然后路由出去做消息推送
              route(packet);
            }else{
              route(packet);
            }
           
        } else {
            if (packet instanceof Message) {
                broadcast((Message) packet);
            }else {
                throw new IllegalArgumentException("broadcast packet must be message.");
            }
        }
    }

    private void broadcast(Message packet) {
        routingTable.broadcastPacket((Message) packet, true);
    }

    private void route(Packet packet) {
        packetRouter.route(packet);
    }
    
    
    
    
    private void store(Message message){
   	 if (message == null) {
            return;
        }
        JID recipient = message.getTo();
        String username = recipient.getNode();
        // If the username is null (such as when an anonymous user), don't store.
        if (username == null || !UserManager.getInstance().isRegisteredUser(recipient)) {
            return;
        }
        else
        if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(recipient.getDomain())) {
            // Do not store messages sent to users of remote servers
            return;
        }

        long messageID = SequenceManager.nextID(JiveConstants.OFFLINE);

        // Get the message in XML format.
        String msgXML = message.getElement().asXML();

        String sql = 
       	        "INSERT INTO ofOffline (username, messageID, creationDate, messageSize, stanza) " +
       	                "VALUES (?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setLong(2, messageID);
            pstmt.setString(3, StringUtils.dateToMillis(new java.util.Date()));
            pstmt.setInt(4, msgXML.length());
            pstmt.setString(5, msgXML);
            pstmt.executeUpdate();
        }

        catch (Exception e) {
       	 e.printStackTrace();
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }

   }
}
