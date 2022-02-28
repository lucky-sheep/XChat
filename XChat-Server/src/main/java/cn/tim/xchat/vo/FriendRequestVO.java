package cn.tim.xchat.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendRequestVO extends FriendVO {
    private int argeeState;

    private int isMyRequest; // 0 true 1 false
}
