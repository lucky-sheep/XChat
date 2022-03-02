package cn.tim.xchat.controller;

import cn.tim.xchat.enums.ResultEnum;
import cn.tim.xchat.exception.XChatException;
import cn.tim.xchat.form.UserLoginForm;
import cn.tim.xchat.form.UserRegisterForm;
import cn.tim.xchat.service.UserAuthorizeService;
import cn.tim.xchat.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(value = "用户认证相关登录/注册", tags = "USER")
public class UserController {
    @Resource
    UserAuthorizeService userAuthorizeService;

    @ApiOperation(value = "用户注册接口", notes = "username与email都作为用户唯一标识，不可重复")
    @ApiParam(value = "用户注册表单")
    @PostMapping("/register")
    public R userRegister(@RequestBody @Valid UserRegisterForm form,
                          BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new XChatException(ResultEnum.PARAM_ERROR.getCode(),
                    Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        Map<String, Object> map = userAuthorizeService.userRegister(form);
        return R.ok().data(map);
    }


    @ApiOperation(value = "用户登录接口", notes = "username与email都作为用户唯一标识，不可重复")
    @ApiParam(value = "用户登录表单")
    @PostMapping("/login")
    public R userLogin(@RequestBody @Valid UserLoginForm form,
                          BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new XChatException(ResultEnum.PARAM_ERROR.getCode(),
                    Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        Map<String, Object> map = userAuthorizeService.userLogin(form);
        if(map != null && !ObjectUtils.isEmpty(map.get("token"))) {
            return R.ok().data(map);
        }
        return R.error(ResultEnum.PWD_LOGIN_FAILED);
    }
}
