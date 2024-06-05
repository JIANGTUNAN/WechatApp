package top.tolan.auth.sms.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.tea.TeaException;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import top.tolan.auth.sms.config.SmsConfig;
import top.tolan.auth.sms.constant.SMSRedisKeys;
import top.tolan.auth.sms.constant.SMSTemplates;
import top.tolan.auth.sms.entity.SendResult;
import top.tolan.auth.sms.service.ISendMessages;
import top.tolan.common.exception.ServiceException;
import top.tolan.common.utils.IpUtils;
import top.tolan.common.utils.RedisCache;
import top.tolan.common.utils.StringUtils;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 发送信息服务
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Slf4j
@Service
public class SendMessagesServiceImpl implements ISendMessages {

    @Resource
    private RedisCache redisCache;

    /**
     * 比较验证码
     *
     * @param phone           手机号
     * @param userEnteredCode 用户输入的验证码
     * @param smsTemplates    短信模板
     * @return 是否相等
     */
    @Override
    public boolean compareVerificationCodes(String phone, String userEnteredCode, @NonNull SMSTemplates smsTemplates) {
        if (StringUtils.isNoneBlank(phone, userEnteredCode)) {
            String redisKey = SMSRedisKeys.VERIFICATION + smsTemplates.getType() + ":" + phone;
            String verificationCode = redisCache.getCacheObject(redisKey);
            if (StringUtils.equals(userEnteredCode, verificationCode)) {
                log.info("手机号为：{} 的用户验证成功，验证码类型为：{}", phone, smsTemplates.getDesc());
                // 验证成功后删除验证码
                return redisCache.deleteObject(redisKey);
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 发送短信验证码
     *
     * @param phoneNumber  手机号
     * @param smsTemplates 短信模板
     * @return 发送结果
     */
    public SendResult sendVerificationCode(String phoneNumber, SMSTemplates smsTemplates) {
        // 检查限制
        this.checkLimit(phoneNumber);
        // 生成并缓存验证码
        String code = this.cacheVerificationCode(phoneNumber, smsTemplates.getType());
        log.info("生成的验证码为：{}, 短信类型为：{}", code, smsTemplates.getDesc());
        // 发送验证码
        return this.sendVerificationCode(phoneNumber, code, smsTemplates.getCode());
    }

    /**
     * 生成并缓存验证码
     *
     * @param phone   手机号
     * @param smsType 短信类型
     * @return 验证码
     */
    private String cacheVerificationCode(@NonNull String phone, String smsType) {
        // 生成一个随机的六位数
        String code = String.valueOf(new Random().nextInt(999999 - 100000 + 1) + 100000);
        redisCache.setCacheObject(SMSRedisKeys.VERIFICATION + smsType + ":" + phone, code, SmsConfig.codeValidTime, TimeUnit.MINUTES);
        return code;
    }

    /**
     * 限制发送
     */
    private void limitSend(String phone) {
        // 获取请求的IP地址
        String ipAddr = IpUtils.getIpAddr();
        // 把IP地址和手机号存入Redis
        redisCache.setCacheObject(SMSRedisKeys.RESTRICTIONS + ipAddr, phone, SmsConfig.sendFrequency, TimeUnit.MINUTES);
        // key为手机号，value为发送次数
        String timesKey = SMSRedisKeys.SEND_TIMES + phone;
        // 获取发送次数
        Integer sendTimes = redisCache.getCacheObject(timesKey);
        if (ObjectUtils.isNotEmpty(sendTimes)) {
            // 获取过期时间
            long expire = redisCache.getExpire(timesKey);
            // 把发送次数加1
            redisCache.setCacheObject(timesKey, ++sendTimes);
            // 设置过期时间
            redisCache.expire(timesKey, expire);
        } else {
            // 如果没有发送过，就设置为1
            redisCache.setCacheObject(timesKey, 1, 1, TimeUnit.DAYS);
        }
    }

    /**
     * 检查限制
     */
    private void checkLimit(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber))
            throw new IllegalArgumentException("手机号或验证码不能为空");
        // 限制发送
        if (redisCache.hasKey(SMSRedisKeys.RESTRICTIONS + IpUtils.getIpAddr()))
            throw new ServiceException("发送过于频繁，请稍后再试");
        Integer sendTimes = redisCache.getCacheObject(SMSRedisKeys.SEND_TIMES + phoneNumber);
        if (ObjectUtils.isNotEmpty(sendTimes) && sendTimes >= SmsConfig.dayMaxTimes)
            throw new ServiceException("发送次数已达上限，请明天再试");
    }


    /**
     * 发送验证码
     *
     * @param phone         手机号
     * @param code          验证码
     * @param templatesCode 模板代码
     * @return 发送结果
     */
    private SendResult sendVerificationCode(String phone, String code, String templatesCode) {
        Client client = SmsConfig.client;
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(SmsConfig.signName)
                .setPhoneNumbers(phone)
                .setTemplateCode(templatesCode)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        SendResult sendResult = new SendResult();
        try {
            // TODO 如果需要实际发送短信的时候才放开注释
//             SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
//             SendSmsResponseBody body = sendSmsResponse.getBody();
            SendSmsResponseBody body = new SendSmsResponseBody();
            body.setCode("OK");
            body.setMessage("OK");
            if (body.code.equals("OK")) {
                // 限制发送
                this.limitSend(phone);
                log.info("短信发送成功，手机号为：{}，响应码为：{}，响应消息为：{}", phone, body.code, body.message);
                return new SendResult(true, body.message, body.code);
            }
        } catch (TeaException error) {
            log.error("短信发送失败{},诊断地址为{}", error.message,
                    Optional.ofNullable(error.getData()).map(data -> data.get("Recommend")).orElse("无"));
            sendResult = new SendResult(Boolean.FALSE, error.message, "500");
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.error("发生未知错：{},诊断地址为：{}", error.message,
                    Optional.ofNullable(error.getData()).map(data -> data.get("Recommend")).orElse("无"));
            sendResult = new SendResult(Boolean.FALSE, error.message, "500");
        }
        log.error("短信发送失败，手机号为：{}，响应码为：{}，响应消息为：{}", phone, sendResult.getCode(), sendResult.getMessage());
        return sendResult;
    }

}
