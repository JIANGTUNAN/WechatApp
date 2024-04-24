package top.tolan.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tolan.auth.entity.LoginUser;
import top.tolan.auth.utils.SecurityUtils;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.entity.po.SysUser;

@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * 根据请求中携带的token获取当前登录用户所有信息
     */
    @GetMapping("/getLoginUser")
    public AjaxResult getLoginUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser);
    }

    /**
     * 根据请求中携带的token获取当前登录用户主键
     */
    @GetMapping("/getLoginUserId")
    public AjaxResult getLoginUserId() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser.getUserId());
    }

    /**
     * 根据请求中携带的token获取当前登录用户名
     */
    @GetMapping("/getLoginUserName")
    public AjaxResult getLoginUserName() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser.getSysNickName());
    }

    /**
     * 根据请求中携带的token获取当前登录用户头像
     */
    @GetMapping("/getLoginUserHeadPic")
    public AjaxResult getLoginUserHeadPic() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser.getSysHeadPic());
    }

    /**
     * 根据请求中携带的token获取当前登录用户性别
     */
    @GetMapping("/getLoginUserSex")
    public AjaxResult getLoginUserSex() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser.getSex());
    }

}
