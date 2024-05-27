package top.tolan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.tolan.common.entity.po.SysUser;
import top.tolan.system.entity.dto.EditUserDto;

public interface ISysUserService extends IService<SysUser> {

    /**
     * 当用户不存在时就新增
     */
    public SysUser addUserOnNotPresence(SysUser sysUser);

    /**
     * 根据openID查询系统用户
     */
    public SysUser findUserByOpenId(String openId);

    /**
     * 编辑用户信息
     */
    public int updateUser(EditUserDto editUserDto);
}
