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
    PWD_ERROR_TOO_MANY_TIMES(20005, "用户名/密码错误次数太多, 10分钟后再试"),
    PWD_LOGIN_FAILED(20006, "用户名/密码错误");

    private final Integer code;
    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
