package top.tolan.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import top.tolan.auth.entity.LoginUser;

public interface ITokenService {

    // 小程序授权登录验证
    public LoginUser appLogin(String code, LoginUser sysUser);

    // 创建token
    public String createToken(LoginUser loginUser);

    /**
     * 获取用户身份信息
     */
    public LoginUser getLoginUser(HttpServletRequest request);

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     */
    public void verifyToken(LoginUser loginUser);

    /**
     * 注销登录
     */
    public void delLoginUser(String token);

}
