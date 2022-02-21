package cn.tim.xchat.vo;

import lombok.Data;

@Data
public class FriendVO {
    protected String itemId;
    protected String userId;
    protected String username;
    protected String nickname;
    protected String email;
    protected String faceImage;
    protected String faceBigImage;
    protected String qrCode;
    protected String notes; // 备注名
}
