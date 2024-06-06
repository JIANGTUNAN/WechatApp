package top.tolan.common.config;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.springframework.stereotype.Component;
import top.tolan.common.utils.RedisCache;
import cn.hutool.json.JSONUtil;
import top.tolan.common.utils.SpringUtils;

import java.util.List;
import java.util.Map;

/**
 * 自定义Redis的Sink
 *
 * @author tooooolan
 * @version 2024年6月7日
 */
@Slf4j
@Component
public class RedisSink extends RichSinkFunction<String> {

    private final static RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
    private static final String PREFIX = "app:";

    @Override
    public void invoke(String json, Context context) {
        try {
            // 解析 JSON 字符串
            JSONObject jsonObject = JSONUtil.parseObj(json);

            // 获取操作类型
            String opStr = jsonObject.get("op").toString();
            OperationType op = OperationType.fromString(opStr);

            // 如果操作类型不是读操作，进行处理
            if (op != OperationType.READ && op != OperationType.CREATE) {
                Map<String, Object> data;

                // 根据操作类型获取相应的数据
                if (op == OperationType.DELETE) {
                    data = (Map<String, Object>) jsonObject.get("before");
                } else {
                    data = (Map<String, Object>) jsonObject.get("after");
                }

                // 检查数据是否为空
                if (data == null) {
                    log.error("操作 {} 的数据为空。", opStr);
                    return;
                }
                String tableName = (String) jsonObject.get("tableName");
                List<String> primaryKeys = MonitorMySQLCDC.tables.values().stream().toList();
                for (String primaryKey : primaryKeys) {
                    if (data.containsKey(primaryKey)) {
                        // 获取 ID 并删除 Redis 中的对应记录
                        String id = data.get(primaryKey).toString();
                        String redisKey = PREFIX + tableName + ":" + id;
                        redisCache.deleteObject(redisKey);
                        log.info("CDC监听到：{}操作，成功删除Redis缓存 => key: {}", opStr, redisKey);
                    }
                }
            }
        } catch (Exception e) {
            // 捕获并记录异常
            log.error("处理 JSON 时出错: {}", e.getMessage(), e);
        }
    }

    // 使用枚举替代硬编码的操作类型
    @Getter
    @AllArgsConstructor
    private enum OperationType {
        READ("READ"),
        DELETE("DELETE"),
        UPDATE("UPDATE"),
        CREATE("CREATE");

        private final String value;

        public static OperationType fromString(String value) {
            for (OperationType type : OperationType.values()) {
                if (type.getValue().equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid operation type: " + value);
        }
    }

}
