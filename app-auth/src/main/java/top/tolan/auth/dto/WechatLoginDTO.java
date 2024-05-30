package top.tolan.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
