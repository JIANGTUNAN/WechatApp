package top.tolan.auth.service.impl;

import lombok.NonNull;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.ITokenService;
import top.tolan.common.domain.AjaxResult;

public abstract class BaseAuthServer implements IAuthServer {
    protected final ITokenService tokenService;

    protected BaseAuthServer(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public AjaxResult logout(@NonNull LoginUser loginUser) {
        // 删除用户缓存记录
        tokenService.delLoginUser(loginUser.getUuid());
        return AjaxResult.success("退出成功");
    }
}
