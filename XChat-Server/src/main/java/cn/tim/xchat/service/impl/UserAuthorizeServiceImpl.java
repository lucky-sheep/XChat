package cn.tim.xchat.service.impl;

import cn.tim.xchat.constant.RedisConstant;
import cn.tim.xchat.entity.UserInfo;
import cn.tim.xchat.enums.ResultEnum;
import cn.tim.xchat.exception.XChatException;
import cn.tim.xchat.form.UserLoginForm;
import cn.tim.xchat.form.UserRegisterForm;
import cn.tim.xchat.repository.UserInfoRepository;
import cn.tim.xchat.service.UserAuthorizeService;
import cn.tim.xchat.utils.KeyUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserAuthorizeServiceImpl implements UserAuthorizeService {
    @Resource
    UserInfoRepository userInfoRepository;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Map<String, Object> userRegister(UserRegisterForm form) {
        UserInfo byUsername = userInfoRepository.findUserInfoByUsername(form.getUsername());
        UserInfo byUserEmail = userInfoRepository.findUserInfoByEmail(form.getEmail());
        if(byUsername != null) {
            // 已经存在此用户了
            throw new XChatException(ResultEnum.USERNAME_ALREADY_EXISTS);
        }

        if(byUserEmail != null) {
            // 邮箱已经被占用
            throw new XChatException(ResultEnum.EMAIL_ALREADY_EXISTS);
        }

        // 检查客户端重复请求
        Integer optCount = redisTemplate.opsForValue().get(form.getDeviceId());
        if(optCount != null && optCount++ > 2) {
            throw new XChatException(ResultEnum.SAME_DEVICE_REPEAT_REQUEST);
        }
        redisTemplate.opsForValue().set(form.getDeviceId(), optCount != null ? optCount: 1,
                RedisConstant.BLACKLIST_INVALID, TimeUnit.SECONDS);

        // 保存信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(KeyUtil.genUserKey());
        userInfo.setUsername(form.getUsername());
        userInfo.setEmail(form.getEmail());
        userInfo.setPassword(form.getPassword());
        userInfo.setClientId(form.getDeviceId());

        UserInfo savedUser = userInfoRepository.save(userInfo);

        HashMap<String, Object> ret = new HashMap<>();
        String token = UUID.randomUUID().toString();

        // 保存Token到Redis
        stringRedisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token),
                savedUser.getId(), RedisConstant.EXPIRE, TimeUnit.SECONDS);

        ret.put("userInfo", savedUser);
        ret.put("token", token);
        return ret;
    }

    @Override
    public Map<String, Object> userLogin(UserLoginForm form) {
        HashMap<String, Object> ret = Maps.newHashMap();
        Integer loginCount = redisTemplate.opsForValue().get(form.getUsername());
        if(loginCount != null && loginCount++ > 5) {
            // 请求次数过多
            throw new XChatException(ResultEnum.PWD_ERROR_TOO_MANY_TIMES);
        }else {
            log.info("form.getUsername() = " + form.getUsername() + ", loginCount = " + loginCount);
            redisTemplate.opsForValue().set(form.getUsername(), loginCount != null ? loginCount : 1,
                    RedisConstant.PASSWORD_ERROR_EXPIRE, TimeUnit.SECONDS);
        }

        UserInfo userInfo = userInfoRepository.findUserInfoByUsername(form.getUsername());
        if(userInfo != null) {
            if(userInfo.getPassword().equals(form.getPassword())){
                // 先获取Redis里面的Token，否则存在Token重复的问题
                String token = UUID.randomUUID().toString();
                stringRedisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token),
                        userInfo.getId(), RedisConstant.EXPIRE, TimeUnit.SECONDS);
                ret.put("token", token);
                ret.put("userInfo", userInfo);

                // 记录一下设备变更
                if(!userInfo.getClientId().equals(form.getDeviceId())){
                    // TODO 记录一下设备变更
                }
                return ret;
            }
        }
        return null;
    }
}
