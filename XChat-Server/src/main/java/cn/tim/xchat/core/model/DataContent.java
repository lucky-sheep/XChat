package cn.tim.xchat.core.model;

import cn.tim.xchat.core.enums.MsgActionEnum;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DataContent {
    /**
     * {@link MsgActionEnum}
     */
    protected int action;

    protected String senderId;
    protected String receiveId;
    // 用户发送的时间戳（并非服务器时间）
    protected int timestamp;
    protected ChatMessage chatMessage;
}
