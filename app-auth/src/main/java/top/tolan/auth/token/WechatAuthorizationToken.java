package top.tolan.auth.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import top.tolan.auth.entity.LoginUser;
import top.tolan.common.entity.po.SysUser;

import java.util.Collection;

/**
 * 微信授权登录验证令牌
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WechatAuthorizationToken extends AbstractAuthenticationToken {

    // 系统用户登录信息
    private LoginUser loginUser;
    // 微信登录code
    private String loginCode;

    public WechatAuthorizationToken(LoginUser loginUser, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.loginUser = loginUser;
        super.setAuthenticated(true);
    }

    public WechatAuthorizationToken(String loginCode, String wxHeadPic, String wxNickName) {
        super(null);
        SysUser sysUser = new SysUser(wxHeadPic, wxNickName);
        this.loginUser = new LoginUser();
        this.loginUser.setSysUser(sysUser);
        this.loginCode = loginCode;
    }

    /**
     * 获取凭证
     */
    @Override
    public Object getCredentials() {
        return this.loginUser.getUserId();
    }

    /**
     * 获取主体信息
     */
    @Override
    public LoginUser getPrincipal() {
        return this.loginUser;
    }

}
