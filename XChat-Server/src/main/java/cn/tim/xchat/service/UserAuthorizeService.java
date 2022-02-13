package cn.tim.xchat.service;

import cn.tim.xchat.form.UserLoginForm;
import cn.tim.xchat.form.UserRegisterForm;

import java.util.Map;

public interface UserAuthorizeService {
    /**
     * 用户注册
     * @return 如果注册成功就返回Token
     */
    Map<String, Object> userRegister(UserRegisterForm form);

    /**
     * 用户登录
     * @return 如果登录成功就返回Token
     */
    Map<String, Object> userLogin(UserLoginForm form);
}
