package top.tolan.auth.sms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送信息影响接受
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
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
