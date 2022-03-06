package cn.tim.xchat.common.module.chat;


import org.litepal.crud.LitePalSupport;

public class ChatMsg extends LitePalSupport {

    private String itemId; // -> db id

    private int type;

    private String sendUserId;

    private String acceptUserId;

    private String msg;

    private int signFlag;

    private long createTime;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getAcceptUserId() {
        return acceptUserId;
    }

    public void setAcceptUserId(String acceptUserId) {
        this.acceptUserId = acceptUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSignFlag() {
        return signFlag;
    }

    public void setSignFlag(int signFlag) {
        this.signFlag = signFlag;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    @Override
    public String toString() {
        return "ChatMsg{" +
                "itemId='" + itemId + '\'' +
                ", type=" + type +
                ", sendUserId='" + sendUserId + '\'' +
                ", acceptUserId='" + acceptUserId + '\'' +
                ", msg='" + msg + '\'' +
                ", signFlag=" + signFlag +
                ", createTime=" + createTime +
                '}';
    }
}
