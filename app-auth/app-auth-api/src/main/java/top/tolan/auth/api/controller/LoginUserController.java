package top.tolan.auth.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tolan.auth.base.entity.LoginUser;
import top.tolan.auth.base.utils.SecurityUtils;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.entity.po.SysUser;

/**
 * 登录用户信息控制器
 *
 * @author tooooolan
 * @version 2024年5月27日
 */
@RestController("/loginUser")
public class LoginUserController {

    /**
     * 根据请求中携带的token获取当前登录用户所有信息
     */
    @GetMapping
    public AjaxResult getLoginUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser);
    }

    /**
     * 根据请求中携带的token获取当前登录用户主键
     */
    @GetMapping("/getId")
    public AjaxResult getLoginUserId() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser.getUserId());
    }

    /**
     * 根据请求中携带的token获取当前登录用户名
     */
    @GetMapping("/getUserName")
    public AjaxResult getLoginUserName() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser.getSysNickName());
    }

    /**
     * 根据请求中携带的token获取当前登录用户头像
     */
    @GetMapping("/getHeadPic")
    public AjaxResult getLoginUserHeadPic() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser.getSysHeadPic());
    }

    /**
     * 根据请求中携带的token获取当前登录用户性别
     */
    @GetMapping("/getSex")
    public AjaxResult getLoginUserSex() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        return AjaxResult.success(sysUser.getSex());
    }

}
