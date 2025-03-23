package alvin.study.guava.collect;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;

/**
 * 代理 {@link LinkedHashMap} 类型的类型
 */
public class IdMap extends ForwardingMap<Long, String> {
    // 设置被代理的对象
    private final Map<Long, String> delegatedMap = new LinkedHashMap<>();

    /**
     * 获取被代理的对象
     *
     * @return 被代理的 {@link List} 类型对象
     */
    @Override
    protected Map<Long, String> delegate() {
        return delegatedMap;
    }

    /**
     * 重写 {@link Map#put(Object, Object)} 方法
     *
     * @param key   要添加的键值
     * @param value 要添加的 Value 值
     * @return 如果添加成功则返回 {@code true}, 反之返回 {@code false}
     * @throws NullPointerException     如果 {@code key} 参数为 {@code null} 时,
     *                                  抛出该异常
     * @throws IllegalArgumentException 如果 {@code value} 参数为 {@code null}
     *                                  或空字符串时, 抛出该异常
     */
    @Override
    public String put(Long key, String value) {
        Preconditions.checkNotNull(key);

        if (Strings.isNullOrEmpty(value)) {
            throw new IllegalArgumentException(
                "value argument cannot be null or empty");
        }

        return delegatedMap.put(key, value);
    }

    /**
     * 重写 {@link Map#putAll(Map)} 方法
     *
     * @param map 要添加的元素
     * @throws IllegalArgumentException 如果 {@code collection} 参数中包含值为空字符串或
     *                                  {@code null} 的元素 时, 抛出该异常
     */
    @Override
    public void putAll(Map<? extends Long, ? extends String> map) {
        Preconditions.checkNotNull(map).forEach((key, value) -> {
            Preconditions.checkNotNull(key);
            if (Strings.isNullOrEmpty(value)) {
                throw new IllegalArgumentException(
                    "map argument cannot include null or empty string value");
            }
        });

        delegatedMap.putAll(map);
    }

    /**
     * 重写 {@link Map#entrySet()} 方法
     *
     * <p>
     * 本方法返回 {@link IdMapEntry} 类型 (即 {@link Map.Entry} 类型的代理类型)
     * 的 {@link Set} 集合
     * </p>
     *
     * @return {@link IdMapEntry} 类型的 {@link Set} 集合
     */
    @Override
    public Set<Entry<Long, String>> entrySet() {
        return delegatedMap.entrySet().stream()
                .map(IdMapEntry::new)
                .collect(Collectors.toSet());
    }

    /**
     * 代理 {@link Map.Entry} 类型
     */
    static class IdMapEntry extends ForwardingMapEntry<Long, String> {
        // 被代理的 Map.Entry 对象
        private final Map.Entry<Long, String> delegatedEntry;

        /**
         * 构造器, 设置被代理的 {@link Map.Entry} 类型对象
         *
         * @param delegatedEntry {@link Map.Entry} 类型对象
         */
        IdMapEntry(Entry<Long, String> delegatedEntry) {
            this.delegatedEntry = delegatedEntry;
        }

        /**
         * 构造一个 {@link IdMapEntry} 类型对象
         *
         * @param key   键值
         * @param value value 值
         * @return {@link IdMapEntry} 对象
         */
        public static Entry<Long, String> entry(Long key, String value) {
            Preconditions.checkNotNull(key);
            if (Strings.isNullOrEmpty(value)) {
                throw new IllegalArgumentException(
                    "value argument cannot be null or empty");
            }
            return new IdMapEntry(Map.entry(key, value));
        }

        /**
         * 获取被代理的 {@link Map.Entry} 类型对象
         *
         * @return {@link Map.Entry} 类型对象
         */
        @Override
        protected Entry<Long, String> delegate() {
            return delegatedEntry;
        }
    }
}
