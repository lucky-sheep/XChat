package cn.tim.xchat.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "chat_msg")
public class ChatMsg {
    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "send_user_id", nullable = false, length = 64)
    private String sendUserId;

    @Column(name = "accept_user_id", nullable = false, length = 64)
    private String acceptUserId;

    @Column(name = "msg", nullable = false)
    private String msg;

    /**
     * {@link cn.tim.xchat.core.enums.MsgSignEnum}
     */
    @Column(name = "sign_flag", nullable = false)
    private Integer signFlag;

    @Column(name = "create_time", nullable = false)
    private Instant createTime;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatMsg chatMsg = (ChatMsg) o;
        return id != null && Objects.equals(id, chatMsg.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
