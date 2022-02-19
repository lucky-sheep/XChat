package cn.tim.xchat.chat.ws.event;

public class WSEvent {
    Type type;

    public WSEvent(Type type) {
        this.type = type;
    }
    public enum Type {
        CONN_SUCCESS, // 连接成功
        DIS_CONN, // 连接断开
    }

    public Type getType() {
        return type;
    }
}
