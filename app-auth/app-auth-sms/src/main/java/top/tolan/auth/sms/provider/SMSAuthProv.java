package top.tolan.auth.sms.provider;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import top.tolan.auth.base.constant.LoginMethods;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.auth.sms.constant.SMSRedisKeys;
import top.tolan.auth.sms.constant.SMSTemplates;
import top.tolan.auth.sms.token.SMSAuthorizationToken;
import top.tolan.common.entity.po.SysUser;
import top.tolan.common.exception.ServiceException;
import top.tolan.common.utils.RedisCache;
import top.tolan.common.utils.StringUtils;
import top.tolan.system.service.ISysUserService;

/**
 * 手机信息验证码登录验证提供
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SMSAuthProv implements AuthenticationProvider {

    @Resource
    private RedisCache redisCache;
    @Resource
    private ISysUserService userService;

    /**
     * 判断是否支持当前认证方式
     *
     * @param authenticationClass 认证器类
     * @return 是否支持
     */
    @Override
    public boolean supports(Class<?> authenticationClass) {
        return SMSAuthorizationToken.class.isAssignableFrom(authenticationClass);
    }

    /**
     * 调用authenticate方法进行认证
     *
     * @param authentication 认证器
     * @return 认证结果
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SMSAuthorizationToken authorizationToken = (SMSAuthorizationToken) authentication;
        LoginUser loginUser = this.compareVerificationCodes(authorizationToken.getPrincipal(), authorizationToken.getPhoneNumber(), authorizationToken.getSmsCode());
        authorizationToken.setAuthenticated(true);
        authorizationToken.setLoginUser(loginUser);
        return authorizationToken;
    }

    /**
     * 比较验证码
     *
     * @param phoneNumber     手机号
     * @param userEnteredCode 用户输入的验证码
     * @return 是否相等
     */
    public LoginUser compareVerificationCodes(LoginUser loginUser, String phoneNumber, String userEnteredCode) {
        if (StringUtils.isNoneBlank(phoneNumber, userEnteredCode)) {
            String redisKey = SMSRedisKeys.VERIFICATION + SMSTemplates.TEST.getType() + ":" + phoneNumber;
            String verificationCode = redisCache.getCacheObject(redisKey);
            if (StringUtils.equals(userEnteredCode, verificationCode)) {
                log.info("手机号为：{} 的用户验证成功，验证码类型为：{}", phoneNumber, SMSTemplates.TEST.getDesc());
                // 验证成功后删除验证码
                redisCache.deleteObject(redisKey);
                LambdaUpdateWrapper<SysUser> wrapper = Wrappers.lambdaUpdate(SysUser.class)
                        .eq(SysUser::getPhone, phoneNumber);
                SysUser sysUser = userService.getOneOpt(wrapper)
                        .orElseThrow(() -> new ServiceException("登录失败：用户不存在"));
                loginUser.setSysUser(sysUser);
                loginUser.setUserId(sysUser.getUserId());
                loginUser.setUserName(sysUser.getUserName());
                loginUser.setSysNickName(sysUser.getSysNickName());
                loginUser.setSysHeadPic(sysUser.getSysHeadPic());
                loginUser.setRoleId(sysUser.getRoleId());
                loginUser.setLoginMethod(LoginMethods.PHONE);
                loginUser.setLoginTime(System.currentTimeMillis());
                loginUser.setExpireTime(loginUser.getLoginTime() + 30 * 60 * 1000L);
                return loginUser;
            } else {
                throw new ServiceException("登录失败：验证码错误");
            }
        } else {
            throw new ServiceException("登录失败：参数错误");
        }
    }

}
