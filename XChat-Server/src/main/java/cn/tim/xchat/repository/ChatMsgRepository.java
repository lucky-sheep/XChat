package cn.tim.xchat.repository;

import cn.tim.xchat.entity.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, String>, JpaSpecificationExecutor<ChatMsg> {
    List<ChatMsg> findAllByAcceptUserIdAndSignFlag(String acceptUserId, int signFlag);
}
