package top.tolan.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序登录返回数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    // 用户唯一标识
    private String openid;
    // 会话密钥
    private String session_key;
    // 用户在开放平台的唯一标识符
    private String unionid;
    // 错误信息
    private String errmsg;
    // 错误码
    // 40029：js_code无效
    // 45011：API 调用太频繁，请稍候再试
    // 40226：高风险等级用户，小程序登录拦截
    // -1：系统繁忙，此时请开发者稍候再试
    private Integer errcode;

}
