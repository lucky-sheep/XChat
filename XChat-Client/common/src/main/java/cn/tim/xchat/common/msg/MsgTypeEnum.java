package cn.tim.xchat.common.msg;

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

    FRIEND_REQUEST, // 好友添加请求, 同意/拒绝请求等，都算
}