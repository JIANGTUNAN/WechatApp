package top.tolan.auth.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.ITokenService;
import top.tolan.auth.token.WechatAuthorizationToken;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.utils.ServletUtils;

/**
 * 验证成功处理器
 */
@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private ITokenService wechatUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        if (authentication instanceof WechatAuthorizationToken
            && authentication.getPrincipal() instanceof LoginUser loginUser) {
            String token = wechatUserService.createToken(loginUser);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", token);
            jsonObject.put("sysUser", loginUser.getSysUser());
            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success(jsonObject)));
        }
    }

}
