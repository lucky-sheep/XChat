package cn.tim.xchat.common.event;

public class TokenEvent {
    TokenType type;

    public TokenEvent(TokenType typeEnum) {
        type = typeEnum;
    }
    public enum TokenType {
        TOKEN_OVERDUE, // Token过期
        TOKEN_REFRESH, // 刷新Token，重新从本地获取
        SERVER_ERROR, // 服务器故障
    }

    public TokenType getType() {
        return type;
    }
}
