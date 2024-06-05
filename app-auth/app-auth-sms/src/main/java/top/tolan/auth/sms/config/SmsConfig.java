package top.tolan.auth.sms.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import top.tolan.common.exception.ServiceException;

/**
 * 手机信息发送配置
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Component
@ConfigurationProperties(prefix = "sms")
@PropertySource(value = {"classpath:application-sms.yml"}, encoding = "UTF-8")
public class SmsConfig {

    // 访问key
    private static String accessKeyId;
    // 访问钥匙
    private static String accessKeySecret;
    // 端点
    private static String endpoint;
    // 签名
    public static String signName;
    // 一天最大发送次数
    public static Integer dayMaxTimes;
    // 发送频率限制(分钟)
    public static Integer sendFrequency;
    // 验证码有效时间(分钟)
    public static Integer codeValidTime;

    // 阿里云短信服务
    public static Client client;

    /**
     * 使用AK&SK初始化账号Client
     */
    @PostConstruct
    public static void initClient() {
        Config config = new Config()
                .setAccessKeyId(SmsConfig.accessKeyId)
                .setAccessKeySecret(SmsConfig.accessKeySecret);
        config.endpoint = SmsConfig.endpoint;
        try {
            client = new Client(config);
        } catch (Exception e) {
            throw new ServiceException("初始化阿里云短信服务失败");
        }
    }

    @Value("${access-key-id}")
    public void setAccessKeyId(String accessKeyId) {
        SmsConfig.accessKeyId = accessKeyId;
    }

    @Value("${access-key-secret}")
    public void setAccessKeySecret(String accessKeySecret) {
        SmsConfig.accessKeySecret = accessKeySecret;
    }

    @Value("${endpoint}")
    public void setEndpoint(String endpoint) {
        SmsConfig.endpoint = endpoint;
    }

    @Value("${signName}")
    public void setSignName(String signName) {
        SmsConfig.signName = signName;
    }

    @Value("${codeValidTime}")
    public void setCodeValidTime(Integer codeValidTime) {
        SmsConfig.codeValidTime = codeValidTime;
    }

    @Value("${dayMaxTimes}")
    public void setDayMaxTimes(Integer dayMaxTimes) {
        SmsConfig.dayMaxTimes = dayMaxTimes;
    }

    @Value("${sendFrequency}")
    public void setSendFrequency(Integer sendFrequency) {
        SmsConfig.sendFrequency = sendFrequency;
    }

}
