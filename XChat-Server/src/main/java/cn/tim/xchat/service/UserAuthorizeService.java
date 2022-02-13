package cn.tim.xchat.service;

import cn.tim.xchat.form.UserRegisterForm;

import java.util.Map;

public interface UserAuthorizeService {
    /**
     * 用户注册
     * @param form
     * @return 如果注册成功就返回Token
     */
    Map<String, Object> userRegister(UserRegisterForm form);
}
