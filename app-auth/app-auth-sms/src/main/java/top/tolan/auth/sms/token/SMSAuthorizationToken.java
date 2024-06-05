package top.tolan.auth.sms.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import top.tolan.auth.base.constant.LoginMethods;
import top.tolan.auth.base.entity.LoginUser;

import java.util.Collection;

/**
 * 信息验证身份令牌
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SMSAuthorizationToken extends AbstractAuthenticationToken {

    // 系统用户登录信息
    private LoginUser loginUser;
    // 电话号
    private String phoneNumber;
    // 短信验证码
    private String smsCode;

    public SMSAuthorizationToken(LoginUser loginUser, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.loginUser = loginUser;
        super.setAuthenticated(true);
    }

    public SMSAuthorizationToken(String phoneNumber, String smsCode) {
        super(null);
        this.loginUser = new LoginUser();
        this.loginUser.setLoginMethod(LoginMethods.PHONE);
        this.phoneNumber = phoneNumber;
        this.smsCode = smsCode;
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
