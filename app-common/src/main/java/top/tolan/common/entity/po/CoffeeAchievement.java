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
 * 咖啡店打卡成就信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coffee_achievement")
public class CoffeeAchievement {

    /**
     * 主键
     */
    @Id
    @Column(name = "achievement_id")
    @GeneratedValue(generator = "JDBC")
    private Integer achievementId;

    /**
     * 用户主键
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 店铺主键
     */
    @Column(name = "store_id")
    private Integer storeId;

    /**
     * 达成时间
     */
    @Column(name = "reach_time")
    private Date reachTime;

}
