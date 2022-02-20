package cn.tim.xchat.repository;

import cn.tim.xchat.entity.MyFriend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFriendRepository extends JpaRepository<MyFriend, String> {
    List<MyFriend> findAllByMyUserId(String myUserId);

    MyFriend findMyFriendByMyUserIdAndMyFriendId(String userId, String myFriendId);
}