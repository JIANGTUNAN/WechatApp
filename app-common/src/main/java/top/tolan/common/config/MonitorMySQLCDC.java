package top.tolan.common.config;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 创建并启动FinkCDC应用监控MySQL数据库
 *
 * @author tooooolan
 * @version 2024年6月7日
 */
@Component
public class MonitorMySQLCDC implements ApplicationRunner {

    @Resource
    private RedisSink redisSink;
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 监控的表和主键名
     */
    public static final Map<String, String> tables = Map.ofEntries(
            Map.entry("wechat_app.sys_user", "user_id")
    );

    /**
     * 异步启用CDC监控
     */
    @Override
    @Async("monitorMySQLCDCExecutor")
    public void run(ApplicationArguments args) {
        try {
            Map<String, String> jdbcInfo = parseJdbcUrl(jdbcUrl);
            Properties jdbcProperties = new Properties();
            // 添加其他需要的属性
            for (Map.Entry<String, String> entry : jdbcInfo.entrySet()) {
                if (!entry.getKey().equals("hostname") && !entry.getKey().equals("port") && !entry.getKey().equals("database")) {
                    jdbcProperties.setProperty(entry.getKey(), entry.getValue());
                }
            }
            MySqlSource<String> source = MySqlSource.<String>builder()
                    .hostname(jdbcInfo.get("hostname"))
                    .port(NumberUtils.createInteger(jdbcInfo.get("port")))
                    // 可配置多个数据库
                    .databaseList(jdbcInfo.get("database"))
                    // 可配置多个表
                    .tableList(tables.keySet().toArray(String[]::new))
                    .username(username)
                    .password(password)
                    .jdbcProperties(jdbcProperties)
                    // 包括schema的改变
                    .includeSchemaChanges(true)
                    // 使用自定义反序列化
                    .deserializer(new CustomerDeserializationSchema())
                    // 启动模式；关于启动模式
                    .startupOptions(StartupOptions.initial())
                    .serverTimeZone("GMT+8")
                    .build();
            // 环境配置
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            // 设置 checkpoint 间隔
            env.enableCheckpointing(5000);
            // 设置 Checkpoint 超时时间为 1 分钟
            env.getCheckpointConfig().setCheckpointTimeout(60000);
            // 设置最大并发 Checkpoint 数量为 1
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
            // 设置两次 Checkpoint 之间的最小间隔时间为 500 毫秒
            env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
            // 启用非对齐 Checkpoint
            env.getCheckpointConfig().enableUnalignedCheckpoints();

            // 设置 source 节点的并行数
            env.setParallelism(1);
            env.fromSource(source, WatermarkStrategy.noWatermarks(), "MySQL")
                    // 添加Sink，也就数据流向何处
                    .addSink(this.redisSink);
            env.executeAsync("mysql-cdc-redis");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 解析配置文件中的JDBC URL
     *
     * @param jdbcUrl JDBC URL
     * @return 结果
     * @throws URISyntaxException 解析异常
     */
    public static Map<String, String> parseJdbcUrl(String jdbcUrl) throws URISyntaxException {
        String url = jdbcUrl.substring(5);
        URI uri = new URI(url);
        Map<String, String> result = new HashMap<>();
        result.put("hostname", uri.getHost());
        result.put("port", String.valueOf(uri.getPort()));
        result.put("database", uri.getPath().substring(1));
        // 解析查询参数
        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result;
    }

}
