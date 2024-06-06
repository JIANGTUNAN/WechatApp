package top.tolan.system.controller;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.tolan.common.domain.AjaxResult;
import top.tolan.common.entity.po.SysUser;
import top.tolan.common.utils.RedisCache;
import top.tolan.system.constant.RedisKeys;
import top.tolan.system.entity.dto.EditUserDto;
import top.tolan.system.service.ISysUserService;

/**
 * 系统用户Controller
 *
 * @author tooooolan
 * @version 2024年6月7日
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private ISysUserService userService;
    @Resource
    private RedisCache redisCache;

    /**
     * 根据主键查询系统用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/findByUserId/{userId}")
    public AjaxResult findByUserId(@PathVariable("userId") Integer userId) {
        SysUser user = redisCache.getCacheObject(RedisKeys.SysUserKey + userId);
        if (ObjectUtils.isEmpty(user)) {
            user = userService.getById(userId);
            redisCache.setCacheObject(RedisKeys.SysUserKey + userId, user, RedisKeys.timeout, RedisKeys.timeUnit);
        }
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
