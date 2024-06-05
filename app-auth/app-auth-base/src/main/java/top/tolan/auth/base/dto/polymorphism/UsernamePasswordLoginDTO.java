package top.tolan.auth.base.dto.polymorphism;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.tolan.auth.base.dto.LoginParentDTO;

/**
 * 用户名密码登录DTO
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UsernamePasswordLoginDTO extends LoginParentDTO {

    // 账号
    @NotBlank(message = "用户账号不能为空")
    private String username;
    // 密码
    @NotBlank(message = "用户密码不能为空")
    private String password;
}
