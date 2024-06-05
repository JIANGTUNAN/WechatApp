package top.tolan.auth.base.dto.polymorphism;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.tolan.auth.base.dto.LoginParentDTO;

/**
 * 微信授权登录DTO
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WechatLoginDTO extends LoginParentDTO {

    // 微信code
    @NotBlank(message = "微信code不能为空")
    private String code;
    // 微信头像
    private String wxHeadPic;
    // 微信昵称
    private String wxNickName;

}
