package cn.tim.xchat.common.module;

import org.litepal.crud.LitePalSupport;

public class FriendInfo extends LitePalSupport implements FriendObtain{
    private String itemId;
    private String userId;
    private String username;
    private String nickname;
    private String email;
    private String faceImage;
    private String faceBigImage;
    private String qrCode;
    private String notes;

    public FriendInfo(){

    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public String getFaceBigImage() {
        return faceBigImage;
    }

    public void setFaceBigImage(String faceBigImage) {
        this.faceBigImage = faceBigImage;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    @Override
    public String toString() {
        return "FriendInfo{" +
                "itemId='" + itemId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", faceImage='" + faceImage + '\'' +
                ", faceBigImage='" + faceBigImage + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
