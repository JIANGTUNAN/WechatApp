package top.tolan.auth.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;
import top.tolan.auth.handler.LoginSuccessHandler;
import top.tolan.auth.provider.WechatCodeAuthProv;
import top.tolan.auth.token.WechatAuthorizationToken;

/**
 * 微信登录过滤器
 */
@Component
public class WechatLoginFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 设置过滤器管理器和登录成功处理器
     *
     * @param wechatCodeAuthProvider 微信登录认证提供者
     * @param loginSuccessHandler    登录成功处理器
     */
    public WechatLoginFilter(WechatCodeAuthProv wechatCodeAuthProvider, LoginSuccessHandler loginSuccessHandler) {
        super("/app/login");
        this.setAuthenticationManager(new ProviderManager(wechatCodeAuthProvider));
        this.setAuthenticationSuccessHandler(loginSuccessHandler);
    }

    /**
     * 尝试进行身份验证
     *
     * @param request  请求
     * @param response 响应
     * @return 经过身份验证的登录令牌
     * @throws AuthenticationException 身份验证异常
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 检查请求方法，如果不是POST方法，则抛出异常
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 从请求参数中获取code
        String code = request.getParameter("code");
        // 微信头像
        String wxHeadPic = request.getParameter("avatarUrl");
        // 微信昵称
        String wxNickName = request.getParameter("nickName");
        // 创建微信授权登录验证令牌
        WechatAuthorizationToken authorizationToken = new WechatAuthorizationToken(code, wxHeadPic, wxNickName);
        // 设置登录令牌的详细信息
        authorizationToken.setDetails(this.authenticationDetailsSource.buildDetails(request));
        // 使用身份验证管理器进行身份验证
        this.getAuthenticationManager().authenticate(authorizationToken);
        // 返回经过身份验证的登录令牌
        return authorizationToken;
    }

}
