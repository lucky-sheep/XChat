package cn.tim.xchat.common.event;

public class WSEvent {
    Type type;
    public WSEvent(Type type) {
        this.type = type;
    }

    public enum Type {
        CONNECTED(0, "已连接"), //
        DISCONNECTED(1, "已断开"), // 连接断开
        DISCONNECTED_BY_USER(2, "主动关闭"); // 主动关闭服务

        String name;
        int code;

        Type(int code, String name) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }
    }

    public Type getType() {
        return type;
    }
}
