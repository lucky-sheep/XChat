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
@Table(name = "user_info")
public class UserInfo {
    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "face_image", nullable = false)
    private String faceImage;

    @Column(name = "face_image_big", nullable = false)
    private String faceImageBig;

    @Column(name = "nickname", nullable = false, length = 64)
    private String nickname;

    @Column(name = "qrcode", nullable = false)
    private String qrcode;

    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserInfo userInfo = (UserInfo) o;
        return id != null && Objects.equals(id, userInfo.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}