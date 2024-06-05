package top.tolan.auth.base.service.impl;

import org.springframework.stereotype.Service;
import top.tolan.auth.base.constant.LoginMethods;
import top.tolan.auth.base.dto.LoginParentDTO;
import top.tolan.auth.base.service.base.BaseAuthService;
import top.tolan.common.domain.AjaxResult;

@Service(LoginMethods.EMAIL)
public class EmailLoginServiceImpl extends BaseAuthService {

    @Override
    public AjaxResult login(LoginParentDTO loginParentDTO) {
        return null;
    }

}
