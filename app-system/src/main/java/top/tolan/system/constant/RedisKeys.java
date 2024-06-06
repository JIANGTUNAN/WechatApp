package top.tolan.system.constant;

import java.util.concurrent.TimeUnit;

/**
 * 系统模块RedisKey
 *
 * @author tooooolan
 * @version 2024年6月7日
 */
public class RedisKeys {

    // 默认过期时间5分钟
    public static final Integer timeout = 5;
    // 默认过期时间单位（分钟）
    public static final TimeUnit timeUnit = TimeUnit.MINUTES;
    // 缓存基础key
    public static final String baseKey = "app:";
    // 系统用户key
    public static final String SysUserKey = baseKey + "sys_user:";

}
