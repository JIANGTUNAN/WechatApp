package top.tolan.auth.service.impl;

import org.springframework.stereotype.Service;
import top.tolan.auth.dto.LoginParentDTO;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.ITokenService;
import top.tolan.common.domain.AjaxResult;

@Service
public class PhoneLoginServiceImpl extends BaseAuthServer implements IAuthServer {

    private final ITokenService tokenService;

    public PhoneLoginServiceImpl(ITokenService tokenService) {
        super(tokenService);
        this.tokenService = tokenService;
    }

    @Override
    public AjaxResult login(LoginParentDTO loginParentDTO) {
        return null;
    }


}
