package cn.tim.xchat.repository;

import cn.tim.xchat.entity.MyFriend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyFriendRepository extends JpaRepository<MyFriend, String> {
}