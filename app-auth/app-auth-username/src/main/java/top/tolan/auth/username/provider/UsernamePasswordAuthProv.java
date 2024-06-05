package top.tolan.auth.username.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.tolan.auth.base.constant.LoginMethods;
import top.tolan.auth.base.context.AuthenticationContextHolder;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.auth.base.utils.SecurityUtils;
import top.tolan.common.entity.po.SysUser;
import top.tolan.common.exception.ServiceException;
import top.tolan.system.service.ISysUserService;

/**
 * 用户名密码登录验证提供者
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Slf4j
@Service
public class UsernamePasswordAuthProv implements UserDetailsService {

    @Resource
    private ISysUserService userService;

    /**
     * 用户验证
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class)
                .eq(SysUser::getUserName, username);
        // 获取用户信息
        SysUser sysUser = userService.getOneOpt(wrapper)
                .orElseThrow(() -> new ServiceException("用户不存在"));
        // 获取用户密码
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();
        // 验证密码
        if (matchesPassword(password, sysUser.getPassword())) {
            // 返回用户信息
            return createLoginUser(sysUser);
        }
        throw new ServiceException("密码错误");
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    private static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return SecurityUtils.matchesPassword(rawPassword, encodedPassword);
    }

    /**
     * 构建安全框架用户
     *
     * @param user 系统用户
     * @return 用户信息
     */
    private static UserDetails createLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + 30 * 60 * 1000L);
        loginUser.setSysUser(user);
        loginUser.setLoginMethod(LoginMethods.USERNAME);
        return loginUser;
    }

}
