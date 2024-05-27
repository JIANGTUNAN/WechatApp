package top.tolan.sms.service;

import lombok.NonNull;
import top.tolan.sms.constant.SMSTemplates;
import top.tolan.sms.entity.SendResult;

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
