package top.tolan.auth.base.service;

import lombok.NonNull;
import top.tolan.auth.base.dto.LoginParentDTO;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.common.domain.AjaxResult;

/**
 * 用户登录授权接口
 *
 * @author tooooolan
 * @version 2024年5月30日
 */
public interface IAuthService {

    /**
     * 登录
     */
    public <T extends LoginParentDTO> AjaxResult login(T loginParentDTO);

    /**
     * 登出
     */
    public AjaxResult logout(@NonNull LoginUser loginUser);

}
