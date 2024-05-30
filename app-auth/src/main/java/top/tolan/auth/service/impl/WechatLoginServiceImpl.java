package top.tolan.auth.service.impl;

import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import top.tolan.auth.context.AuthenticationContextHolder;
import top.tolan.auth.dto.LoginParentDTO;
import top.tolan.auth.dto.UsernamePasswordLoginDTO;
import top.tolan.auth.dto.WechatLoginDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.provider.UsernamePasswordAuthProv;
import top.tolan.auth.provider.WechatCodeAuthProv;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.ITokenService;
import top.tolan.auth.token.WechatAuthorizationToken;
import top.tolan.common.constant.HttpStatus;
import top.tolan.common.domain.AjaxResult;

/**
 * 登录服务实现类（微信授权）
 *
 * @author tooooolan
 * @version 2024年5月30日
 */
@Service
public class WechatLoginServiceImpl implements IAuthServer {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private ITokenService tokenService;

    /**
     * 用户名密码登录验证
     * 提供者{@link WechatCodeAuthProv#authenticate(Authentication)}
     *
     * @param t 登录信息接收
     * @return 验证失败信息或成功身份令牌
     */
    @Override
    public <T extends LoginParentDTO> AjaxResult login(T t) {
        if (t instanceof WechatLoginDTO wechatLoginDTO) {
            Authentication authentication;
            try {
                // 创建微信授权登录验证令牌
                WechatAuthorizationToken wechatAuthorizationToken =
                        new WechatAuthorizationToken(wechatLoginDTO.getCode(), wechatLoginDTO.getWxHeadPic(), wechatLoginDTO.getWxNickName());
                AuthenticationContextHolder.setContext(wechatAuthorizationToken);
                authentication = authenticationManager.authenticate(wechatAuthorizationToken);
            } catch (Exception e) {
                if (e instanceof BadCredentialsException) {
                    return AjaxResult.error(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
                } else {
                    return AjaxResult.error(e.getMessage());
                }
            } finally {
                AuthenticationContextHolder.clearContext();
            }
            // 登录成功后获取构建好的用户信息
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            String token = tokenService.createToken(loginUser);
            return AjaxResult.success("登录成功", token);
        }
        return AjaxResult.error("登录失败");
    }


}
