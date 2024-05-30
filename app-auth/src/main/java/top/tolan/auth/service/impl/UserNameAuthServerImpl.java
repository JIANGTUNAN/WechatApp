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
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.provider.UsernamePasswordAuthProv;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.ITokenService;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.constant.HttpStatus;

/**
 * 登录服务实现类（账号密码）
 *
 * @author tooooolan
 * @version 2024年5月30日
 */
@Service
public class UserNameAuthServerImpl extends BaseAuthServer implements IAuthServer {

    private final AuthenticationManager authenticationManager;
    private final ITokenService tokenService;

    public UserNameAuthServerImpl(
            ITokenService tokenService,
            AuthenticationManager authenticationManager
    ) {
        super(tokenService);
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }


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
            String token = tokenService.createToken(loginUser);
            return AjaxResult.success("登录成功", token);
        }
        return AjaxResult.error("登录失败");
    }

}
