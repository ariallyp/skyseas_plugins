package com.skyseas.openfireplugins.group.iq.user;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupEventDispatcher;
import com.skyseas.openfireplugins.group.iq.GroupIQHandler;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.iq.XHandler;
import com.skyseas.openfireplugins.group.util.StringUtils;


/**
 * 邀请用户后用户同意-不同意加入的处理程序。
 * Created by ghg  on 16-2-22.
 */
@XHandler(namespace = IQHandler.USER_NAMESPACE, elementName = "invite")
public class InviteResultHandler extends GroupIQHandler {

	@Override
	protected void process(IQ packet, Group group) {
		// TODO Auto-generated method stub
	      assert packet != null;
	        assert group != null;
	        InviteResultPacket applyPacket = new InviteResultPacket(packet.getChildElement());
	        String userName = applyPacket.getUserName();
	        String nickname = applyPacket.getNickname();
	        if (StringUtils.isNullOrEmpty(userName)) {
	            replyError(packet, PacketError.Condition.bad_request);
	            return;
	        }
			
			boolean result = true; // 同意
            if (!applyPacket.isAgree()) {
            	result = false;
            } 
            try {
				 if(result){
					 group.getChatUserManager().addUser(userName,nickname); 
					 replyOK(packet);
					 return;
				 }
//				Message msg = InviteResultPacket.newInstanceForInviteResult(result,applyPacket.getUserName(), applyPacket.getReason());
//				group.send(packet.getFrom(), msg);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				 replyError(packet, PacketError.Condition.internal_server_error);
			} 
            replyError(packet, PacketError.Condition.internal_server_error);
	}

}
