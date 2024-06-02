package top.tolan.auth.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.tolan.auth.dto.LoginParentDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.AuthServerFactory;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.ITokenService;
import top.tolan.common.domain.AjaxResult;

/**
 * 验证控制器
 *
 * @author tooooolan
 * @version 2024年5月30日
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private ITokenService tokenService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody @Validated LoginParentDTO loginParentDTO) {
        IAuthServer authServer = AuthServerFactory.getAuthServer(loginParentDTO.getLoginMethod());
        return authServer.login(loginParentDTO);
    }

    /**
     * 登出
     */
    @GetMapping("/logout")
    public AjaxResult outLogin(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        IAuthServer authServer = AuthServerFactory.getAuthServer(loginUser.getLoginMethod());
        return authServer.logout(loginUser);
    }

}
