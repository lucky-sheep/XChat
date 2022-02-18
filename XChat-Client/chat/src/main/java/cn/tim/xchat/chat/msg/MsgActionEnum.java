package cn.tim.xchat.chat.msg;

public enum MsgActionEnum {
    KEEPALIVE(0, "心跳保持"),
    CONNECT(1, "初始化链接/重连"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "消息签收/已读");

    public final int type;
    public final String desc;

    MsgActionEnum(int type, String desc){
        this.type = type;
        this.desc = desc;
    }
}