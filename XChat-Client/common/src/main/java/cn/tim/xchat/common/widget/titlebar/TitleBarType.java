package cn.tim.xchat.common.widget.titlebar;

public enum TitleBarType {
    MESSAGE_MAIN_PAGER(101, "消息列表页"),
    MESSAGE_CHAT_PAGER(102, "聊天页"),
    MESSAGE_CHAT_TARGET_INFO(103, "聊天对象信息页"),

    CONTACTS_MAIN_PAGER(201, "联系人主页"),
    CONTACTS_FRIEND_INFO(202, "联系人详情"),
    CONTACTS_APPLY_LIST(203, "好友申请列表"),

    PERSONAL_MAIN_PAGER(301, "个人信息页"),
    PERSONAL_DETAIL_PAGER(302, "个人信息详情页");


    private int type;
    private final String desc;
    public static final String TAG = "TitleBarType";

    TitleBarType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }
}
