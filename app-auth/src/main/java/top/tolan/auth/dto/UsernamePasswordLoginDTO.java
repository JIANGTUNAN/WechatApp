package top.tolan.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
