package cn.tim.xchat.repository;

import cn.tim.xchat.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    UserInfo findUserInfoByUsername(String name);

    UserInfo findUserInfoByEmail(String email);
}