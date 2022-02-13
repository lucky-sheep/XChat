package cn.tim.xchat.controller;

import cn.tim.xchat.enums.ResultEnum;
import cn.tim.xchat.exception.XChatException;
import cn.tim.xchat.form.UserRegisterForm;
import cn.tim.xchat.service.UserAuthorizeService;
import cn.tim.xchat.vo.R;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
public class UserController {
    @Resource
    UserAuthorizeService userAuthorizeService;

    @ApiOperation("用户注册接口")
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
}
