package top.tolan.auth.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送登录验证码DTO
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendLoginCodeDTO {

    // 接收短信的手机号
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    // 短信类型
    @NotBlank(message = "短信类型不能为空")
    private String smsType;

}
