package top.tolan.sms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendResult {

    // 发送结果
    private Boolean success;
    // 消息
    private String message;
    // 状态码
    private String code;

}
