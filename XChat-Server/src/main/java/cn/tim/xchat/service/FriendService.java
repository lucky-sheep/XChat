package cn.tim.xchat.service;

import cn.tim.xchat.vo.FriendRequestVO;
import cn.tim.xchat.vo.FriendVO;

import java.util.List;

public interface FriendService {
    List<FriendVO> getAllFriend(String userId);

    FriendRequestVO requestFriend(String userId, String reqUserNameOrEmail);
}
