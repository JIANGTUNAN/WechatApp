package top.tolan.auth.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import top.tolan.auth.constant.LoginMethods;
import top.tolan.auth.service.base.BaseAuthServer;

import java.util.Map;

/**
 * 验证服务静态工厂
 *
 * @author tooooolan
 * @version 2024年6月1日
 */
@Component
public class AuthServerFactory {

    @Resource
    private Map<String, BaseAuthServer> loginServiceMap;

    private static Map<String, BaseAuthServer> staticLoginServiceMap;

    /**
     * 初始化静态登录服务
     */
    @PostConstruct
    public void init() {
        staticLoginServiceMap = loginServiceMap;
    }

    /**
     * 获取登录服务
     */
    public static IAuthServer getAuthServer(String loginMethod) {
        return switch (loginMethod) {
            case LoginMethods.PHONE -> staticLoginServiceMap.get(LoginMethods.PHONE);
            case LoginMethods.EMAIL -> staticLoginServiceMap.get(LoginMethods.EMAIL);
            case LoginMethods.WECHAT -> staticLoginServiceMap.get(LoginMethods.WECHAT);
            case LoginMethods.USERNAME -> staticLoginServiceMap.get(LoginMethods.USERNAME);
            default -> throw new IllegalStateException("没有找到对应的登录方式");
        };
    }

}
