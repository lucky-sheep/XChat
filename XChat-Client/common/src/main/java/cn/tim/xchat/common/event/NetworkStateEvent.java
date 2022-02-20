package cn.tim.xchat.common.event;

public class NetworkStateEvent {
    NetworkStateEvent.Type type;

    public NetworkStateEvent(NetworkStateEvent.Type typeEnum) {
        type = typeEnum;
    }
    public enum Type {
        AVAILABLE,
        UNAVAILABLE
    }

    public NetworkStateEvent.Type getType() {
        return type;
    }
}
