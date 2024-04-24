package top.tolan.system.service;

import top.tolan.common.entity.po.SysUser;
import top.tolan.system.entity.dto.EditUserDto;

public interface ISysUserService {

    /**
     * 当用户不存在时就新增
     */
    public SysUser addUserOnNotPresence(SysUser sysUser);

    /**
     * 根据openID查询系统用户
     */
    public SysUser findUserByOpenId(String openId);

    /**
     * 根据用户ID查询系统用户
     */
    public SysUser findUserByUserId(Integer userId);

    /**
     * 编辑用户信息
     */
    public int updateUser(EditUserDto editUserDto);
}
