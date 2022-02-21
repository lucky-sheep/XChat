package cn.tim.xchat.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendRequestVO extends FriendVO {
    private int argeeState;
}
