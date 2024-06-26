package top.tolan.auth.base.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.auth.base.service.ITokenService;
import top.tolan.common.utils.RedisCache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 身份令牌服务实现
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Slf4j
@Service
@Transactional
public class TokenServiceImpl implements ITokenService {


    @Resource
    private RedisCache redisCache;
    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;
    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;


    protected static final long MILLIS_SECOND = 1000;
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;


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
        // 重新设置过期时间
        loginUser.setExpireTime(System.currentTimeMillis() + expireTime * MILLIS_MINUTE);
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
