package top.tolan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.tolan.common.entity.po.SysUser;
import top.tolan.system.entity.dto.EditUserDto;
import top.tolan.system.mapper.SysUserMapper;
import top.tolan.system.service.ISysUserService;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Resource
    private SysUserMapper userMapper;

    /**
     * 编辑用户信息
     */
    @Override
    public int updateUser(EditUserDto editUserDto) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(editUserDto, sysUser);
        return userMapper.updateById(sysUser);
    }

    /**
     * 当用户不存在时就新增
     */
    @Override
    public SysUser addUserOnNotPresence(SysUser sysUser) {
        // 根据openID查询系统用户
        SysUser user = this.findUserByOpenId(sysUser.getOpenId());
        // 如果存在就返回
        if (ObjectUtils.isNotEmpty(user))
            return user;
        // null的属性不会保存，会使用数据库默认值
        userMapper.insert(sysUser);
        return userMapper.selectById(sysUser.getUserId());
    }

    /**
     * 根据openID查询系统用户
     */
    @Override
    public SysUser findUserByOpenId(String openId) {
        // 创建查询条件
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class)
                .eq(SysUser::getOpenId, openId);
        // 执行查询
        return userMapper.selectOne(wrapper);
    }
}
