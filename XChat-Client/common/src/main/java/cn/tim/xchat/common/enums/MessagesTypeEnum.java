package cn.tim.xchat.common.enums;
/**
 * 消息类型, 服务端必须和客户端端统一
 */
public enum MessagesTypeEnum {
    TEXT,
    VOICE,
    VIDEO,
    PICTURE,
    ADDRESS,
    FILE,

    FRIEND_REQUEST, // 好友添加请求
}
