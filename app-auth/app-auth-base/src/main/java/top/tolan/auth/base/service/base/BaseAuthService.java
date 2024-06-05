package top.tolan.auth.base.service.base;

import jakarta.annotation.Resource;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import top.tolan.auth.base.dto.LoginParentDTO;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.auth.base.service.IAuthService;
import top.tolan.auth.base.service.ITokenService;
import top.tolan.common.domain.AjaxResult;

/**
 * 基础验证服务
 *
 * @author tooooolan
 * @version 2024年6月3日
 */
@Component
public abstract class BaseAuthService implements IAuthService {

    @Resource
    protected ITokenService tokenService;

    /**
     * 登录
     */
    @Override
    public abstract <T extends LoginParentDTO> AjaxResult login(T loginParentDTO);

    /**
     * 登出
     */
    @Override
    public AjaxResult logout(@NonNull LoginUser loginUser) {
        // 删除用户缓存记录
        tokenService.delLoginUser(loginUser.getUuid());
        return AjaxResult.success("退出成功");
    }

}
