package cn.tim.xchat.common.module;

import org.litepal.crud.LitePalSupport;

public class FriendInfo extends LitePalSupport {
    private int id;
    private String userid;
    private String username;
    private String nickname;
    private String email;
    private String faceImage;
    private String faceBigImage;
    private String qrCode;
    private String notes;

    public FriendInfo(){

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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
}
