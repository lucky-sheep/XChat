package cn.tim.xchat.common.enums.business;

import cn.tim.xchat.common.enums.CodeEnum;

public enum MsgSignEnum implements CodeEnum {
    UN_RECEIVE(0, "未发出"),
    RECEIVED(1, "已收到"), // 服务器用，client不用
    UN_READ(2, "未读"),
    READ(3, "已读");

    int code;
    String desc;

    MsgSignEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }


    public String getDesc() {
        return desc;
    }

    @Override
    public int getCode() {
        return code;
    }
}
