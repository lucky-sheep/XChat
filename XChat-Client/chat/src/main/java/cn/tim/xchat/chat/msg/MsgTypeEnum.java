package cn.tim.xchat.chat.msg;

/**
 * 消息类型, 服务端必须和客户端端统一
 */
public enum MsgTypeEnum {
    TEXT,
    VOICE,
    VIDEO,
    PICTURE,
    ADDRESS,
    FILE,

    FRIEND_REQUEST, // 好友添加请求
}
