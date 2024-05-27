package top.tolan.sms.constant;

public class SMSRedisKeys {

    // 验证码缓存
    // sms:code:类型:手机号
    public static String VERIFICATION = "sms:code:";

    // 限制短信发送次数
    // sms:limit:IP地址
    public static String RESTRICTIONS = "sms:limit:";

    // 最大次数限制
    // sms:times:手机号
    public static String SEND_TIMES = "sms:times:";

}
