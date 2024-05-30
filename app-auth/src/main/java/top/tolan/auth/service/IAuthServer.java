package top.tolan.auth.service;

import lombok.NonNull;
import top.tolan.auth.dto.LoginParentDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.utils.SpringUtils;

/**
 * 用户登录授权接口
 *
 * @author tooooolan
 * @version 2024年5月30日
 */
public interface IAuthServer {

    /**
     * 登录
     */
    public <T extends LoginParentDTO> AjaxResult login(T loginParentDTO);

    /**
     * 登出
     */
    public AjaxResult logout(@NonNull LoginUser loginUser);

}
