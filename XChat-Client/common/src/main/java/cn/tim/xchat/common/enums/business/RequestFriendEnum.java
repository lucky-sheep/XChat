package cn.tim.xchat.common.enums.business;

import cn.tim.xchat.common.enums.CodeEnum;

public enum RequestFriendEnum implements CodeEnum {
    UNHAND(0, "未处理"),
    AGREE(1, "已同意"),
    REFUSE(2, "已拒绝");

    int code;
    String desc;

    RequestFriendEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}