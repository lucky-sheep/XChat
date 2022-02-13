package cn.tim.xchat.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "用户注册表单")
@Data
public class UserRegisterForm {
    @ApiModelProperty(value = "设备ID")
    @NotBlank(message = "缺失设备ID")
    private String deviceId;

    @ApiModelProperty(value = "E-mail")
    @Email(message = "Email格式错误")
    @NotBlank(message = "缺失E-mail")
    @Length(min=2, max=64, message = "邮件长度请保持在2-64个字符")
    private String email;

    @ApiModelProperty(value = "用户密码")
    @NotBlank(message = "缺失密码")
    @Length(min=8, max=64, message = "密码长度请保持在8-64个字符")
    private String password;

    @ApiModelProperty(value = "用户名")
    @NotEmpty(message = "缺失用户名")
    @Length(min=5, max=15, message = "用户名长度请保持在5-15个字符")
    private String username;
}
