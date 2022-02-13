package cn.tim.xchat.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS(0, "成功"),
    ERROR(10001, "失败"),

    PARAM_ERROR(20001, "参数错误"),
    USERNAME_ALREADY_EXISTS(20002, "用户名已经存在"),
    EMAIL_ALREADY_EXISTS(20003, "Email已经存在"),
    SAME_DEVICE_REPEAT_REQUEST(20004, "相同设备重复请求");

    private final Integer code;
    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
