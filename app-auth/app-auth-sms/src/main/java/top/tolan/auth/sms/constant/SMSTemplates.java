package top.tolan.auth.sms.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * 手机信息发送模板
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Getter
@AllArgsConstructor
public enum SMSTemplates {

    /**
     * 您的验证码为：${code}，该验证码5分钟内有效，请勿泄露于他人！
     * code-仅数字；
     */
    LOGIN("SMS_465915522", "login", "短信验证登录"),

    /**
     * 您正在申请手机注册，验证码为：${code}，5分钟内有效！
     * code-仅数字
     */
    REGISTER("SMS_465915522", "register", "短信注册"),

    /**
     * 您正在进行找回密码，验证码为：${code}，5分钟内有效，请勿泄露于他人！
     * code-仅数字
     */
    CHANGE("SMS_465920517", "change", "忘记密码"),

    /**
     * 验证码为：${code}，您正在绑定平台账号，若非本人操作，请忽略本短信。
     * code-仅数字
     */
    BINDING("SMS_465925508", "binding", "绑定手机号"),

    /**
     * 您的动态码为：${code}，5分钟内有效，请勿泄露！
     * code-数字+字母组合或仅字母
     */
    GENERIC("SMS_465925593", "generic", "通用验证"),

    /**
     * 您的动态码为：${code}，5分钟内有效，请勿泄露！
     * code-仅数字；
     */
    TEST("SMS_465895596", "test", "测试"),
    ;

    // 短信模板code
    private final String code;
    // 短信类型
    private final String type;
    // 类型描述
    private final String desc;


    // 通过类型获取模板
    public static Optional<SMSTemplates> getByType(String type) {
        for (SMSTemplates smsTemplates : SMSTemplates.values()) {
            if (smsTemplates.getType().equals(type)) {
                return Optional.of(smsTemplates);
            }
        }
        return Optional.empty();
    }

}
