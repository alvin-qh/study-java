package alvin.study.springboot.mybatis.util.collection;

import java.util.Map;

/**
 * 同时存储两个相关的值
 *
 * @see java.util.Map.Entry
 */
public class Pair<F, S> implements Map.Entry<F, S> {
    // 第一个值
    private F first;

    // 第二个值
    private S second;

    /**
     * 初始化对象
     *
     * @param first  第一个值
     * @param second 第二个值
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * 获取第一个值
     *
     * @return 第一个值
     */
    public F getFirst() { return first; }

    /**
     * 设置第一个值
     *
     * @param first 第一个值
     */
    public void setFirst(F first) { this.first = first; }

    /**
     * 获取第二个值
     *
     * @return 第二个值
     */
    public S getSecond() { return second; }

    /**
     * 设置第二个值
     *
     * @param first 第二个值
     */
    public void setSecond(S second) { this.second = second; }

    @Override
    public F getKey() { return this.first; }

    @Override
    public S getValue() { return this.second; }

    @Override
    public S setValue(S second) {
        var old = this.second;
        this.second = second;
        return old;
    }
}
