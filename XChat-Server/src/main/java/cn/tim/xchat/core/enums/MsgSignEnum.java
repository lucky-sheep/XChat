package cn.tim.xchat.core.enums;

import lombok.Getter;

@Getter
public enum MsgSignEnum {
    UN_RECEIVE(0, "未发出"),
    RECEIVED(1, "已收到"),
    UN_READ(2, "未读"),
    READ(3, "已读");

    int code;
    String desc;

    MsgSignEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
