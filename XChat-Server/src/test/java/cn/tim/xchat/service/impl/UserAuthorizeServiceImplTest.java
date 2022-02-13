package cn.tim.xchat.service.impl;

import cn.tim.xchat.form.UserLoginForm;
import cn.tim.xchat.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserAuthorizeServiceImplTest {
    @Resource
    UserAuthorizeServiceImpl userAuthorizeService;

    @Resource
    RedisTemplate<String, Integer> redisTemplate;

    @Test
    public void userLogin() {
        Integer integer = redisTemplate.opsForValue().get(KeyUtil.genUniqueKey());
        assertNull(integer);
    }
}