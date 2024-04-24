package top.tolan.auth.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;
import top.tolan.auth.filter.JwtAuthenticationTokenFilter;
import top.tolan.auth.filter.WechatLoginFilter;
import top.tolan.auth.handler.AuthenticationEntryPointHandler;
import top.tolan.auth.handler.LogoutSuccessHandlerImpl;


@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@EnableAsync
public class SecurityConfig {

    // 微信登录过滤器
    @Resource
    private WechatLoginFilter wechatLoginFilter;

    // 认证失败处理类
    @Resource
    private AuthenticationEntryPointHandler unauthorizedHandler;

    // 自定义退出处理类
    @Resource
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    // token认证过滤器
    @Resource
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    // 跨域过滤器
    @Resource
    private CorsFilter corsFilter;

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF禁用，因为不使用session
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用HTTP响应标头
            .headers(headers -> headers.xssProtection(HeadersConfigurer.XXssConfig::disable)
                .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            // 认证失败处理类
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(unauthorizedHandler))
            // 基于token，所以不需要session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .logout(logout -> logout.logoutUrl("/app/logout").logoutSuccessHandler(logoutSuccessHandler))
            .authorizeHttpRequests(auth ->
                    auth
                        // .requestMatchers( "/**").permitAll()
                        .requestMatchers("/login", "/register", "/captchaImage", "/app/login", "/wechat/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/*.html", "/**.html", "/**.css", "/**.js", "/profile/**").permitAll()
                        .anyRequest().authenticated()
            );

        // 添加自定义JWT过滤器
        http.addFilterBefore(wechatLoginFilter, UsernamePasswordAuthenticationFilter.class);
        // token过滤器 验证token有效性
        http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加CORS过滤器
        http.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class);
        http.addFilterBefore(corsFilter, LogoutFilter.class);

        return http.build();
    }

}
