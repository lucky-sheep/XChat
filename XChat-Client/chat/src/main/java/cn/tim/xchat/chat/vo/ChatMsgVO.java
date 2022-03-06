package cn.tim.xchat.chat.vo;

import java.time.Instant;

import cn.tim.xchat.common.enums.business.MsgSignEnum;
import cn.tim.xchat.common.msg.MsgTypeEnum;

public class ChatMsgVO {

    private String msgId;

    private String text;

    // 如果不是 http://xxxxx, https://xxxx, file:// 那么就是用文字头像
    private String senderAvatar;


    private String senderUserId;

    private String time;

    private MsgSignEnum sign; // 已读/未读 -> 签收状态

    private boolean isMe;

    private MsgTypeEnum msgTypeEnum;

    private long instant;

    public ChatMsgVO(){}


    public ChatMsgVO(String msgId, String text, String senderAvatar,
                     String time, MsgSignEnum sign, boolean isMe,
                     MsgTypeEnum msgTypeEnum, long instant) {
        this.msgId = msgId;
        this.text = text;
        this.senderAvatar = senderAvatar;
        this.time = time;
        this.sign = sign;
        this.isMe = isMe;
        this.msgTypeEnum = msgTypeEnum;
        this.instant = instant;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public MsgSignEnum getSign() {
        return sign;
    }

    public void setSign(MsgSignEnum sign) {
        this.sign = sign;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public boolean isMe() {
        return isMe;
    }

    public MsgTypeEnum getMsgTypeEnum() {
        return msgTypeEnum;
    }

    public void setMsgTypeEnum(MsgTypeEnum msgTypeEnum) {
        this.msgTypeEnum = msgTypeEnum;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public long getInstant() {
        return instant;
    }

    public void setInstant(long instant) {
        this.instant = instant;
    }

    @Override
    public String toString() {
        return "ChatMsgVO{" +
                "msgId='" + msgId + '\'' +
                ", text='" + text + '\'' +
                ", senderAvatar='" + senderAvatar + '\'' +
                ", senderUserId='" + senderUserId + '\'' +
                ", time='" + time + '\'' +
                ", sign=" + sign +
                ", isMe=" + isMe +
                ", msgTypeEnum=" + msgTypeEnum +
                ", instant=" + instant +
                '}';
    }
}
