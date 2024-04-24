package top.tolan.common.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_user")
public class SysUser {

    /**
     * 主键
     */
    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "JDBC")
    private Integer userId;

    /**
     * 用户微信openId
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 用户系统头像
     */
    @Column(name = "sys_head_pic")
    private String sysHeadPic;

    /**
     * 用户微信头像
     */
    @Column(name = "wx_head_pic")
    private String wxHeadPic;

    /**
     * 用户手机号码
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 用户角色 1：普通用户；2：管理员
     */
    @Column(name = "role_id")
    private String roleId;

    /**
     * 用户性别 1：男；2：女；3：未知
     */
    @Column(name = "sex")
    private String sex;

    /**
     * 用户系统昵称
     */
    @Column(name = "sys_nick_name")
    private String sysNickName;

    /**
     * 用户微信昵称
     */
    @Column(name = "wx_nick_name")
    private String wxNickName;

    public SysUser(String wxHeadPic, String wxNickName) {
        this.wxHeadPic = wxHeadPic;
        this.wxNickName = wxNickName;
    }

}
