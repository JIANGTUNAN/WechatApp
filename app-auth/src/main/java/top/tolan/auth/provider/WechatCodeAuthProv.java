package top.tolan.auth.provider;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import top.tolan.auth.constant.LoginMethods;
import top.tolan.auth.entity.LoginDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.ITokenService;
import top.tolan.auth.token.WechatAuthorizationToken;
import top.tolan.common.entity.po.SysUser;
import top.tolan.common.exception.ServiceException;
import top.tolan.system.service.ISysUserService;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * 微信小程序登录验证提供者
 *
 * @author tooooolan
 * @version 2024年5月30日
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatCodeAuthProv implements AuthenticationProvider {

    // 小程序的AppID
    @Value("${Wechat.AppID}")
    private String APP_ID;
    // 小程序密钥
    @Value("${Wechat.AppSecret}")
    private String APP_SECRET;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ISysUserService userService;
    // 允许的code类型，防止参数入侵。
    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-]+$");

    /**
     * 注意这里的supports方法，是实现多种认证方式的关键。
     * 认证管理器AuthenticationManager会通过这个supports方法来判定当前需要使用哪一种认证方式。
     * 调用supports方法判断当前的Authentication实现类是否是{@link  WechatAuthorizationToken}
     *
     * @param authenticationClass 认证器类
     * @return 是否支持
     */
    @Override
    public boolean supports(Class<?> authenticationClass) {
        return WechatAuthorizationToken.class.isAssignableFrom(authenticationClass);
    }

    /**
     * 调用authenticate方法进行认证
     *
     * @param authentication 认证器
     * @return 认证结果
     * @throws AuthenticationException 认证异常
     */
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WechatAuthorizationToken authorizationToken = (WechatAuthorizationToken) authentication;
        LoginUser loginUser = this.appLogin(authorizationToken.getLoginCode(), authorizationToken.getPrincipal());
        authorizationToken.setAuthenticated(true);
        authorizationToken.setLoginUser(loginUser);
        return authorizationToken;
    }


    /**
     * 微信用户授权小程序登录验证
     */
    public LoginUser appLogin(String code, LoginUser loginUser) {
        // 构建请求地址
        URI uri = this.buildWeChatApiUri(code);
        try {
            String response = restTemplate.getForObject(uri, String.class);
            LoginDTO loginDTO = JSON.parseObject(response, LoginDTO.class);
            assert ObjectUtils.isNotEmpty(loginDTO);
            if (loginDTO.getErrcode() == null) {
                String openid = loginDTO.getOpenid();
                SysUser sysUser = loginUser.getSysUser();
                sysUser.setOpenId(openid);
                sysUser = userService.addUserOnNotPresence(sysUser);
                loginUser.setSysUser(sysUser);
                loginUser.setLoginTime(System.currentTimeMillis());
                loginUser.setExpireTime(loginUser.getLoginTime() + 30 * 60 * 1000L);
                loginUser.setLoginMethod(LoginMethods.WECHAT);
                return loginUser;
            } else {
                log.error("微信登录失败, 错误码: {}, 错误消息: {}", loginDTO.getErrcode(), loginDTO.getErrmsg());
                throw new ServiceException("微信登录失败: " + loginDTO.getErrmsg());
            }
        } catch (Exception e) {
            log.error("登录失败：{}", e.getMessage(), e);
            throw new ServiceException("登录异常，请稍后再试");
        }
    }

    /**
     * 构造微信授权认证链接
     */
    public URI buildWeChatApiUri(String code) {
        // 验证 code 是否符合预期格式
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new ServiceException("微信Code错误");
        }
        return UriComponentsBuilder.fromUriString("https://api.weixin.qq.com/sns/jscode2session")
                .queryParam("appid", APP_ID)
                .queryParam("secret", APP_SECRET)
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .build()
                .toUri();
    }

}
