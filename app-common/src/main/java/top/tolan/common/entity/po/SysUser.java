package top.tolan.common.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class SysUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户微信openId
     */
    @TableField(value = "open_id")
    private String openId;

    /**
     * 用户系统头像
     */
    @TableField(value = "sys_head_pic")
    private String sysHeadPic;

    /**
     * 用户微信头像
     */
    @TableField(value = "wx_head_pic")
    private String wxHeadPic;

    /**
     * 用户手机号码
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 用户角色  1：普通用户；2：管理员
     */
    @TableField(value = "role_id")
    private String roleId;

    /**
     * 用户性别  1：男；2：女；3：未知
     */
    @TableField(value = "sex")
    private String sex;

    /**
     * 用户系统昵称
     */
    @TableField(value = "sys_nick_name")
    private String sysNickName;

    /**
     * 用户微信昵称
     */
    @TableField(value = "wx_nick_name")
    private String wxNickName;

    /**
     * 个人简介
     */
    @TableField(value = "introduction")
    private String introduction;

    /**
     * 生日
     */
    @TableField(value = "birthday")
    private String birthday;

    /**
     * 学校
     */
    @TableField(value = "school")
    private String school;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    public SysUser(String sysHeadPic, String sysNickName) {
        this.sysHeadPic = sysHeadPic;
        this.sysNickName = sysNickName;
    }
}
