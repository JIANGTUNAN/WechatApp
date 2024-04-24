package top.tolan.system.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.tolan.common.entity.po.SysUser;
import top.tolan.system.entity.dto.EditUserDto;
import top.tolan.system.mapper.SysUserMapper;
import top.tolan.system.service.ISysUserService;

@Service
public class SysUserServiceImpl implements ISysUserService {

    @Resource
    private SysUserMapper userMapper;

    /**
     * 根据用户ID查询系统用户
     *
     * @param userId 用户ID
     * @return 系统用户
     */
    @Override
    public SysUser findUserByUserId(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    /**
     * 编辑用户信息
     */
    @Override
    public int updateUser(EditUserDto editUserDto) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(editUserDto, sysUser);
        return userMapper.updateByPrimaryKeySelective(sysUser);
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
        userMapper.insertSelective(sysUser);
        return userMapper.selectByPrimaryKey(sysUser.getUserId());
    }

    /**
     * 根据openID查询系统用户
     */
    @Override
    public SysUser findUserByOpenId(String openId) {
        // 创建Example对象
        Example example = new Example(SysUser.class);
        // 创建查询条件
        Example.Criteria criteria = example.createCriteria();
        // 添加条件
        criteria.andEqualTo("openId", openId);
        // 执行查询
        return userMapper.selectOneByExample(example);
    }
}
