package cn.tim.xchat.repository;

import cn.tim.xchat.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    FriendRequest findFriendRequestBySendUserIdAndAcceptUserId(String sendUserId, String acceptUserId);


    List<FriendRequest> findAllByAcceptUserId(String acceptUserId);

//    List<FriendRequest> findAllByAcceptUserIdAAnd
}