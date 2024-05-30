package top.tolan.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.tolan.auth.constant.LoginMethods;
import top.tolan.auth.dto.LoginParentDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.ITokenService;
import top.tolan.auth.service.impl.EmailLoginServiceImpl;
import top.tolan.auth.service.impl.PhoneLoginServiceImpl;
import top.tolan.auth.service.impl.UserNameAuthServerImpl;
import top.tolan.auth.service.impl.WechatLoginServiceImpl;
import top.tolan.common.domain.AjaxResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Map<String, IAuthServer> loginServiceMap;

    private final ITokenService tokenService;

    /**
     * 构造方法注入
     */
    @Autowired
    public AuthController(ITokenService tokenService, IAuthServer... services) {
        this.tokenService = tokenService;
        loginServiceMap = new HashMap<>();
        Arrays.stream(services).forEach(service -> {
            if (service instanceof UserNameAuthServerImpl) {
                loginServiceMap.put(LoginMethods.USERNAME, service);
            } else if (service instanceof WechatLoginServiceImpl) {
                loginServiceMap.put(LoginMethods.WECHAT, service);
            } else if (service instanceof PhoneLoginServiceImpl) {
                loginServiceMap.put(LoginMethods.PHONE, service);
            } else if (service instanceof EmailLoginServiceImpl) {
                loginServiceMap.put(LoginMethods.EMAIL, service);
            }
        });
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody @Validated LoginParentDTO loginParentDTO) {
        IAuthServer service = loginServiceMap.get(loginParentDTO.getLoginMethod());
        return service.login(loginParentDTO);
    }

    /**
     * 登出
     */
    @GetMapping("/logout")
    public AjaxResult outLogin(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        IAuthServer iAuthServer = loginServiceMap.get(loginUser.getLoginMethod());
        return iAuthServer.logout(loginUser);
    }

}
