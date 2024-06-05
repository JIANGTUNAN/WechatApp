package top.tolan.auth.sms.service.impl;

import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import top.tolan.auth.base.constant.LoginMethods;
import top.tolan.auth.base.context.AuthenticationContextHolder;
import top.tolan.auth.base.dto.LoginParentDTO;
import top.tolan.auth.base.dto.polymorphism.PhoneLoginDTO;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.auth.base.service.base.BaseAuthService;
import top.tolan.auth.sms.provider.SMSAuthProv;
import top.tolan.auth.sms.token.SMSAuthorizationToken;
import top.tolan.common.constant.HttpStatus;
import top.tolan.common.domain.AjaxResult;

/**
 * 手机信息验证码登录
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Service(LoginMethods.PHONE)
public class SMSAuthServiceImpl extends BaseAuthService {

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 手机信息验证码登录验证
     * 提供者{@link SMSAuthProv#authenticate(Authentication)}
     *
     * @param t 登录信息接收
     * @return 验证失败信息或成功身份令牌
     */
    @Override
    public <T extends LoginParentDTO> AjaxResult login(T t) {
        if (t instanceof PhoneLoginDTO phoneLoginDTO) {
            Authentication authentication;
            try {
                SMSAuthorizationToken authorizationToken =
                        new SMSAuthorizationToken(phoneLoginDTO.getPhoneNumber(), phoneLoginDTO.getSmsCode());
                AuthenticationContextHolder.setContext(authorizationToken);
                authentication = authenticationManager.authenticate(authorizationToken);
            } catch (Exception e) {
                return AjaxResult.error(e.getMessage());
            } finally {
                AuthenticationContextHolder.clearContext();
            }
            // 登录成功后获取构建好的用户信息
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            String token = super.tokenService.createToken(loginUser);
            return AjaxResult.success("登录成功", token);
        }
        return AjaxResult.error("登录失败");
    }

}
