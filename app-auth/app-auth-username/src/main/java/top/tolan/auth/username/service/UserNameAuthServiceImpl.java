package top.tolan.auth.username.service;

import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import top.tolan.auth.base.constant.LoginMethods;
import top.tolan.auth.base.context.AuthenticationContextHolder;
import top.tolan.auth.base.dto.LoginParentDTO;
import top.tolan.auth.base.dto.polymorphism.UsernamePasswordLoginDTO;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.auth.base.service.base.BaseAuthService;
import top.tolan.auth.username.provider.UsernamePasswordAuthProv;
import top.tolan.common.constant.HttpStatus;
import top.tolan.common.domain.AjaxResult;

/**
 * 登录服务实现类（账号密码）
 *
 * @author tooooolan
 * @version 2024年5月30日
 */
@Service(LoginMethods.USERNAME)
public class UserNameAuthServiceImpl extends BaseAuthService {

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 用户名密码登录验证
     * 提供者{@link UsernamePasswordAuthProv#loadUserByUsername(String)}
     *
     * @param t 登录信息接收
     * @return 验证失败信息或成功身份令牌
     */
    @Override
    public <T extends LoginParentDTO> AjaxResult login(T t) {
        if (t instanceof UsernamePasswordLoginDTO usernamePasswordLoginDto) {
            Authentication authentication;
            try {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(usernamePasswordLoginDto.getUsername(), usernamePasswordLoginDto.getPassword());
                AuthenticationContextHolder.setContext(authenticationToken);
                authentication = authenticationManager.authenticate(authenticationToken);
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
            String token = super.tokenService.createToken(loginUser);
            return AjaxResult.success("登录成功", token);
        }
        return AjaxResult.error("登录失败");
    }

}
