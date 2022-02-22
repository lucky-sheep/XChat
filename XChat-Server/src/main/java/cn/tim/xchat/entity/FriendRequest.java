package cn.tim.xchat.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "friend_request", indexes = {
        @Index(name = "senduser_index", columnList = "send_user_id", unique = false),
        @Index(name = "acceptuser_index", columnList = "accept_user_id", unique = false),
})
@Proxy(lazy = false)
public class FriendRequest {
    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "send_user_id", length = 64)
    private String sendUserId;

    @Column(name = "accept_user_id", length = 64)
    private String acceptUserId;

    @Column(name = "argee_ret", columnDefinition = "TINYINT", length = 2)
    private Integer argeeRet;

    @Column(name = "request_datetime")
    private Instant requestDatetime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FriendRequest that = (FriendRequest) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}