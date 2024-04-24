package top.tolan.common.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 咖啡店评论图片信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coffee_comment_image")
public class CoffeeCommentImage {

    /**
     * 评论图片主键
     */
    @Id
    @Column(name = "comment_image_id")
    @GeneratedValue(generator = "JDBC")
    private Integer commentImageId;

    /**
     * 评论主键
     */
    @Column(name = "comment_id")
    private Integer commentId;

    /**
     * 评论顺序
     */
    @Column(name = "comment_sort")
    private Integer commentSort;

    /**
     * 图片访问地址
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 图片存储地址
     */
    @Column(name = "image_path")
    private String imagePath;

    /**
     * 评论用户主键
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 店铺id
     */
    @Column(name = "store_id")
    private Integer storeId;

    /**
     * 删除标识（0：正常；1：删除）
     */
    @Column(name = "del_flag")
    private String delFlag;
}
