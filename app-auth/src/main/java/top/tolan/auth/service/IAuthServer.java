package top.tolan.auth.service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.tolan.auth.dto.LoginDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.utils.SpringUtils;

/**
 * 用户登录授权服务
 */
public interface IAuthServer {


    /**
     * 登录
     */
    public AjaxResult login(LoginDTO loginDTO);

    /**
     * 登出
     */
    public default AjaxResult logout(@NonNull LoginUser loginUser) {
        ITokenService tokenService = SpringUtils.getBean(ITokenService.class);
        // 删除用户缓存记录
        tokenService.delLoginUser(loginUser.getUuid());
        return AjaxResult.success("退出成功");
    }

}
