package cn.tim.xchat.common.event;

public class WSEvent {
    Type type;

    public WSEvent(Type type) {
        this.type = type;
    }
    public enum Type {
        CONN_SUCCESS, // 连接成功
        DIS_CONN, // 连接断开
        ACTIVE_CLOSE, // 主动关闭服务
    }

    public Type getType() {
        return type;
    }
}
