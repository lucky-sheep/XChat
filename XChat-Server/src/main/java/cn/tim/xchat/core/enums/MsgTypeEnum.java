package cn.tim.xchat.core.enums;

/**
 * 消息类型, 服务端必须和客户端端统一
 */
public enum MsgTypeEnum implements CodeEnum {
    TEXT(0, "文本消息"),
    VOICE(1, "语音消息"),
    VIDEO(2, "视频消息"),
    PICTURE(3, "图片消息"),
    ADDRESS(4, "位置消息"),
    FILE(5, "文件"),


    FRIEND_REQUEST_NEW(11, "好友添加请求 & 客户端同意/拒绝"),
    FRIEND_REQUEST_RET(12, "好友请求同意结果");

    private int code;
    private String desc;

    MsgTypeEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
