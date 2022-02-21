package cn.tim.xchat.enums.business;

import lombok.Getter;

/**
 * 请求添加好友的三种状态
 */
@Getter
public enum RequestFriendEnum {
    UNHAND, // 未处理
    AGREE, // 同意
    REFUSE // 拒绝
}
