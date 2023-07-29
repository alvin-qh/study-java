package alvin.study.se.collection;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Streams} 工具类
 */
class StreamsTest {
    /**
     * 过滤掉整数集合中的偶数项, 并将整数以字符串形式返回
     *
     * @param n 整数元素项
     * @return 如果 {@code n} 是偶数, 则返回 {@code null}, 否则返回该整数的字符串形式
     */
    private static String filterOdd(Integer n) {
        if (n % 2 == 0) {
            return null;
        }
        return String.valueOf(n);
    }

    /**
     * 测试 {@link Streams#stream(Object...)} 方法
     */
    @Test
    void stream_shouldConvertElementsToStream() {
        var stream = Streams.stream(1, 2, 3, 4, 5);
        then(stream).containsExactly(1, 2, 3, 4, 5);
    }

    /**
     * 测试 {@link Streams#stream(Iterable, boolean)} 方法
     */
    @Test
    void stream_shouldConvertIterableToStream() {
        var iter = (Iterable<Integer>) Colls.append(List.of(), 1, 2, null, 3, 4);

        // 确认不过滤 null 的结果
        var stream = Streams.stream(iter, false);
        then(stream).containsExactly(1, 2, null, 3, 4);

        // 确认过滤 null 的结果
        stream = Streams.stream(iter, true);
        then(stream).containsExactly(1, 2, 3, 4);
    }

    /**
     * 测试 {@link Streams#stream(java.util.Iterator, boolean)
     * Streams.stream(Iterator, boolean)} 方法
     */
    @Test
    void stream_shouldConvertIteratorToStream() {
        var colls = Colls.append(List.of(), 1, 2, null, 3, 4);

        // 确认不过滤 null 的结果
        var stream = Streams.stream(colls.iterator(), false);
        then(stream).containsExactly(1, 2, null, 3, 4);

        // 确认过滤 null 的结果
        stream = Streams.stream(colls.iterator(), true);
        then(stream).containsExactly(1, 2, 3, 4);
    }

    /**
     * 测试 {@link Streams#range(int, java.util.function.IntFunction, boolean)
     * Streams.range(int, IntFunction, boolean)}
     */
    @Test
    void range_shouldGetCountValueStream() {
        // 确认不过滤 null 的结果
        var results = Streams.range(5, String::valueOf, false);
        then(results).containsExactly("0", "1", "2", "3", "4");

        // 确认过滤 null 的结果
        results = Streams.range(5, StreamsTest::filterOdd, true);
        then(results).containsExactly("1", "3");
    }

    /**
     * 测试 {@link Streams#range(int, int, java.util.function.IntFunction, boolean)
     * Streams.range(int, int, IntFunction, boolean)}
     */
    @Test
    void range_shouldGetRangeValueStream() {
        // 确认不过滤 null 的结果
        var results = Streams.range(1, 5, String::valueOf, false);
        then(results).containsExactly("1", "2", "3", "4");

        // 确认过滤 null 的结果
        results = Streams.range(1, 5, StreamsTest::filterOdd, true);
        then(results).containsExactly("1", "3");
    }

    /**
     * 测试
     * {@link Streams#toList(java.util.stream.Stream, java.util.function.Function, boolean)
     * Streams.toList(Stream, Function, boolean)}
     */
    @Test
    void toList_shouldConvertStreamToList() {
        var coll = List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Streams.toList(coll.stream(), String::valueOf, false);
        then(results).containsExactly("1", "2", "3", "4");

        // 确认过滤 null 的结果
        results = Streams.toList(coll.stream(), StreamsTest::filterOdd, true);
        then(results).containsExactly("1", "3");
    }

    /**
     * 测试
     * {@link Streams#toSet(java.util.stream.Stream, java.util.function.Function, boolean)
     * Streams.toSet(Stream, Function, boolean)}
     */
    @Test
    void toSet_shouldConvertStreamToSet() {
        var coll = List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Streams.toSet(coll.stream(), String::valueOf, false);
        then(results).containsExactly("1", "2", "3", "4");

        // 确认过滤 null 的结果
        results = Streams.toSet(coll.stream(), StreamsTest::filterOdd, true);
        then(results).containsExactly("1", "3");
    }

    /**
     * 测试
     * {@link Streams#toMap(java.util.stream.Stream, java.util.function.Function, java.util.function.Function, boolean)
     * Streams.toMap(Stream, Function, Function, boolean)}
     */
    @Test
    void toMap_shouldConvertStreamToMap() {
        var coll = List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Streams.toMap(
            coll.stream(),
            String::valueOf,
            n -> String.format("%d:%d", n, n),
            false);

        then(results).containsExactly(
            entry("1", "1:1"),
            entry("2", "2:2"),
            entry("3", "3:3"),
            entry("4", "4:4"));

        // 确认过滤 null 的结果
        results = Streams.toMap(coll.stream(), StreamsTest::filterOdd, n -> String.format("%d:%d", n, n), true);
        then(results).containsExactly(entry("1", "1:1"), entry("3", "3:3"));
    }

    /**
     * 测试
     * {@link Streams#toMap(java.util.stream.Stream, java.util.function.Function, boolean)
     * Streams.toMap(Stream, Function, boolean)}
     */
    @Test
    void toMap_shouldConvertStreamToGroupedMap() {
        var coll = List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Streams.toMap(coll.stream(), String::valueOf, false);
        then(results).containsExactly(
            entry("1", 1),
            entry("2", 2),
            entry("3", 3),
            entry("4", 4));

        // 确认过滤 null 的结果
        results = Streams.toMap(coll.stream(), StreamsTest::filterOdd, true);
        then(results).containsExactly(entry("1", 1), entry("3", 3));
    }

    /**
     * 测试 {@link Streams#flatList(java.util.stream.Stream, java.util.function.Function, boolean)
     * Streams.flatList(Stream, Function, boolean)} 方法
     */
    @Test
    void flatList_shouldConvertStreamToList() {
        var coll = List.of(
            Colls.list(1, 2, null),
            Colls.list(4, 5),
            Colls.list(null, 7, 8, 9));

        // 将一个嵌套集合的 stream 平铺, 确认平铺结果
        var results = Streams.flatList(coll.stream(), Collection::stream, false);
        then(results).containsExactly(1, 2, null, 4, 5, null, 7, 8, 9);

        // 将一个 Stream 对象平铺, 并过滤掉 null 的部分, 确认平铺结果
        results = Streams.flatList(coll.stream(), Collection::stream, true);
        then(results).containsExactly(1, 2, 4, 5, 7, 8, 9);
    }
}
