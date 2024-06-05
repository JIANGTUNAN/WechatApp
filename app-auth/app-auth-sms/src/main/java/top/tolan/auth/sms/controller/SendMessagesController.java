package top.tolan.auth.sms.controller;

import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tolan.auth.sms.constant.SMSTemplates;
import top.tolan.auth.sms.dto.SendLoginCodeDTO;
import top.tolan.auth.sms.entity.SendResult;
import top.tolan.auth.sms.service.ISendMessages;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.exception.ServiceException;

/**
 * 发送信息Controller
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@RestController
@RequestMapping("/auth/sms")
public class SendMessagesController {

    @Resource
    private ISendMessages sendMessages;

    @PostMapping("/loginCode")
    public AjaxResult sendLoginCode(@RequestBody @Validated SendLoginCodeDTO sendLoginCodeDTO) {
        SMSTemplates smsTemplates = SMSTemplates.getByType(sendLoginCodeDTO.getSmsType())
                .orElseThrow(() -> new ServiceException("短信类型错误"));
        SendResult sendResult = sendMessages.sendVerificationCode(sendLoginCodeDTO.getPhone(), smsTemplates);
        return sendResult.getSuccess() ?
                AjaxResult.success(sendResult.getMessage()) : AjaxResult.error(sendResult.getMessage());
    }

}
