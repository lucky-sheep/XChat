package cn.tim.xchat.common.event;

import java.util.Map;

public class ChatEvent {
    public final static String SENDER_ID = "SENDER_ID";
    public final static String MSG_ITEM_ID = "MSG_ITEM_ID";

    ChatEvent.Type type;

    Map<String, Object> data;

    public ChatEvent(ChatEvent.Type typeEnum) {
        type = typeEnum;
    }

    // 传递数据
    public ChatEvent(ChatEvent.Type typeEnum, Map<String, Object> data) {
        type = typeEnum;
        this.data = data;
    }

    public enum Type {
        RECEIVE_MSG,
        SEND_MSG
    }

    public Type getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
