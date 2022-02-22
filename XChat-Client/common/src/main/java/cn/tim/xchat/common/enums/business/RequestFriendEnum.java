package cn.tim.xchat.common.enums.business;

import cn.tim.xchat.common.enums.CodeEnum;

public enum RequestFriendEnum implements CodeEnum {
    UNHAND_ME(0, "我未处理"),
    AGREE_ME(1, "我已同意"),
    REFUSE_ME(2, "我已拒绝"),

    PARTITION_VALIDATOR(5, "============ 用于快速判别类型 ============"),

    UNHAND_OTHER(7, "对方未处理"),
    AGREE_OTHER(8, "对方同意"),
    REFUSE_OTHER(9, "对方拒绝");

    int code;
    String desc;

    RequestFriendEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static boolean isMyHandTask(int code){
        return code < RequestFriendEnum.PARTITION_VALIDATOR.getCode();
    }

    public String getDesc() {
        return desc;
    }
}