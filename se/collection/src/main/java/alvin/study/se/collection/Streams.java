package alvin.study.se.collection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Streams {
    /**
     * 将不定参数转为 {@link Stream} 对象
     *
     * @param <T>  集合元素类型
     * @param elem 不定参数
     * @return 由 {@code elem} 组成的 {@link Stream} 对象
     */
    @SafeVarargs
    public static <T> Stream<T> stream(T... elem) {
        return Arrays.stream(elem);
    }

    /**
     * 将可迭代对象转为 {@link Stream} 对象
     *
     * @param <T>      集合元素对象
     * @param iterable 可迭代对象
     * @return 参数 {@code iterable} 形成的 {@link Stream} 对象
     */
    static <T> Stream<T> stream(Iterable<T> iterable) {
        if (iterable == null) {
            return Stream.empty();
        }
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * 将可迭代对象转为 {@link Stream} 对象
     *
     * @param <T>      集合元素对象
     * @param iterable 可迭代对象
     * @param omitNull 是否过滤 {@code null} 元素
     * @return 参数 {@code iterable} 形成的 {@link Stream} 对象
     */
    public static <T> Stream<T> stream(Iterable<T> iterable, boolean omitNull) {
        if (omitNull) {
            return stream(iterable).filter(Objects::nonNull);
        }
        return stream(iterable);
    }

    /**
     * 将迭代器对象转为 {@link Stream} 对象
     *
     * @param <T>  集合元素类型
     * @param iter 迭代器对象
     * @return 参数 {@code iter} 形成的 {@link Stream} 对象
     */
    static <T> Stream<T> stream(Iterator<T> iter) {
        if (iter == null) {
            return Stream.empty();
        }
        return stream(() -> iter);
    }

    /**
     * 将迭代器对象转为 {@link Stream} 对象
     *
     * @param <T>      集合元素类型
     * @param iter     迭代器对象
     * @param omitNull 是否剔除为 {@code null} 的元素
     * @return 参数 {@code iter} 形成的 {@link Stream} 对象
     */
    public static <T> Stream<T> stream(Iterator<T> iter, boolean omitNull) {
        if (omitNull) {
            return stream(iter).filter(Objects::nonNull);
        }
        return stream(iter);
    }

    /**
     * 将一组数字转换成 {@link Stream} 对象
     *
     * @param <R>      将 {@code int} 转换后的类型
     * @param from     起始数值, 表示一个 {@code [from, to)} 的取值范围
     * @param to       终止数值, 表示一个 {@code [from, to)} 的取值范围
     * @param mapper   转换函数
     * @param omitNull 是否过滤结果中的 {@code null} 元素
     * @return {@link Stream} 对象
     */
    public static <R> Stream<R> range(int from, int to, IntFunction<R> mapper, boolean omitNull) {
        if (from >= to) {
            return Stream.empty();
        }
        var stream = IntStream.range(from, to).mapToObj(mapper);
        if (omitNull) {
            stream = stream.filter(Objects::nonNull);
        }
        return stream;
    }

    /**
     * 将一组数字转换成 {@link Stream} 对象
     *
     * @param <R>    将 {@code int} 转换后的类型
     * @param n      表示一个 {@code [0, n)} 的取值区间
     * @param mapper 转换函数
     * @return {@link Stream} 对象
     */
    public static <R> Stream<R> range(int n, IntFunction<R> mapper, boolean omitNull) {
        var stream = IntStream.range(0, n).mapToObj(mapper);
        if (omitNull) {
            stream = stream.filter(Objects::nonNull);
        }
        return stream;
    }

    /**
     * 将 {@link Stream} 对象转换成 {@link List} 对象
     *
     * @param <T>      {@code stream} 参数的的元素类型
     * @param <R>      返回集合的元素类型
     * @param stream   {@link Stream} 对象
     * @param mapper   转换函数
     * @param omitNull 是否过滤 {@code null} 值
     * @return {@code stream} 参数转换的 {@link List} 对象
     */
    public static <T, R> List<R> toList(
            Stream<? extends T> stream, Function<? super T, ? extends R> mapper, boolean omitNull) {
        var mappedStream = stream.<R>map(mapper);
        if (omitNull) {
            mappedStream = mappedStream.filter(Objects::nonNull);
        }
        return mappedStream.toList();
    }

    /**
     * 创建一个返回 {@link LinkedHashSet} 对象的 {@link Collector} 对象
     *
     * @param <T> 元素类型
     * @return 能够将 {@link Stream} 对象转为 {@link LinkedHashSet} 对象的 {@link Collector}
     *         对象
     */
    private static <T> Collector<T, ?, Set<T>> toLinkedSet() {
        return Collector.of(
            // 集合对象创建函数
            LinkedHashSet<T>::new,
            // 元素添加函数
            Set::add,
            // 集合合并函数
            (left, right) -> {
                left.addAll(right);
                return left;
            });
    }

    /**
     * 将 {@link Stream} 对象转换成 {@link Set} 对象
     *
     * @param <T>      {@code stream} 参数的的元素类型
     * @param <R>      返回集合的元素类型
     * @param stream   {@link Stream} 对象
     * @param mapper   对象转换函数
     * @param omitNull 是否过滤 {@code null} 元素
     * @return {@code stream} 转换得到的 {@link Set} 对象
     */
    public static <T, R> Set<R> toSet(
            Stream<? extends T> stream,
            Function<? super T, ? extends R> mapper,
            boolean omitNull) {
        var rs = stream.map(mapper);
        if (omitNull) {
            rs = rs.filter(Objects::nonNull);
        }
        return rs.collect(toLinkedSet());
    }

    /**
     * 创建一个返回 {@link LinkedHashMap} 对象的 {@link Collector} 对象
     *
     * @param <T> 元素类型
     * @return 能够将 {@link Stream} 对象转为 {@link LinkedHashMap} 对象的 {@link Collector}
     *         对象
     */
    private static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper,
            boolean omitKeyNull) {
        return Collector.of(
            // 集合对象创建函数
            LinkedHashMap::new,
            // 集合元素添加函数
            (map, entity) -> {
                var key = keyMapper.apply(entity);
                if (omitKeyNull && key == null) {
                    return;
                }
                map.merge(key, valueMapper.apply(entity), (left, right) -> right);
            },
            // 集合合并函数
            (left, right) -> {
                left.putAll(right);
                return left;
            });
    }

    /**
     * 将 {@link Stream} 对象转化为 {@link Map} 对象
     *
     * @param <K>         要返回 {@link Map} 的 {@code Key} 值类型
     * @param <V>         要返回 {@link Map} 的 {@code Value} 值类型
     * @param <U>         参数 {@code stream} 的元素类型
     * @param stream      {@link Stream} 对象, 包含集合元素
     * @param keyMapper   将集合元素转为 {@code Key} 值的函数对象
     * @param valueMapper 将集合元素转为 {@code Value} 值的函数对象
     * @param omitKeyNull 是否过滤掉为 {@code null} 的 {@code Key} 值
     * @return {@link Map} 对象
     */
    public static <K, V, U> Map<K, V> toMap(
            Stream<? extends U> stream,
            Function<? super U, ? extends K> keyMapper,
            Function<? super U, ? extends V> valueMapper,
            boolean omitKeyNull) {
        return stream.collect(toLinkedMap(keyMapper, valueMapper, omitKeyNull));
    }

    /**
     * 给 {@link Stream} 对象进行分组, 转化为 {@link Map} 对象
     *
     * @param <K>         要返回 {@link Map} 的 {@code Key} 值类型
     * @param <V>         要返回 {@link Map} 的 {@code Value} 值类型, 同时也是 {@code stream}
     *                    参数的元素类型
     * @param stream      {@link Stream} 对象, 包含集合元素
     * @param keyMapper   将集合元素转为 {@code Key} 值的函数对象
     * @param omitKeyNull 是否过滤掉为 {@code null} 的 {@code Key} 值
     * @return {@link Map} 对象
     */
    public static <K, V> Map<K, V> toMap(
            Stream<? extends V> stream,
            Function<? super V, ? extends K> keyMapper,
            boolean omitKeyNull) {
        return toMap(stream, keyMapper, Function.identity(), omitKeyNull);
    }

    /**
     * 将 {@link Stream} 对象平铺为 {@link List} 对象
     *
     * @param <T>      {@code stream} 参数的元素类型
     * @param <R>      返回结果的元素类型
     * @param stream   {@link Stream} 对象
     * @param mapper   类型转换对象
     * @param omitNull 是否过滤结果中为 {@code null} 的元素
     * @return 转换后的 {@link List} 对象
     */
    @SuppressWarnings("unchecked")
    public static <T, R> List<T> flatList(
            Stream<? extends R> stream,
            Function<? super R, Stream<? extends T>> mapper,
            boolean omitNull) {
        var rs = stream.flatMap(mapper);
        if (omitNull) {
            rs = rs.filter(Objects::nonNull);
        }
        return (List<T>) rs.toList();
    }

    /**
     * 将 {@link Stream} 对象平铺为 {@link Set} 对象
     *
     * @param <T>      {@code stream} 参数的元素类型
     * @param <R>      返回结果的元素类型
     * @param stream   {@link Stream} 对象
     * @param mapper   类型转换函数对象
     * @param omitNull 是否规律结果中为 {@code null} 的元素项
     * @return 转换后的 {@link Set} 对象
     */
    public static <T, R> Set<T> flatSet(
            Stream<? extends R> stream,
            Function<? super R, Stream<? extends T>> mapper,
            boolean omitNull) {
        var rs = stream.flatMap(mapper);
        if (omitNull) {
            rs = rs.filter(Objects::nonNull);
        }
        return rs.collect(Collectors.toSet());
    }
}
