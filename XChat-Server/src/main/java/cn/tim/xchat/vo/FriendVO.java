package cn.tim.xchat.vo;

import lombok.Data;

@Data
public class FriendVO {
    private int id;
    private String userid;
    private String username;
    private String nickname;
    private String email;
    private String faceImage;
    private String faceBigImage;
    private String qrCode;
    private String notes; // 备注名
}
