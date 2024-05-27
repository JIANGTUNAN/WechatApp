package top.tolan.common.utils;

import java.util.*;

/**
 * 协同过滤推荐工具类
 *
 * @param <T> 用户ID的类型
 * @param <k> 物品ID的类型
 */
public class CollaborativeFilteringUtil<T, k> {

    // 矩阵
    // 每一行代表一个用户，每一列代表一个物品
    private final Map<T, List<k>> matrix;

    public CollaborativeFilteringUtil(Map<T, List<k>> matrix) {
        this.matrix = matrix;
    }

    /**
     * 为指定用户生成推荐
     * <p>
     * 生成推荐的方式是，计算该用户与所有其他用户的相似度
     * 然后根据相似度和其他用户喜欢的物品生成推荐
     * 最后返回的是一个物品id的列表，按照推荐的分数从高到低排序
     *
     * @param userId 用户ID
     * @return 推荐的物品ID列表
     */
    public List<k> recommend(T userId) {
        // 存储每个物品的推荐分数
        Map<k, Double> itemScores = new HashMap<>();

        // 获取用户喜欢的物品列表
        List<k> userLikesList = matrix.get(userId);

        // 遍历所有其他用户
        for (T otherUserId : matrix.keySet()) {
            // 不与自己进行比较
            if (otherUserId == userId) continue;

            // 计算相似度
            double similarity = calculateSimilarity(userLikesList, matrix.get(otherUserId));

            // 遍历其他用户喜欢的物品
            for (k itemId : matrix.get(otherUserId)) {
                // 计算推荐分数，相似度越高，推荐分数越高
                itemScores.put(itemId, itemScores.getOrDefault(itemId, 0.0) + similarity);
            }
        }

        // 将物品和对应的推荐分数转换为列表，准备进行排序
        List<Map.Entry<k, Double>> itemList = new ArrayList<>(itemScores.entrySet());
        // 按照推荐分数从高到低排序
        itemList.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));

        // 创建一个列表，用于存储最终的推荐结果（这次存储的是商品ID）
        List<k> recommendations = new ArrayList<>();
        // 将排序后的物品ID添加到推荐结果列表中
        for (Map.Entry<k, Double> entry : itemList) {
            recommendations.add(entry.getKey());
        }

        // 返回推荐结果
        return recommendations;
    }


    /**
     * 计算两个用户的相似度
     * <p>
     * 这里使用的是余弦相似度
     * 相似度计算的基础是两个用户共同喜欢的物品数量
     *
     * @param user1Likes 用户1喜欢的物品列表
     * @param user2Likes 用户2喜欢的物品列表
     * @return 两个用户的相似度
     */
    private double calculateSimilarity(List<k> user1Likes, List<k> user2Likes) {

        int commonLikes = 0;

        for (k item : user1Likes) {
            if (user2Likes.contains(item)) {
                commonLikes++;
            }
        }

        if (user1Likes.isEmpty() || user2Likes.isEmpty()) {
            return 0.0;
        }

        /**
         * 这行代码计算的是两个用户的余弦相似度。
         * 在这个上下文中，每个用户可以看作是一个向量，向量的每一维对应一个物品，如果用户喜欢该物品，
         * 那么在该维度上的值为1，否则为0。因此，两个用户共同喜欢的物品的数量就是这两个向量的点积。
         * 余弦相似度的计算公式为：
         *
         * 余弦相似度= (向量A·向量B) / (向量A的模长*向量B的模长)
         *
         * 在这个代码中，{@link commonLikes} 对应的是点积，
         * {@link user1Likes#size()} 和 {@link user2Likes#size()}
         * 对应的是每个向量的模长的平方（因为每个维度的值都是1或0），
         * 所以需要开平方得到模长。因此，这行代码就是在计算余弦相似度。
         */
        return (double) commonLikes / (Math.sqrt(user1Likes.size()) * Math.sqrt(user2Likes.size()));
    }

}
