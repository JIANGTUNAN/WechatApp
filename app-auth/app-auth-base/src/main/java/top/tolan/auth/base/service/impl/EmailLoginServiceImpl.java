package top.tolan.auth.base.service.impl;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import top.tolan.auth.base.constant.LoginMethods;
import top.tolan.auth.base.dto.LoginParentDTO;
import top.tolan.auth.base.service.base.BaseAuthServer;
import top.tolan.common.domain.AjaxResult;

@Service(LoginMethods.EMAIL)
public class EmailLoginServiceImpl extends BaseAuthServer {

    @Override
    public AjaxResult login(LoginParentDTO loginParentDTO) {
        return null;
    }

}
