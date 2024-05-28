package top.tolan.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录表单
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    // 登录方式
    @NotBlank(message = "登录方式不能为空")
    private String loginMethod;

    // 账号
    private String username;
    // 密码
    private String password;


}
