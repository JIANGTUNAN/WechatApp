package top.tolan.system.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditUserDto {

    /**
     * 主键
     */
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    /**
     * 用户手机号码
     */
    @Size(max = 11, message = "手机号码长度不能超过11位")
    private String phone;

    /**
     * 用户性别 1：男；2：女；3：未知
     */
    @NotBlank(message = "性别不能为空")
    private String sex;

    /**
     * 用户系统昵称
     */
    @NotBlank(message = "系统昵称不能为空")
    @Size(max = 20, message = "系统昵称长度不能超过20位")
    private String sysNickName;

}
