package cn.tim.xchat.common.module;

import androidx.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

public class FriendRequest extends LitePalSupport implements FriendObtain{
    private String itemId;
    private String userId;
    private String username;
    private String nickname;
    private String email;
    private String faceImage;
    private String faceBigImage;
    private String qrCode;
    private int argeeState;
    /**
     * 0 true 1 false
     */
    private int isMyRequest; // 0 true 1 false

    public FriendRequest() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    @Override
    public String getNotes() {
        return null;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }


    public int getArgeeState() {
        return argeeState;
    }

    public void setArgeeState(int argeeState) {
        this.argeeState = argeeState;
    }

    public void setIsMyRequest(int isMyRequest) {
        this.isMyRequest = isMyRequest;
    }

    public int getIsMyRequest() {
        return isMyRequest;
    }

    @NonNull
    @Override
    public String toString() {
        return "FriendRequest{" +
                "itemId='" + itemId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", faceImage='" + faceImage + '\'' +
                ", faceBigImage='" + faceBigImage + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", argeeState=" + argeeState +
                '}';
    }
}
