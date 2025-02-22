package alvin.study.springboot.shiro.util.collection;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 同时存储两个相关的值
 *
 * @see java.util.Map.Entry
 */
@Getter
@Setter
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
