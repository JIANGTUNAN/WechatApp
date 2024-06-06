package top.tolan.common.config;

import cn.hutool.json.JSONObject;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Field;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Schema;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Struct;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.util.*;

/**
 * 自定义反序列化器
 *
 * @author tooooolan
 * @version 2024年6月7日
 */
@Slf4j
public class CustomerDeserializationSchema implements DebeziumDeserializationSchema<String> {

    /**
     * 将 SourceRecord 转换为 JSON 格式
     *
     * @param sourceRecord sourceRecord
     * @param collector    collector
     */
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) {
        JSONObject result = new JSONObject();
        try {
            // 获取 SourceRecord 的 topic
            // 获取结果：mysql_binlog_source.wechat_app.sys_user
            String topic = sourceRecord.topic();
            // 将 topic 按 "." 分割，获取数据库和表名
            String[] fields = topic.split("\\.");
            // 验证数组长度以避免ArrayIndexOutOfBoundsException
            if (fields.length < 3)
                throw new IllegalArgumentException("Topic format is incorrect.");

            result.set("db", fields[1]);
            result.set("tableName", fields[2]);
            // 获取 SourceRecord 的值并转换为 Struct 类型
            Object value = sourceRecord.value();
            if (value instanceof Struct structValue) {
                // 获取表的主键名
                String primaryKeys = getPrimaryKeys(structValue.schema());
                result.set("primaryKeys", primaryKeys);
                // 处理 "before" 部分的数据
                Struct before = structValue.getStruct("before");
                JSONObject beforeJson = convertStructToJson(before);
                result.set("before", beforeJson);

                // 处理 "after" 部分的数据
                Struct after = structValue.getStruct("after");
                JSONObject afterJson = convertStructToJson(after);
                result.set("after", afterJson);

                //获取操作类型 READ DELETE UPDATE CREATE
                Envelope.Operation operation = Envelope.operationFor(sourceRecord);
                result.set("op", operation.toString());

                /*
                {
                    "db":"wechat_app",
                    "tableName":"sys_user",
                    "before":{"id":"1001","name":""...},
                    "after":{"id":"1001","name":""...},
                    "op":"UPDATE"
                 }
                 */
                collector.collect(result.toString());
            } else {
                throw new ClassCastException("Value is not an instance of Struct.");
            }
        } catch (Exception e) {
            // 在实际应用中，应更具体地处理异常或记录日志
            log.error(e.getMessage(), e);
        }
    }

    private String getPrimaryKeys(Schema schema) {
        // 在这里实现获取主键名的逻辑
        // 假设主键信息存储在 Schema 的字段属性中
        // 例如，可以通过字段的某个属性来识别主键
        for (Field field : schema.fields()) {
            if (isPrimaryKey(field)) {
                return field.name();
            }
        }
        return null;
    }

    private boolean isPrimaryKey(Field field) {
        // 在这里实现判断字段是否为主键的逻辑
        // 例如，可以通过字段的某个属性来识别主键
        // 具体实现取决于你的 Schema 和数据格式
        // 这里假设主键字段有一个名为 "primaryKey" 的属性，其值为 true
        Schema schema = field.schema();
        Map<String, String> parameters = Optional.ofNullable(schema.parameters())
                .orElse(Collections.emptyMap());
        String booleanStr = parameters.get("primaryKey");
        return BooleanUtils.toBoolean(booleanStr);
    }

    /**
     * 将 Struct 转换为 JSON 对象
     *
     * @param struct Struct 对象
     * @return JSON
     */
    private JSONObject convertStructToJson(Struct struct) {
        if (struct == null) return null;
        JSONObject json = new JSONObject();
        Schema schema = struct.schema();
        for (Field field : schema.fields()) {
            Object value = struct.get(field);
            // 这里可以添加对value的类型检查，确保数据类型的正确性和兼容性
            json.set(field.name(), value);
        }
        return json;
    }


    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }

}

