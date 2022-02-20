package cn.tim.xchat.controller;

import cn.tim.xchat.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class FriendControllerTest {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void test(){
        assertNotNull(stringRedisTemplate);
        UUID uuid = UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, uuid),
                "37891078931", RedisConstant.EXPIRE, TimeUnit.SECONDS);

        String userId = stringRedisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, uuid));
        assertNotNull(userId);
        log.info("userId = " + userId);
    }
}