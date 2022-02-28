package cn.tim.xchat.common.msg;

import cn.tim.xchat.common.enums.CodeEnum;

public enum MsgActionEnum implements CodeEnum {
    KEEPALIVE(0, "心跳保持"),
    CONNECT(1, "初始化链接/重连"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "消息签收/已读"),
    BUSINESS(4, "业务消息(需要使用WS通道的业务)"),
    GROUP_CHAT(5, "群聊消息");

    public final int type;
    public final String desc;

    MsgActionEnum(int type, String desc){
        this.type = type;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return this.type;
    }
}
