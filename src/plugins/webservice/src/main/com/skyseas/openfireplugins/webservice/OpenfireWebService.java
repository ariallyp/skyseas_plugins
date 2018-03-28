package com.skyseas.openfireplugins.webservice;

import java.util.List;

import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.FullMemberException;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.GroupMemberInfo;
import com.skyseas.openfireplugins.group.GroupQueryObject;
import com.skyseas.openfireplugins.group.ChatUserManager.RemoveType;
import com.skyseas.openfireplugins.group.util.Paging;

public interface OpenfireWebService {

	public final static String ALLOW_CREATE_GROUP = "group.setting.allow_create_group";
	public final static String FIRE_PROPERTY_LISTENER_TMP = "fire.property.listener.tmp";

	// 用户管理
	/**
	 * 获得用户列表。
	 * 
	 * @return
	 */
	ChatUser[] getUsers();

	/**
	 * 获得用户数量。
	 * 
	 * @return
	 */
	int getNumberOfUsers();

	/**
	 * 获得特定用户。
	 * 
	 * @param userName
	 * @return
	 */
	ChatUser getUser(String userName);

	/**
	 * 返回当前用户是否是多用户聊天房间用户。
	 * 
	 * @param userName
	 * @return
	 */
	boolean hasUser(String userName);

	/**
	 * 添加用户。
	 * 
	 * @param userName
	 * @param nickname
	 */
	ChatUser addUser(String userName, String nickname)
			throws FullMemberException;

	/**
	 * 批量添加用户。
	 * 
	 * @param memberInfoList
	 * @return
	 * @throws FullMemberException
	 */
	List<ChatUser> addUsers(List<GroupMemberInfo> memberInfoList);

	/**
	 * 删除用户。
	 * 
	 * @param userName
	 * @return
	 */
	ChatUser removeUser(RemoveType type, String userName, JID from,
			String reason);

	/**
	 * 修改用户昵称。
	 * 
	 * @param userName
	 * @param nickname
	 */
	void changeNickname(String userName, String nickname);

	// 群管理
	/**
	 * 获取所有群
	 * 
	 * @return
	 */
	public String getAllGroup();

	/**
	 * 更新群状态
	 * 
	 * @param groupId
	 * @param status
	 * @return
	 */
	public String updateGroupStatus(String groupId, String status);

	/**
	 * 创建群
	 * 
	 * @param jids
	 * @param ownerJid
	 * @param name
	 * @param desc
	 * @return
	 */
	public String createGroup(String[] jids, String ownerJid, String name,
			String desc);

	/**
	 * 创建群。
	 * 
	 * @param groupInfo
	 * @return 群标示符。
	 */
	Group create(GroupInfo groupInfo);

	/**
	 * 获得群Id。
	 * 
	 * @param id
	 * @return
	 */
	Group getGroup(String id);

	/**
	 * 搜索群信息列表。
	 * 
	 * @return
	 */
	Paging<GroupInfo> search(GroupQueryObject query, int offset, int limit);

	/**
	 * 更新群信息。
	 * 
	 * @param groupInfo
	 * @return 群标示符。
	 */
	Group update(GroupInfo groupInfo);

	/**
	 * 删除群。
	 * 
	 * @param group
	 * @param operator
	 * @param reason
	 * @return
	 */
	boolean remove(Group group, JID operator, String reason);

	/**
	 * 获得成员加入过的群列表。
	 * 
	 * @param userName
	 * @return
	 */
	GroupInfo[] getMemberJoinedGroups(String userName);

	/**
	 * 更新群设置
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public String updateGroupSetting(String propertyName, String value);

	/**
	 * 获得群设置
	 * 
	 * @param propertyName
	 * @return
	 */
	public String getGroupSetting(String propertyName);

	/**
	 * 获取当前在线用户数
	 * 
	 * @return
	 */
	public String getCurrentSessionCount();

	// 用户-群行为管理
	/**
	 * 当用户已退出。
	 * 
	 * @param group
	 * @param user
	 * @param reason
	 */
	boolean userExited(Group group, ChatUser user, String reason);

	/**
	 * 当用户已被踢出
	 * 
	 * @param group
	 * @param user
	 * @param from
	 * @param reason
	 */
	boolean userKicked(Group group, ChatUser user, JID from, String reason);

	/**
	 * 当用户昵称已修改。
	 * 
	 * @param group
	 * @param user
	 * @param oldNickname
	 */
	boolean userNicknameChanged(Group group, ChatUser user, String oldNickname);

	/**
	 * 当用户已加入圈子。
	 * 
	 * @param group
	 * @param user
	 */
	boolean userJoined(Group group, ChatUser user);

	// 群消息管理
	/**
	 * 发送消息
	 * 
	 * @param packet
	 * @return
	 */
	boolean sendPacket(Packet packet);

	/**
	 * 发送系统消息
	 * 
	 * @param domain
	 * @param toId
	 * @param content
	 * @return
	 */
	public String sendServerMessage(String domain, String toId, String content);

	/**
	 * 发送提醒消息
	 * 
	 * @param domain
	 * @param fromId
	 * @param toId
	 * @param content
	 * @return
	 */
	public String sendNoticeMessage(String domain, String fromId, String toId,String content);

}
