package top.tolan.auth.service.impl;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import top.tolan.auth.entity.LoginDTO;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.service.ITokenService;
import top.tolan.common.entity.po.SysUser;
import top.tolan.common.exception.ServiceException;
import top.tolan.common.utils.RedisCache;
import top.tolan.system.service.ISysUserService;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 微信用户相关的服务
 */
@Slf4j
@Service
@Transactional
public class TokenServiceImpl implements ITokenService {

    // 小程序的AppID
    @Value("${Wechat.AppID}")
    private String APP_ID;
    // 小程序密钥
    @Value("${Wechat.AppSecret}")
    private String APP_SECRET;
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;
    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;
    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;
    protected static final long MILLIS_SECOND = 1000;
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Resource
    private ISysUserService userService;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisCache redisCache;

    /**
     * 微信用户授权小程序登录验证
     */
    @Override
    public LoginUser appLogin(String code, LoginUser loginUser) {
        // 构建请求地址
        URI uri = UriComponentsBuilder.fromUriString("https://api.weixin.qq.com/sns/jscode2session")
                .queryParam("appid", APP_ID).queryParam("secret", APP_SECRET).queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code").build().toUri();
        try {
            String response = restTemplate.getForObject(uri, String.class);
            LoginDTO loginDTO = JSON.parseObject(response, LoginDTO.class);
            assert ObjectUtils.isNotEmpty(loginDTO);
            if (loginDTO.getErrcode() == null) {
                String openid = loginDTO.getOpenid();
                SysUser sysUser = loginUser.getSysUser();
                sysUser.setOpenId(openid);
                sysUser = userService.addUserOnNotPresence(sysUser);
                loginUser.setSysUser(sysUser);
                return loginUser;
            } else {
                log.error("微信登录失败, 错误码: {}, 错误消息: {}", loginDTO.getErrcode(), loginDTO.getErrmsg());
                throw new ServiceException("微信登录失败: " + loginDTO.getErrmsg());
            }
        } catch (Exception e) {
            log.error("登录失败：{}", e.getMessage(), e);
            throw new ServiceException("登录异常，请稍后再试");
        }
    }

    /**
     * 创建令牌
     */
    @Override
    public String createToken(LoginUser loginUser) {
        String token = UUID.randomUUID().toString();
        loginUser.setUuid(token);
        this.refreshToken(loginUser);
        Map<String, Object> claims = new HashMap<>();
        claims.put("login_user_key", token);
        return this.createToken(claims);
    }

    /**
     * 刷新令牌有效期
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getUuid());
        // 保存用户信息到Redis
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private String getTokenKey(String uuid) {
        return "login_tokens:" + uuid;
    }

    @Override
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = this.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get("login_user_key");
                String userKey = getTokenKey(uuid);
                // login_tokens:42071833-752d-4030-ac7e-ab1309227627
                return redisCache.getCacheObject(userKey);
            } catch (Exception e) {
                log.error("获取用户信息异常'{}'", e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 获取请求token
     *
     * @param request 请求
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
        }
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     */
    @Override
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    @Override
    public void delLoginUser(String token) {
        if (StringUtils.isNotBlank(token)) {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }
}
