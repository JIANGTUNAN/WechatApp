package top.tolan.sms.config;

import com.aliyun.facebody20191230.Client;
import com.aliyun.teaopenapi.models.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import top.tolan.common.exception.ServiceException;

@Component
@ConfigurationProperties(prefix = "face")
@PropertySource(value = {"classpath:application-sms.yml"}, encoding = "UTF-8")
public class FaceWithConfig {

    // 访问key
    private static String accessKeyId;
    // 访问钥匙
    private static String accessKeySecret;
    // 端点
    private static String endpoint;

    // 阿里云短信服务
    public static Client client;

    /**
     * 使用AK&SK初始化账号Client
     */
    @PostConstruct
    public static void initClient() {
        Config config = new Config()
                .setAccessKeyId(FaceWithConfig.accessKeyId)
                .setAccessKeySecret(FaceWithConfig.accessKeySecret);
        config.endpoint = FaceWithConfig.endpoint;
        try {
            client = new Client(config);
        } catch (Exception e) {
            throw new ServiceException("初始化阿里云短信服务失败");
        }
    }

    @Value("${access-key-id}")
    public void setAccessKeyId(String accessKeyId) {
        FaceWithConfig.accessKeyId = accessKeyId;
    }

    @Value("${access-key-secret}")
    public void setAccessKeySecret(String accessKeySecret) {
        FaceWithConfig.accessKeySecret = accessKeySecret;
    }

    @Value("${endpoint}")
    public void setEndpoint(String endpoint) {
        FaceWithConfig.endpoint = endpoint;
    }




}
