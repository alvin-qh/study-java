package alvin.study.se.concurrent.util;

import java.util.LinkedHashMap;

/**
 * 用于检测锁是否公平的计数器类
 */
public class FaireCounter {
    private LinkedHashMap<Integer, Integer> counter = new LinkedHashMap<>();
    private int count = 0;
    private int maxCount;

    /**
     * 构造器, 用于初始化计数器
     *
     * @param maxCount 最大计数次数
     */
    public FaireCounter(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 添加计数值
     *
     * @param index 计数值所属的分组
     * @return 是否添加成功, 当计数次数达到最大次数时返回 `false`
     */
    public boolean add(int group) {
        if (this.count++ >= this.maxCount) {
            return false;
        }

        // 将所属分组的计数次数加 `1`
        this.counter.compute(group, (k, v) -> {
            if (v == null) {
                v = 0;
            }
            v++;
            return v;
        });
        return true;
    }

    /**
     * 计算各分组计数次数的平均值
     *
     * @return 平均值结果
     */
    public double mean() {
        return this.counter.values()
                .stream()
                .mapToDouble(v -> v)
                .average()
                .orElse(0);
    }

    /**
     * 计算各分组计数次数的方差
     *
     * @return 方差结果
     */
    public double var() {
        var mean = this.mean();

        return Math.sqrt(this.counter.values()
                .stream()
                .mapToDouble(v -> v)
                .map(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0));
    }

    /**
     * 计算各分组计数次数的标准差
     *
     * @return 标准差结果
     */
    public double std() {
        return Math.sqrt(this.var());
    }

    /**
     * 计算各分组计算结果是否满足公平性
     *
     * <p>
     * 通过计算各分组计数的标注差, 并于所给的阈值进行比较
     * </p>
     *
     * @param threshold 阈值
     * @return 返回 `true` 表示满足公平性
     */
    public boolean isFair(double threshold) {
        return this.std() < threshold;
    }
}
