package top.tolan.auth.service.impl;

import org.springframework.stereotype.Service;
import top.tolan.auth.dto.LoginDTO;
import top.tolan.auth.service.IAuthServer;
import top.tolan.common.domain.AjaxResult;

@Service
public class WechatLoginServiceImpl implements IAuthServer {
    @Override
    public AjaxResult login(LoginDTO loginDTO) {
        return null;
    }


}
