package top.tolan.auth.service.impl;

import top.tolan.auth.dto.LoginParentDTO;
import top.tolan.auth.service.IAuthServer;
import top.tolan.auth.service.ITokenService;
import top.tolan.common.domain.AjaxResult;

public class EmailLoginServiceImpl extends BaseAuthServer implements IAuthServer {

    private final ITokenService tokenService;

    public EmailLoginServiceImpl(ITokenService tokenService) {
        super(tokenService);
        this.tokenService = tokenService;
    }

    @Override
    public AjaxResult login(LoginParentDTO loginParentDTO) {
        return null;
    }


}
