package top.tolan.auth.api.controller;

import com.aliyun.facebody20191230.models.CompareFaceResponseBody;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.tolan.auth.base.dto.FaceVerificationDTO;
import top.tolan.auth.base.dto.LoginParentDTO;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.auth.base.service.AuthServerFactory;
import top.tolan.auth.base.service.IAuthServer;
import top.tolan.auth.base.service.ITokenService;
import top.tolan.auth.face.service.FaceVerificationService;
import top.tolan.common.domain.AjaxResult;

/**
 * 验证控制器
 *
 * @author tooooolan
 * @version 2024年5月30日
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private ITokenService tokenService;
    @Resource
    private FaceVerificationService faceVerificationService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody @Validated LoginParentDTO loginParentDTO) {
        IAuthServer authServer = AuthServerFactory.getAuthServer(loginParentDTO.getLoginMethod());
        return authServer.login(loginParentDTO);
    }

    /**
     * 登出
     */
    @GetMapping("/logout")
    public AjaxResult outLogin(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        IAuthServer authServer = AuthServerFactory.getAuthServer(loginUser.getLoginMethod());
        return authServer.logout(loginUser);
    }

    /**
     * 验证人脸
     */
    @PostMapping("/face")
    public AjaxResult faceVerification(FaceVerificationDTO faceVerificationDTO) {
        CompareFaceResponseBody.CompareFaceResponseBodyData verification =
                faceVerificationService.verification(faceVerificationDTO.getFile());
        return AjaxResult.success(verification);
    }

}
