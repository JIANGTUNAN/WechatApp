package top.tolan.system.controller;

import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.entity.po.SysUser;
import top.tolan.system.entity.dto.EditUserDto;
import top.tolan.system.service.ISysUserService;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private ISysUserService userService;


    /**
     * 根据主键查询系统用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/findByUserId/{userId}")
    public AjaxResult findByUserId(@PathVariable("userId") Integer userId) {
        SysUser user = userService.getById(userId);
        return AjaxResult.success(user);
    }

    /**
     * 编辑用户信息
     *
     * @param editUserDto 用户信息编辑表单接收对象
     * @return 成功或失败
     */
    @PutMapping("/edit")
    private AjaxResult editUser(@RequestBody @Validated EditUserDto editUserDto) {
        int success = userService.updateUser(editUserDto);
        return success > 0 ? AjaxResult.success() : AjaxResult.error();
    }

}
