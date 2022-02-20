package cn.tim.xchat.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@ApiModel(value = "用户登录表单")
@Data
public class UserLoginForm {
    @ApiModelProperty(value = "设备ID")
    @NotBlank(message = "缺失设备ID")
    private String deviceId;

    @ApiModelProperty(value = "用户密码")
    @NotBlank(message = "缺失密码")
    @Length(min=8, max=64, message = "密码长度请保持在8-64个字符")
    private String password;

    @ApiModelProperty(value = "用户名")
    @NotEmpty(message = "缺失用户名")
    @Length(min=2, max=15, message = "用户名长度请保持在2-15个字符")
    private String username;
}
