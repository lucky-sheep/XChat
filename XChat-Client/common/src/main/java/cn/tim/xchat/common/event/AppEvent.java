package cn.tim.xchat.common.event;

public class AppEvent {
    AppEvent.Type type;

    public AppEvent(AppEvent.Type typeEnum) {
        type = typeEnum;
    }

    public enum Type {
        NETWORK_AVAILABLE, // 网络可用
        NETWORK_UNAVAILABLE, // 网络不可用
    }

    public AppEvent.Type getType() {
        return type;
    }
}
