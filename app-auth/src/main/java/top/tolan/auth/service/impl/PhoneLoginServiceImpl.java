package top.tolan.auth.service.impl;

import org.springframework.stereotype.Service;
import top.tolan.auth.dto.LoginParentDTO;
import top.tolan.auth.service.IAuthServer;
import top.tolan.common.domain.AjaxResult;

@Service
public class PhoneLoginServiceImpl implements IAuthServer {
    @Override
    public AjaxResult login(LoginParentDTO loginParentDTO) {
        return null;
    }


}
