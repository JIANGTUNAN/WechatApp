package top.tolan.auth.service;

import lombok.NonNull;
import top.tolan.auth.dto.LoginParentDTO;
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
    public <T extends LoginParentDTO> AjaxResult login(T loginParentDTO);

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
