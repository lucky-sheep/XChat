package cn.tim.xchat.core.model;

import lombok.Data;

@Data
public class ChatMessage {
    /**
     * 消息类型，{@link cn.tim.xchat.core.enums.MessagesTypeEnum}
     */
    private int type;

    // 消息内容
    private String text;

    // @xxx，@某个用户ID用于群聊
    private String at;

    // 音视频消息的URL
    private String url;

    // 用于回复某条消息 msg id，用于群/私聊
    private String reply;
}
