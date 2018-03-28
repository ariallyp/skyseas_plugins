package com.skyseas.openfireplugins.group.iq.user;

import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.util.HasReasonPacket;
import com.skyseas.openfireplugins.group.util.StringUtils;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;


/**
 * Created by apple on 14-9-8.
 */
public class InviteResultPacket extends HasReasonPacket {

    public InviteResultPacket(String name, String namespace) {
        super(name, namespace, "invite");
    }

    public InviteResultPacket(Element element) {
        super(element, "invite");
    }

    private void setReason(String reason) {
        modeElement.addElement("reason").setText(StringUtils.ifNullReturnEmpty(reason));
    }

    public String getUserName() {
        Element ele = getMemberElement();
        return ele == null ? null : ele.attributeValue("username");
    }

    public void setUserName(String userName) {
        Element ele = ensureMemberElement();
        ele.addAttribute("username", StringUtils.ifNullReturnEmpty(userName));
    }

    public String getNickname() {
        Element ele = getMemberElement();
        return ele == null ? null : ele.attributeValue("nickname");
    }

    public void setNickname(String nickname) {
        Element ele = ensureMemberElement();
        ele.addAttribute("nickname", StringUtils.ifNullReturnEmpty(nickname));
    }

    public String getId() {
        return getAttributeValue("id");
    }

    private void setId(String id) {
        modeElement.addAttribute("id", StringUtils.ifNullReturnEmpty(id));
    }

    public boolean isAgree() {
        return modeElement.element("agree") != null;
    }

    public boolean isDecline() {
        return modeElement.element("decline") != null;
    }

    private Element getMemberElement() {
        return this.modeElement.element("member");
    }
    private Element ensureMemberElement() {
        Element element = getMemberElement();
        if(element == null) {
            element = this.modeElement.addElement("member");
        }
        return element;
    }

    private String getElementValue(String name) {
        Element ele = this.modeElement.element(name);
        if (ele == null) {
            return "";
        }
        return ele.getStringValue();
    }

    private String getAttributeValue(String name) {
        Attribute attr = this.modeElement.attribute(name);
        if (attr == null) {
            return "";
        }
        return attr.getValue();
    }




    /**
     * 创建一个反应申请结果的扩展包。
     *
     * @param result
     * @param from
     * @param reason
     * @return
     */
    public static Message newInstanceForInviteResult(boolean result, String from, String reason) {
        InviteResultPacket packet = new InviteResultPacket("x", IQHandler.MEMBER_NAMESPACE);
        Message message = new Message();

        Element resultEle = result
                ? packet.modeElement.addElement("agree")
                : packet.modeElement.addElement("decline");

        if (!StringUtils.isNullOrEmpty(from)) {
            resultEle.addAttribute("from", from);
        }

        if (!StringUtils.isNullOrEmpty(reason)) {
            packet.setReason(reason);
        }

        packet.appendTo(message.getElement());
        return message;
    }

}
