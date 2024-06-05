package top.tolan.auth.base.dto.polymorphism;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.tolan.auth.base.dto.LoginParentDTO;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PhoneLoginDTO extends LoginParentDTO {

    // 电话号码
    @NotBlank(message = "电话号码不能为空")
    private String phoneNumber;
    // 验证码
    @NotBlank(message = "验证码不能为空")
    private String smsCode;

}
