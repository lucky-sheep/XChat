package cn.tim.xchat.repository;

import cn.tim.xchat.entity.UserInfo;
import cn.tim.xchat.utils.KeyUtil;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserInfoRepositoryTest {
    @Resource
    UserInfoRepository repository;

    List<String> idList = Lists.newArrayList();
    List<String> nameList = Lists.newArrayList();
    List<String> emailList = Lists.newArrayList();

    @Before
    public void initData(){
        for (int i = 0; i < 30; i++) {
            UserInfo userInfo = new UserInfo();
            String idKey = KeyUtil.genUniqueKey();
            String nameKey = KeyUtil.genUniqueKey();
            String emailKey = KeyUtil.genUniqueKey();

            idList.add(idKey);
            nameList.add(nameKey);
            emailList.add(emailKey);

            userInfo.setId(idKey);
            userInfo.setUsername(nameKey);
            userInfo.setEmail(emailKey + "@163.com");
            userInfo.setPassword(KeyUtil.genUniqueKey());
            repository.save(userInfo);
        }
    }

    @Test
    public void findUserInfo() {
        for(String id: idList){
            Assertions.assertNotNull(repository.getById(id));
        }

        findUserInfoByUsername();
        findUserInfoByEmail();

        Assertions.assertNull(repository.findUserInfoByUsername("x"));
    }

    public void findUserInfoByUsername() {
        for(String name: nameList){
            UserInfo userInfoByUsername = repository.findUserInfoByUsername(name);
            Assertions.assertNotNull(userInfoByUsername);
        }
    }

    public void findUserInfoByEmail() {
        for(String email: emailList){
            UserInfo userInfoByUsername = repository.findUserInfoByEmail(email+ "@163.com");
            Assertions.assertNotNull(userInfoByUsername);
        }
    }

    @After
    public void clearData(){
        repository.deleteAllById(idList);
    }
}