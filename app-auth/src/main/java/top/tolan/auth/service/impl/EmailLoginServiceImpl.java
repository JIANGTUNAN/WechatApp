package top.tolan.auth.service.impl;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import top.tolan.auth.constant.LoginMethods;
import top.tolan.auth.dto.LoginParentDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.base.BaseAuthServer;
import top.tolan.common.domain.AjaxResult;

@Service(LoginMethods.EMAIL)
public class EmailLoginServiceImpl extends BaseAuthServer {

    @Override
    public AjaxResult login(LoginParentDTO loginParentDTO) {
        return null;
    }

}
