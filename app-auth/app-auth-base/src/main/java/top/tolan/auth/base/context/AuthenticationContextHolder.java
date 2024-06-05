package top.tolan.auth.base.context;

import org.springframework.security.core.Authentication;

/**
 * 验证上下文信息
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
public class AuthenticationContextHolder {

    private static final ThreadLocal<Authentication> contextHolder = new ThreadLocal<>();

    public static Authentication getContext() {
        return contextHolder.get();
    }

    public static void setContext(Authentication context) {
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }

}
