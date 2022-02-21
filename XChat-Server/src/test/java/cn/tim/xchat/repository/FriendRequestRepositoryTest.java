package cn.tim.xchat.repository;

import cn.tim.xchat.entity.FriendRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class FriendRequestRepositoryTest {
    @Resource
    FriendRequestRepository requestRepository;

    @Test
    public void test(){
        List<FriendRequest> all = requestRepository.findAll();
        log.info("ret size = " + all.size());
    }
}