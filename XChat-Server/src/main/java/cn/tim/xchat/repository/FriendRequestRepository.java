package cn.tim.xchat.repository;

import cn.tim.xchat.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    FriendRequest findFriendRequestBySendUserIdAndAcceptUserId(String sendUserId, String acceptUserId);
}