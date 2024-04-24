package top.tolan.common.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 咖啡店评论信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coffee_comment")
public class CoffeeComment {

    /**
     * 主键
     */
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(generator = "JDBC")
    private Integer commentId;

    /**
     * 内容
     */
    @Column(name = "comment_content")
    private String commentContent;

    /**
     * 点赞数
     */
    @Column(name = "like_num")
    private Integer likeNum;

    /**
     * 回复数量
     */
    @Column(name = "reply_num")
    private Integer replyNum;

    /**
     * 父级评论
     */
    @Column(name = "ref_id")
    private Integer refId;

    /**
     * 店铺id
     */
    @Column(name = "store_id")
    private Integer storeId;

    /**
     * 评论用户主键
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 发布时间
     */
    @Column(name = "publish_time")
    private Date publishTime;

    /**
     * 删除标识（0：正常；1：删除）
     */
    @Column(name = "del_flag")
    private String delFlag;

}
