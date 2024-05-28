package top.tolan.auth.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import top.tolan.common.entity.po.SysUser;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    // 系统用户信息
    private SysUser sysUser;
    // 在缓存中存储用户身份令牌的key
    private String uuid;
    // 用户主键
    private Integer userId;
    // 用户账户
    private String userName;
    // 用户系统头像
    private String sysHeadPic;
    // 用户系统昵称
    private String sysNickName;
    // 用户角色  1：普通用户；2：管理员
    private String roleId;
    // 登录时间
    private Long loginTime;
    // 登录方式
    private String loginMethod;
    // 过期时间
    private Long expireTime;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JSONField(serialize = false)
    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    @JSONField(serialize = false)
    @Override
    public String getUsername() {
        return sysUser.getUserName();
    }

    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isEnabled() {
        return true;
    }

}
