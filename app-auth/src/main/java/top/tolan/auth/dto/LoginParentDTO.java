package top.tolan.auth.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.tolan.auth.constant.LoginMethods;

/**
 * 登录表单
 * 使用@JsonTypeInfo实现请求数据对象多态
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
// 指定使用名称来标识子类，指定 JSON 中的属性名，使属性在反序列化时可见
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "loginMethod", visible = true)
@JsonSubTypes({
        // 指定子类及其对应的名称
        @JsonSubTypes.Type(value = UsernamePasswordLoginDTO.class, name = LoginMethods.USERNAME),
        @JsonSubTypes.Type(value = WechatLoginDTO.class, name = LoginMethods.WECHAT),
})
public class LoginParentDTO {

    // 登录方式
    @NotBlank(message = "登录方式不能为空")
    private String loginMethod;

}
