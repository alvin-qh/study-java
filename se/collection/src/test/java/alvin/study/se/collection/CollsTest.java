package alvin.study.se.collection;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试 {@link Colls} 集合工具类
 */
class CollsTest {
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
     * 测试 {@link Colls#isEmpty(Collection)} 方法
     */
    @Test
    void isEmpty_shouldCheckCollectionIfEmpty() {
        // 确认空集合返回 true
        then(Colls.isEmpty(List.of())).isTrue();
        then(Colls.isEmpty(Set.of())).isTrue();

        // 确认非空集合返回 false
        then(Colls.isEmpty(List.of(1))).isFalse();
        then(Colls.isEmpty(Set.of(1))).isFalse();
    }

    /**
     * 测试 {@link Colls#isEmpty(Object[])} 方法
     */
    @Test
    void isEmpty_shouldCheckArrayIfEmpty() {
        // 确认空集合返回 true
        then(Colls.isEmpty(new Integer[0])).isTrue();

        // 确认非空集合返回 false
        then(Colls.isEmpty(new Integer[]{ 1 })).isFalse();
    }

    /**
     * 测试 {@link Colls#isEmpty(Map)} 方法
     */
    @Test
    void isEmpty_shouldCheckMapIfEmpty() {
        // 确认空集合返回 true
        then(Colls.isEmpty(Map.of())).isTrue();

        // 确认非空集合返回 false
        then(Colls.isEmpty(Map.of("A", 100))).isFalse();
    }

    /**
     * 测试 {@link Colls#first(Collection)} 方法
     */
    @Test
    void first_shouldGetFirstElementOfCollection() {
        // 定义一个空集合
        var coll = (Collection<Integer>) List.<Integer>of();
        // 确认空集合的第一个元素不存在
        var first = Colls.first(coll);
        then(first).isEmpty();

        // 定义一个非空集合
        coll = List.of(1, 2, 3, 4);
        // 确认非空集合的第一个元素
        first = Colls.first(coll);
        then(first.isPresent()).isTrue();
        then(first.get()).isEqualTo(1);

        // 定义一个非空集合
        coll = new LinkedHashSet<>(List.of(1, 2, 3, 4));
        // 确认非空集合的第一个元素
        first = Colls.first(coll);
        then(first).isPresent().get().isEqualTo(1);
    }

    /**
     * 测试 {@link Colls#first(List)} 方法
     */
    @Test
    void first_shouldGetFirstElementOfList() {
        // 定义一个空集合
        var l = List.<Integer>of();
        // 确认空集合的第一个元素不存在
        var first = Colls.first(l);
        then(first).isEmpty();

        // 定义一个非空集合
        l = List.of(1, 2, 3);
        // 确认非空集合的第一个元素
        first = Colls.first(l);
        then(first).isPresent().get().isEqualTo(1);
    }

    /**
     * 测试 {@link Colls#only(Collection)} 方法
     */
    @Test
    void only_shouldGetOnlyElementOfCollection() {
        // 定义一个空集合
        var coll = (Collection<Integer>) Set.<Integer>of();
        // 确认空集合不存在唯一元素
        var only = Colls.only(coll);
        then(only).isEmpty();

        // 定义一个具备 2 个元素的集合
        coll = Set.of(1, 2);
        // 确认多于 1 个元素的集合无唯一元素
        only = Colls.only(coll);
        then(only).isEmpty();

        // 定义一个具备 1 个元素的集合
        coll = Set.of(1);
        // 确认 1 个元素的集合的唯一元素
        only = Colls.only(coll);
        then(only).isPresent().get().isEqualTo(1);

        // 定义一个具备 1 个元素的集合
        coll = new LinkedHashSet<>(List.of(1));
        // 确认 1 个元素的集合的唯一元素
        only = Colls.first(coll);
        then(only).isPresent().get().isEqualTo(1);
    }

    /**
     * 测试 {@link Colls#only(List)} 方法
     */
    @Test
    void only_shouldGetOnlyElementOfList() {
        // 定义一个空集合
        var coll = List.<Integer>of();
        // 确认空集合不存在唯一元素
        var only = Colls.only(coll);
        then(only).isEmpty();

        // 定义一个具备 2 个元素的集合
        coll = List.of(1, 2);
        // 确认多于 1 个元素的集合无唯一元素
        only = Colls.only(coll);
        then(only).isEmpty();

        // 定义一个具备 1 个元素的集合
        coll = List.of(1);
        // 确认 1 个元素的集合的唯一元素
        only = Colls.only(coll);
        then(only).isPresent().get().isEqualTo(1);
    }

    /**
     * 测试 {@link Colls#array(Object...)} 方法
     */
    @Test
    void array_shouldGenerateArray() {
        var array = Colls.array(1, 2, 3);
        then(array).containsExactly(1, 2, 3);
    }

    /**
     * 测试 {@link Colls#list(Object...)} 方法
     */
    @Test
    void list_shouldGenerateListByArguments() {
        // 产生一个 List 对象
        var l = Colls.list();
        then(l).isEmpty();

        // 确认返回的 List 对象可被修改
        l.add(1);
        then(l).containsExactly(1);

        // 产生一个非空 List 对象
        l = Colls.list(1, 2, 3);
        then(l).containsExactly(1, 2, 3);

        // 确认返回的 List 对象可被修改
        l.add(4);
        then(l).containsExactly(1, 2, 3, 4);
    }

    /**
     * 测试 {@link Colls#list(java.util.Iterator)
     * Colls#list(Iterator)} 方法
     */
    @Test
    void list_shouldGenerateListByIterator() {
        // 产生一个迭代器对象
        var iter = List.of(1, 2, 3).iterator();

        // 将迭代器对象转为 List 对象
        var l = Colls.list(iter);
        then(l).containsExactly(1, 2, 3);
    }

    /**
     * 测试 {@link Colls#list(Iterable)} 方法
     */
    @Test
    void list_shouldGenerateListByIterable() {
        // 产生一个可迭代对象
        var iter = (Iterable<Integer>) List.of(1, 2, 3);

        // 将迭代器对象转为 List 对象
        var l = Colls.list(iter);
        then(l).containsExactly(1, 2, 3);
    }

    /**
     * 测试 {@link Colls#list(Collection)} 方法
     */
    @Test
    void list_shouldGenerateListByCollection() {
        // 产生 List 对象
        var coll = (Collection<Integer>) List.of(1, 2, 3);

        // 将集合对象转为 List 对象
        var l = Colls.list(coll);
        then(l).containsExactly(1, 2, 3);
    }

    /**
     * 测试 {@link Colls#set(Object...)} 方法
     */
    @Test
    void set_shouldGenerateSetByArguments() {
        // 产生一个空 Set 对象
        var s = Colls.<Integer>set();
        then(s).isEmpty();

        // 确认返回的 Set 对象可被修改
        s.add(1);
        then(s).containsExactly(1);

        // 产生一个非空 Set 对象
        s = Colls.set(1, 2, 3);
        then(s).containsExactly(1, 2, 3);

        // 确认返回的 Set 对象可被修改
        s.add(4);
        then(s).containsExactly(1, 2, 3, 4);
    }

    /**
     * 测试 {@link Colls#set(java.util.Iterator) Colls.set(Iterator)} 方法
     */
    @Test
    void set_shouldGenerateSetByIterator() {
        // 产生一个迭代器对象
        var iter = List.of(1, 2, 3).iterator();

        // 将迭代器对象转为 Set 对象
        var s = Colls.set(iter);
        then(s).containsExactly(1, 2, 3);
    }

    /**
     * 测试 {@link Colls#set(Iterable)} 方法
     */
    @Test
    void set_shouldGenerateSetByIterable() {
        // 产生一个可迭代对象
        var iter = (Iterable<Integer>) List.of(1, 2, 3);

        // 将可迭代对象转为 Set 对象
        var s = Colls.set(iter);
        then(s).containsExactly(1, 2, 3);
    }

    /**
     * 测试 {@link Colls#set(Collection)} 方法
     */
    @Test
    void set_shouldGenerateSetByCollection() {
        // 产生集合对象
        var coll = (Collection<Integer>) List.of(1, 2, 3);

        // 将集合对象转为 List 对象
        var s = Colls.set(coll);
        then(s).containsExactly(1, 2, 3);
    }

    /**
     * 测试 {@link Colls#addAll(Collection, java.util.Iterator)
     * Colls.addAll(Collection, Iterator)} 方法
     */
    @Test
    void addAll_shouldAddIteratorIntoCollection() {
        var coll = Colls.list(1, 2, 3);
        var iter = List.of(4, 5, 6).iterator();

        // 将迭代器的元素加入集合
        Colls.addAll(coll, iter);
        then(coll).containsExactly(1, 2, 3, 4, 5, 6);
    }

    /**
     * 测试 {@link Colls#addAll(Collection, Iterable)} 方法
     */
    @Test
    void addAll_shouldAddIterableIntoCollection() {
        var coll = Colls.list(1, 2, 3);
        var iter = (Iterable<Integer>) List.of(4, 5, 6);

        // 将迭代器的元素加入集合
        Colls.addAll(coll, iter);
        then(coll).containsExactly(1, 2, 3, 4, 5, 6);
    }

    /**
     * 测试 {@link Colls#append(Set, Object...)} 方法
     */
    @Test
    void append_shouldAppendElementsToSet() {
        // 确认追加到一个 null 引用会产生一个新 Set 集合
        var set = Colls.append(null, 1, 2, 3);
        then(set).containsExactly(1, 2, 3);

        // 确认向已有的 Set 集合添加元素
        set = Colls.append(set, 3, 4, 5);
        then(set).containsExactly(1, 2, 3, 4, 5);
    }

    /**
     * 测试 {@link Colls#append(Collection, Object...)} 方法
     */
    @Test
    void append_shouldAppendElementsToCollection() {
        // 确认追加到一个 null 引用会产生一个新 List 集合
        var l = Colls.append((Collection<Integer>) null, 1, 2, 3);
        then(l).containsExactly(1, 2, 3);

        // 确认向已有的集合添加元素
        l = Colls.append(l, 3, 4, 5);
        then(l).containsExactly(1, 2, 3, 3, 4, 5);
    }

    /**
     * 测试 {@link Colls#containsAny(Collection, Object...)} 方法
     */
    @Test
    void containsAny_shouldCheckAnyValueInCollection() {
        var rand = new Random();

        // 产生一个 [1..10) 的集合
        var coll = (Collection<Integer>) IntStream.range(1, 10).boxed().toList();

        // 产生 3 个 [1..10) 范围内的参数
        var p1 = rand.nextInt(1, 10);
        then(p1).isGreaterThanOrEqualTo(1).isLessThan(10);

        var p2 = rand.nextInt(1, 10);
        then(p2).isGreaterThanOrEqualTo(1).isLessThan(10);

        var p3 = rand.nextInt(1, 10);
        then(p3).isGreaterThanOrEqualTo(1).isLessThan(10);

        // 确认参数中只要包含 [1..10) 的任意值, 结果都为 true
        then(Colls.containsAny(coll, p1, p2, p3, 10)).isTrue();

        // 确认参数中不包含 [1..99] 的任意值, 结果就为 false
        then(Colls.containsAny(coll, 10, 11)).isFalse();
    }

    /**
     * 测试 {@link Colls#containsAny(Collection, Collection)} 方法
     */
    @Test
    void containsAny_shouldCheckAnyOneCollectionInOther() {
        var rand = new Random();

        // 产生一个 [1..10) 的集合
        var left = (Collection<Integer>) IntStream.range(1, 10).boxed().toList();

        // 产生一个 [1..10) 内随机数集合
        var right = rand.ints(10, 1, 10).boxed().toList();
        for (var n : right) {
            then(n).isGreaterThanOrEqualTo(1).isLessThan(10);
        }

        // 添加几个不在 left 中的值
        right = Colls.append(right, 10, 11);

        // 确认 left 中包含 right 中的至少一个值
        assertTrue(Colls.containsAny(left, right));

        // 产生一个 [100, 150] 的集合
        right = rand.ints(10, 10, Integer.MAX_VALUE).boxed().toList();
        for (var n : right) {
            then(n).isGreaterThanOrEqualTo(10);
        }

        // 确认 left 中不包含 right 的任何值
        then(Colls.containsAny(left, right)).isFalse();
    }

    /**
     * 测试 {@link Colls#containsAll(Collection, Object...)} 方法
     */
    @Test
    void containsAll_shouldCheckAnyValueInCollection() {
        var rand = new Random();

        // 产生一个 [1..99] 的集合
        var coll = (Collection<Integer>) IntStream.range(1, 100).boxed().toList();

        // 确认所有参数均为 [1..99] 内的值, 结果为 true
        then(Colls.containsAll(coll, rand.nextInt(99) + 1, rand.nextInt(99) + 1, rand.nextInt(99) + 1)).isTrue();

        // 确认任意参数不在 [1..99] 范围内, 结果为 false
        then(Colls.containsAll(coll, rand.nextInt(99) + 1, rand.nextInt(99) + 1, 0)).isFalse();
    }

    /**
     * 测试 {@link Colls#containsAll(Collection, Collection)} 方法
     */
    @Test
    void containsAll_shouldCheckAnyOneCollectionInOther() {
        // 产生一个 [1..9] 的集合
        var left = (Collection<Integer>) IntStream.range(1, 10).boxed().toList();

        // 产生一个 [1..4] 的集合
        var right = (Collection<Integer>) IntStream.range(1, 5).boxed().toList();

        // 确认 left 中包含 right 中的全部值
        then(Colls.containsAll(left, right)).isTrue();

        // 添加一个不会在 left 集合中的值
        right = Colls.append(right, 0);

        // 确认 left 无法全部包含 right 的值
        then(Colls.containsAll(left, right)).isFalse();
    }

    /**
     * 测试 {@link Colls#removeAll(Collection, Collection)} 方法
     */
    @Test
    void removeAll_shouldRemoveElementsFromCollections() {
        // 产生一个 [1..9] 的集合
        var left = (Collection<Integer>) IntStream.range(1, 10).boxed().toList();

        // 产生一个 [1..4] 的集合
        var right = (Collection<Integer>) IntStream.range(1, 5).boxed().toList();

        // 从 left 中删除 right 的内容, 的到结果
        var results = Colls.removeAll(left, right);

        // 确认结果在 left 中且不在 right 中
        then(results).containsExactlyInAnyOrder(5, 6, 7, 8, 9);
    }

    /**
     * 测试 {@link Colls#intersect(Collection, Collection)} 方法
     */
    @Test
    void intersect_shouldGetSubsetFromTwoCollections() {
        // 产生一个 [1..5) 的集合
        var left = IntStream.range(1, 5)
            .mapToObj(n -> (Object) n)
            .toList();

        // 产生一个 [3..10) 的集合
        var right = IntStream.range(3, 10)
            .mapToObj(n -> (Object) n)
            .toList();

        // 获取两个集合的交集
        var results = Colls.intersect(left, right);

        // 确认交集为 [3, 4]
        then(results).containsExactlyInAnyOrder(3, 4);
    }

    /**
     * 测试 {@link Colls#diff(Collection, Collection)} 方法
     */
    @Test
    void diff_shouldGetDiffSetBetweenTwoCollections() {
        // 产生一个 [1..5) 的集合
        var left = (Collection<Integer>) IntStream.range(1, 5).boxed().toList();

        // 产生一个 [3..10) 的集合
        var right = IntStream.range(3, 10)
            .mapToObj(n -> (Object) n)
            .toList();

        // 获取两个集合的差集
        var result = Colls.diff(left, right);

        // 确认差集为 [1, 2, 5, 6, 7, 8, 9]
        then(result).containsExactlyInAnyOrder(1, 2, 5, 6, 7, 8, 9);
    }

    /**
     * 测试 {@link Colls#merge(boolean, Collection...)} 方法
     */
    @Test
    void merge_shouldMergeMultiCollections() {
        var rand = new Random();

        // 合并多个集合
        var results = Colls.merge(
            true,
            rand.ints(5, 1, 10).boxed().toList(),
            rand.ints(5, 1, 10).boxed().toList(),
            rand.ints(5, 1, 10).boxed().toList(),
            rand.ints(5, 1, 10).boxed().toList(),
            rand.ints(5, 1, 10).boxed().toList()
        );
        // 确认合并后的集合大小不会超过 10
        then(results).size().isLessThan(10);

        // 确认结果集合中的每个元素值
        for (var n : results) {
            then((int) n).isGreaterThanOrEqualTo(0).isLessThan(10);
        }
    }

    /**
     * 测试 {@link Colls#disorder(Collection, int)} 方法
     */
    @Test
    void disorder_shouldDisorderCollection() {
        // 产生一个整数集合
        var coll = IntStream.range(0, 10).boxed().toList();

        // 打乱集合内容
        var result = Colls.disorder(coll, 1000);

        // 确认结果和原集合内容一致, 但元素顺序不同
        then(result).doesNotContainSequence(coll);
        then(result).containsExactlyInAnyOrderElementsOf(coll);
    }

    /**
     * 测试 {@link Colls#merge(Map...)} 方法
     */
    @Test
    void merge_shouldMergeMultiMaps() {
        var maps = Colls.array(
            Map.of("A", 1, "B", 2),
            Map.of("C", 3),
            Map.of("D", 4, "E", 5));

        // 合并多个集合
        var results = Colls.merge(maps);

        // 确认合并后的 Map 集合内容
        then(results).hasSize(5).contains(
            entry("A", 1),
            entry("B", 2),
            entry("C", 3),
            entry("D", 4),
            entry("E", 5));
    }

    /**
     * 测试 {@link Colls#toList(Iterable, java.util.function.Function, boolean)
     * Colls.toList(Iterable, Function, boolean)}
     */
    @Test
    void toList_shouldConvertIterableToList() {
        var iter = (Iterable<Integer>) List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Colls.toList(iter, String::valueOf, false);
        then(results).containsExactly("1", "2", "3", "4");

        // 确认过滤 null 的结果
        results = Colls.toList(iter, CollsTest::filterOdd, true);
        then(results).containsExactly("1", "3");
    }

    /**
     * 测试
     * {@link Colls#toList(java.util.Iterator, java.util.function.Function, boolean)
     * Colls.toList(Iterator, Function, boolean)}
     */
    @Test
    void toList_shouldConvertIteratorToList() {
        var coll = List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Colls.toList(coll.iterator(), String::valueOf, false);
        then(results).containsExactly("1", "2", "3", "4");

        // 确认过滤 null 的结果
        results = Colls.toList(coll.iterator(), CollsTest::filterOdd, true);
        then(results).containsExactly("1", "3");
    }

    /**
     * 测试 {@link Colls#toSet(Iterable, java.util.function.Function, boolean)
     * Colls.toSet(Iterable, Function, boolean)}
     */
    @Test
    void toSet_shouldConvertIterableToSet() {
        var iter = (Iterable<Integer>) List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Colls.toList(iter, String::valueOf, false);
        then(results).containsExactly("1", "2", "3", "4");

        // 确认过滤 null 的结果
        results = Colls.toList(iter, CollsTest::filterOdd, true);
        then(results).containsExactly("1", "3");
    }

    /**
     * 测试
     * {@link Colls#toSet(java.util.Iterator, java.util.function.Function, boolean)
     * Colls.toSet(Iterator, Function, boolean)}
     */
    @Test
    void toSet_shouldConvertIteratorToSet() {
        var coll = List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Colls.toSet(coll.iterator(), String::valueOf, false);
        then(results).containsExactly("1", "2", "3", "4");

        // 确认过滤 null 的结果
        results = Colls.toSet(coll.iterator(), CollsTest::filterOdd, true);
        then(results).containsExactly("1", "3");
    }

    /**
     * 测试
     * {@link Colls#toMap(Iterable, java.util.function.Function, java.util.function.Function, boolean)
     * Colls.toMap(Iterable, Function, Function, boolean)}
     */
    @Test
    void toMap_shouldConvertIterableToMap() {
        var iter = (Iterable<Integer>) List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Colls.toMap(iter, String::valueOf, n -> String.format("%d:%d", n, n), false);
        then(results).containsExactly(
            entry("1", "1:1"),
            entry("2", "2:2"),
            entry("3", "3:3"),
            entry("4", "4:4"));

        // 确认过滤 null 的结果
        results = Colls.toMap(iter, CollsTest::filterOdd, n -> String.format("%d:%d", n, n), true);
        then(results).containsExactly(
            entry("1", "1:1"),
            entry("3", "3:3"));
    }

    /**
     * 测试
     * {@link Colls#toMap(Iterable, java.util.function.Function, boolean)
     * Colls.toMap(Iterable, Function, boolean)}
     */
    @Test
    void toMap_shouldConvertIterableToGroupedMap() {
        var iter = (Iterable<Integer>) List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Colls.toMap(iter, String::valueOf, false);
        then(results).containsExactly(
            entry("1", 1),
            entry("2", 2),
            entry("3", 3),
            entry("4", 4));

        // 确认过滤 null 的结果
        results = Colls.toMap(iter, CollsTest::filterOdd, true);
        then(results).containsExactly(
            entry("1", 1),
            entry("3", 3));
    }

    /**
     * 测试
     * {@link Colls#toMap(java.util.Iterator, java.util.function.Function, java.util.function.Function, boolean)
     * Colls.toMap(Iterator, Function, Function, boolean)}
     */
    @Test
    void toMap_shouldConvertIteratorToMap() {
        var coll = List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Colls.toMap(coll.iterator(), String::valueOf, n -> String.format("%d:%d", n, n), false);
        then(results).containsExactly(
            entry("1", "1:1"),
            entry("2", "2:2"),
            entry("3", "3:3"),
            entry("4", "4:4"));

        // 确认过滤 null 的结果
        results = Colls.toMap(coll.iterator(), CollsTest::filterOdd, n -> String.format("%d:%d", n, n), true);
        then(results).containsExactly(
            entry("1", "1:1"),
            entry("3", "3:3"));
    }

    /**
     * 测试
     * {@link Colls#toMap(java.util.Iterator, java.util.function.Function, boolean)
     * Colls.toMap(Iterator, Function, boolean)}
     */
    @Test
    void toMap_shouldConvertIteratorToGroupedMap() {
        var coll = List.of(1, 2, 3, 4);

        // 确认不过滤 null 的结果
        var results = Colls.toMap(coll.iterator(), String::valueOf, false);
        then(results).containsExactly(
            entry("1", 1),
            entry("2", 2),
            entry("3", 3),
            entry("4", 4));

        // 确认过滤 null 的结果
        results = Colls.toMap(coll.iterator(), CollsTest::filterOdd, true);
        then(results).containsExactly(
            entry("1", 1),
            entry("3", 3));
    }

    /**
     * 测试 {@link Colls#flatList(Iterable, boolean)} 方法
     */
    @Test
    void flatList_shouldConvertIterableToList() {
        var coll = List.of(
            Colls.list(1, 2, null),
            Colls.list(4, 5),
            Colls.list(null, 7, 8, 9));

        // 将一个嵌套集合的可迭代对象平铺, 确认平铺结果
        var results = Colls.flatList(coll, false);
        then(results).containsExactly(1, 2, null, 4, 5, null, 7, 8, 9);

        // 将一个嵌套集合的可迭代对象平铺, 并过滤掉 null 的部分, 确认平铺结果
        results = Colls.flatList(coll, true);
        then(results).containsExactly(1, 2, 4, 5, 7, 8, 9);
    }
}
