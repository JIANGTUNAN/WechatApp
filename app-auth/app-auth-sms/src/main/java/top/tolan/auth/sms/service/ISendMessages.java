package top.tolan.auth.sms.service;

import top.tolan.auth.sms.constant.SMSTemplates;
import top.tolan.auth.sms.entity.SendResult;

/**
 * 发送信息接口
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
public interface ISendMessages {

    /**
     * 发送短信验证码
     *
     * @param phone        手机号
     * @param smsTemplates 短信模板
     * @return 发送结果
     */
    public SendResult sendVerificationCode(String phone, SMSTemplates smsTemplates);

    /**
     * 比较验证码
     *
     * @param phone           手机号
     * @param userEnteredCode 用户输入的验证码
     * @param smsTemplates    短信模板
     * @return 是否相等
     */
    public boolean compareVerificationCodes(String phone, String userEnteredCode, SMSTemplates smsTemplates);

}
