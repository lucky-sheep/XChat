package cn.tim.xchat.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS(0, "成功"),
    ERROR(10001, "失败"),

    PARAM_ERROR(20001, "参数错误"),
    USERNAME_ALREADY_EXISTS(20002, "用户名已经存在"),
    EMAIL_ALREADY_EXISTS(20003, "Email已经存在"),
    SAME_DEVICE_REPEAT_REQUEST(20004, "相同设备重复请求"),
    PWD_ERROR_TOO_MANY_TIMES(20005, "重试次数频繁，请10分钟后再试"),
    PWD_LOGIN_FAILED(20006, "用户名或密码错误"),
    USER_DOES_NOT_EXIST(20007, "该用户不存在，申请无效"),
    YOU_ARE_ALREADY_FRIENDS(20008, "已经是好友了，申请无效"),
    REPEAT_REQUEST_FRIENDS(20009, "已申请过，请勿重复发起"),

    TOKEN_OVERDUE(50001, "Token过期，请刷新Token");

    private final Integer code;
    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
