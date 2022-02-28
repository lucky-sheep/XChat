package cn.tim.xchat.enums.business;

import cn.tim.xchat.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 请求添加好友的三种状态
 */
@Getter
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
}