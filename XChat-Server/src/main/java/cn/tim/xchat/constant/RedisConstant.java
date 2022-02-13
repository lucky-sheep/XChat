package cn.tim.xchat.constant;

public interface RedisConstant {
    String TOKEN_PREFIX = "token_%s";
    Integer EXPIRE = 120; // 2小时
    Integer BLACKLIST_INVALID = 300;
    Integer PASSWORD_ERROR_EXPIRE = 600;
}
