package cn.tim.xchat.repository;

import cn.tim.xchat.entity.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, String>, JpaSpecificationExecutor<ChatMsg> {
}