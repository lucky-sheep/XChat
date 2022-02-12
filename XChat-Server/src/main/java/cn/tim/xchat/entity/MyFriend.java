package cn.tim.xchat.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "my_friends")
public class MyFriend {
    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "my_user_id", nullable = false, length = 64)
    private String myUserId;

    @Column(name = "my_friend_id", nullable = false, length = 64)
    private String myFriendId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MyFriend myFriend = (MyFriend) o;
        return id != null && Objects.equals(id, myFriend.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}