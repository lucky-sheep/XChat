package cn.tim.xchat.common.event;

import java.util.Map;

public class AppEvent {
    AppEvent.Type type;

    Map<String, Object> data;

    public AppEvent(AppEvent.Type typeEnum) {
        type = typeEnum;
    }

    // 传递数据
    public AppEvent(AppEvent.Type typeEnum, Map<String, Object> data) {
        type = typeEnum;
        this.data = data;
    }

    public enum Type {
        NETWORK_AVAILABLE, // 网络可用
        NETWORK_UNAVAILABLE, // 网络不可用
        NEW_FRIENDS_REQUEST, // 新的朋友添加请求(包含同意、拒绝)
    }

    public AppEvent.Type getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
