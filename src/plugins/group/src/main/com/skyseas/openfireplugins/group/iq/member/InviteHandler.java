package com.skyseas.openfireplugins.group.iq.member;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.spi.ChatUserImpl;
import com.skyseas.openfireplugins.group.FullMemberException;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupInfo.OpennessType;
import com.skyseas.openfireplugins.group.GroupMemberInfo;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.iq.MemberIQHandler;
import com.skyseas.openfireplugins.group.iq.XHandler;
import com.skyseas.openfireplugins.group.iq.owner.ApplyProcessPacket;
import com.skyseas.openfireplugins.group.util.ModelPacket;
import com.skyseas.openfireplugins.group.util.StringUtils;

import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.TaskEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 邀请处理程序。 Created by zhangzhi on 2014/10/9.
 */
@XHandler(namespace = IQHandler.MEMBER_NAMESPACE, elementName = "invite")
public class InviteHandler extends MemberIQHandler {
	private final static Logger LOGGER = LoggerFactory.getLogger(InviteHandler.class);

	@Override
	protected void process(IQ packet, Group group) {
		assert packet != null;
		assert group != null;

		InvitePacket invPacket = new InvitePacket(packet.getChildElement());
		List<GroupMemberInfo> newMembers = invPacket.getMembers();

		if (newMembers.size() < 1) {
			replyError(packet, PacketError.Condition.bad_request);
			return;
		}

		ArrayList<ChatUser> newUsers = new ArrayList<ChatUser>(newMembers.size());
		synchronized (this) {

			/* 排除已经存在的用户 */
			for (GroupMemberInfo memberInfo : newMembers) {
				if (!group.getChatUserManager().hasUser(memberInfo.getUserName())) {
					ChatUserImpl chatUser = new ChatUserImpl(memberInfo.getUserName(), memberInfo.getNickName(), null);
					newUsers.add(chatUser);
				}
			}
		}
		if (newUsers.size() > 0) {
			if (!group.getGroupInfo().getOpennessType().equals(OpennessType.TEMP)) {
				for (ChatUser cu : newUsers) {
					Message msg = createInviteMessage(packet.getFrom().toBareJID(), cu);
					/* 向被邀请人发送消息 */
					group.send(groupService.getServer().createJID(cu.getUserName(), null), msg);
				}
			} else {
				for (ChatUser cu : newUsers) {
					 try {
						group.getChatUserManager().addUser(cu.getUserName(),cu.getNickname());
					} catch (FullMemberException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				
			}

			replyOK(packet);
		} else {
			replyError(packet, PacketError.Condition.internal_server_error);
		}

		// List<ChatUser> addedUsers = addUsers(group, newMembers);
		// if (addedUsers != null) {
		// replyOK(packet);
		// /* 广播邀请消息 */
		// broadcastInviteMessage(group, packet.getFrom().toBareJID(),
		// addedUsers);
		// } else {
		// replyError(packet, PacketError.Condition.internal_server_error);
		// }

	}

	// private void broadcastInviteMessage(final Group group, final String from,
	// final List<ChatUser> newMembers) {
	// if (newMembers.size() > 0) {
	// TaskEngine.getInstance().submit(new Runnable() {
	// @Override
	// public void run() {
	// Message msg = createInviteMessage(from, newMembers);
	// group.broadcast(msg);
	// }
	// });
	// }
	// }

	/**
	 * <message from='100@group.skysea.com' to='user1@skysea.com'> <x xmlns=
	 * 'http://skysea.com/protocol/group#member'> <invite from=
	 * 'user@skysea.com'> <member username='user100' nickname='独孤求败' /> <member
	 * username='user101' nickname='雁过留声' /> <member username='user102' nickname
	 * ='圆月弯刀' /> </invite> </x> </message>
	 *
	 * @return
	 */
	private Message createInviteMessage(String from, ChatUser chatUser) {
		Message msg = new Message();
		Element invElement = msg.addChildElement("x", IQHandler.MEMBER_NAMESPACE).addElement("invite")
				.addAttribute("from", from);

		invElement.addElement("member").addAttribute("username", chatUser.getUserName()).addAttribute("nickname",
				chatUser.getNickname());

		return msg;
	}

	private List<ChatUser> addUsers(Group group, List<GroupMemberInfo> newMembers) {
		try {
			return group.getChatUserManager().addUsers(newMembers);
		} catch (FullMemberException exp) {
			LOGGER.error("invite members buf group full member.", exp);
		} catch (Throwable throwable) {
			LOGGER.error("add Users fail.", throwable);
		}
		return null;
	}

	private static class InvitePacket extends ModelPacket {
		public InvitePacket(Element element) {
			super(element, "invite");
		}

		public List<GroupMemberInfo> getMembers() {
			List<Element> memberElements = modeElement.elements("member");
			ArrayList<GroupMemberInfo> members = new ArrayList<GroupMemberInfo>(memberElements.size());

			for (Element memElement : memberElements) {
				String userName = memElement.attributeValue("username");
				String userNickname = memElement.attributeValue("nickname");

				if (!StringUtils.isNullOrEmpty(userName)) {
					members.add(new GroupMemberInfo(userName,
							StringUtils.ifNullReturnDefaultValue(userNickname, userName)));
				}
			}
			return members;
		}
	}
}
