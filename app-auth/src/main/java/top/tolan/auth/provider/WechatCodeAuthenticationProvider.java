package top.tolan.auth.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.ITokenService;
import top.tolan.auth.token.WechatAuthorizationToken;

@Component
@RequiredArgsConstructor
public class WechatCodeAuthenticationProvider implements AuthenticationProvider {

    private final ITokenService wechatUserService;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WechatAuthorizationToken authorizationToken = (WechatAuthorizationToken) authentication;
        LoginUser loginUser = wechatUserService.appLogin(authorizationToken.getLoginCode(), authorizationToken.getPrincipal());
        authorizationToken.setAuthenticated(true);
        authorizationToken.setLoginUser(loginUser);
        return authorizationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthorizationToken.class.isAssignableFrom(authentication);
    }
}
