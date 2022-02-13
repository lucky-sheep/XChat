package cn.tim.xchat.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserRegisterForm {
    @NotBlank(message = "缺失设备ID")
    private String deviceId;

    @Email(message = "Email格式错误")
    private String email;

    @NotBlank(message = "缺失密码")
    @Length(min=5, max=15, message = "密码长度请保持在5-15个字符")
    private String password;

    @NotEmpty(message = "缺失用户名")
    @Length(min=5, max=15, message = "用户名长度请保持在5-15个字符")
    private String username;
}
