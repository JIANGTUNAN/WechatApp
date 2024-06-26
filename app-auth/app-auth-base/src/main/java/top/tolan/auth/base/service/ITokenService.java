package top.tolan.auth.base.service;

import jakarta.servlet.http.HttpServletRequest;
import top.tolan.auth.base.entity.LoginUser;

/**
 * 身份令牌接口
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
public interface ITokenService {

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
