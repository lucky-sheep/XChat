package cn.tim.xchat.controller;

import cn.tim.xchat.constant.RedisConstant;
import cn.tim.xchat.enums.ResultEnum;
import cn.tim.xchat.exception.XChatException;
import cn.tim.xchat.service.FriendService;
import cn.tim.xchat.vo.FriendVO;
import cn.tim.xchat.vo.R;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/friend")
public class FriendController {
    @Resource
    FriendService friendService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "请求好友列表接口", notes = "")
    @GetMapping("/list")
    public R userGetOneAllFriends(@RequestHeader(name = "token", required = true) String token){
        //              stringRedisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, uuid))
        String userId = stringRedisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, token));
        if(!ObjectUtils.isEmpty(userId)) {
            List<FriendVO> allFriend = friendService.getAllFriend(userId);
            return R.ok().data("friends", allFriend);
        }else {
            throw new XChatException(ResultEnum.TOKEN_OVERDUE);
        }
    }

    @ApiOperation(value = "申请好友接口", notes = "")
    @PostMapping("/request")
    public R requestFriend(@RequestHeader(name = "token", required = true) String token,
                           @RequestBody Map<String, Object> map){
        String username = (String) map.get("username");
        String userId = stringRedisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, token));
        if(!ObjectUtils.isEmpty(userId)) {
            friendService.requestFriend(userId, username);
            return R.ok().message("申请成功");
        }else {
            throw new XChatException(ResultEnum.TOKEN_OVERDUE);
        }
    }
}
