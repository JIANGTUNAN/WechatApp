package top.tolan.auth.service.impl;

import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import top.tolan.auth.context.AuthenticationContextHolder;
import top.tolan.auth.dto.LoginDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.ITokenService;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.constant.HttpStatus;

@Service
public class UserNameAuthServerImpl implements IAuthServer {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private ITokenService tokenService;

    /**
     * 登录
     */
    @Override
    public AjaxResult login(LoginDTO loginDTO) {
        // 用户验证
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
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

}
