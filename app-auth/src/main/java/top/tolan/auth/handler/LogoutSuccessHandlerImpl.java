package top.tolan.auth.handler;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.ITokenService;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.utils.ServletUtils;


/**
 * 自定义退出处理类 返回成功
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Resource
    private ITokenService wechatUserService;

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoginUser loginUser = wechatUserService.getLoginUser(request);
        if (ObjectUtils.isNotEmpty(loginUser)) {
            // 删除用户缓存记录
            wechatUserService.delLoginUser(loginUser.getToken());
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success("退出成功")));
    }

}
