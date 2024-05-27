package top.tolan.sms.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.tolan.common.constant.HttpStatus;
import top.tolan.common.domain.AjaxResult;
import top.tolan.sms.constant.SMSTemplates;
import top.tolan.sms.entity.SendResult;
import top.tolan.sms.service.ISendMessages;
import top.tolan.sms.service.Impl.FaceWithImpl;

import java.util.UUID;

@RestController
public class TestController {

    @Resource
    private ISendMessages sendMessages;

    @Resource
    private FaceWithImpl faceWith;

    @GetMapping("/test")
    public AjaxResult test() {
        return AjaxResult.success("请求成功", faceWith.test());
    }



    @GetMapping("/testSend")
    public AjaxResult testSend() {
        SendResult sendResult = sendMessages.sendVerificationCode("13178547948", SMSTemplates.TEST);
        return sendResult.getSuccess() ?
                AjaxResult.success(sendResult.getMessage()) : AjaxResult.error(sendResult.getMessage());
    }

    @GetMapping("/testCompare/{code}")
    public AjaxResult testCompare(@PathVariable("code") String code) {
        boolean success = sendMessages.compareVerificationCodes("13178547948", code, SMSTemplates.TEST);
        return success ?
                AjaxResult.success("验证成功") : AjaxResult.error(HttpStatus.WARN, "验证失败");
    }

}
