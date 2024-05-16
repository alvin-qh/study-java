package alvin.study.se.collection;

import com.google.common.collect.Iterators;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 集合操作工具类, 用于演示集合的常见操作
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Colls {
    // 一个临界值, 在此临界值以内通过迭代算法进行处理, 超过该临界值则选用其它算法
    private static final int MIN_COLLECTION_SIZE = 10;

    // 随机数对象
    private static final Random RANDOM = new Random();

    /**
     * 判断集合为空
     *
     * <p>
     * 如果一个引用为 {@code null} 或其引用的集合为空, 都返回 {@code true}
     * </p>
     *
     * @param coll 集合对象
     * @return {@code true} 表示 {@code coll} 参数的引用为 {@code null} 或其引用的集合为空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * 判断数组为空
     *
     * <p>
     * 如果一个引用为 {@code null} 或其引用的数组为空, 都返回 {@code true}
     * </p>
     *
     * @param <T>   数组元素类型
     * @param array 数组对象
     * @return {@code true} 表示 {@code array} 参数的引用为 {@code null} 或其引用的数组为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断字典为空
     *
     * <p>
     * 如果一个引用为 {@code null} 或其引用的字典为空, 都返回 {@code true}
     * </p>
     *
     * @param map 字典对象
     * @return {@code true} 表示 {@code map} 参数的引用为 {@code null} 或其引用的字典为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断集合不为空
     *
     * <p>
     * 如果一个引用不为 {@code null} 且其引用的集合不为空, 则返回 {@code true}
     * </p>
     *
     * @param coll 集合对象
     * @return {@code true} 表示 {@code coll} 参数的引用不为 {@code null} 且其引用的集合不为空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * 判断数组不为空
     *
     * <p>
     * 如果一个引用不为 {@code null} 且其引用的集合不为空, 则返回 {@code true}
     * </p>
     *
     * @param <T>   数组元素类型
     * @param array 数组对象
     * @return {@code true} 表示 {@code array} 参数的引用不为 {@code null} 且其引用的数组不为空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * 判断字典不为空
     *
     * <p>
     * 如果一个引用不为 {@code null} 且其引用的字典不为空, 则返回 {@code true}
     * </p>
     *
     * @param map 字典对象
     * @return {@code true} 表示 {@code map} 参数的引用不为 {@code null} 且其引用的字典不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 获取集的第一个元素
     *
     * @param <T>  集合元素类型
     * @param coll 集合对象
     * @return 如果集合不为空, 则返回其第一个元素的 {@link Optional} 包装对象, 否则返回
     * {@link Optional#empty()}
     */
    public static <T> Optional<T> first(Collection<? extends T> coll) {
        if (isEmpty(coll)) {
            return Optional.empty();
        }
        if (coll instanceof List<? extends T> lst) {
            return Optional.of(lst.get(0));
        }
        return Optional.of(coll.iterator().next());
    }

    /**
     * 获取集合第一个元素 ({@link List} 类型特例)
     *
     * @param <T> 集合元素类型
     * @param l   集合对象
     * @return 如果集合不为空, 则返回其第一个元素的 {@link Optional} 包装对象, 否则返回
     * {@link Optional#empty()}
     */
    public static <T> Optional<T> first(List<? extends T> l) {
        if (isEmpty(l)) {
            return Optional.empty();
        }
        return Optional.of(l.get(0));
    }

    /**
     * 当集合仅有一个元素时, 返回该元素
     *
     * @param <T>  集合元素类型
     * @param coll 集合对象
     * @return 如果集合只有唯一元素, 则返回该元素的 {@link Optional} 包装对象, 否则返回
     * {@link Optional#empty()}
     */
    public static <T> Optional<T> only(Collection<? extends T> coll) {
        if (coll == null || coll.size() != 1) {
            return Optional.empty();
        }
        if (coll instanceof List<? extends T> lst) {
            return Optional.of(lst.get(0));
        }
        return Optional.of(coll.iterator().next());
    }

    /**
     * 当集合仅有一个元素时, 返回该元素 ({@link List} 类型特例)
     *
     * @param <T> 集合元素类型
     * @param l   集合对象
     * @return 如果集合只有唯一元素, 则返回该元素的 {@link Optional} 包装对象, 否则返回
     * {@link Optional#empty()}
     */
    public static <T> Optional<T> only(List<? extends T> l) {
        if (l == null || l.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(l.get(0));
    }

    /**
     * 通过不定参数产生数组对象
     *
     * @param <T>  数组元素类型
     * @param elem 不定参数
     * @return {@code elem} 参数转为的数组对象
     */
    @SafeVarargs
    public static <T> T[] array(T... elem) {
        return elem;
    }

    /**
     * 通过不定参数产生 {@link List} 对象
     *
     * @param <T>  集合元素类型
     * @param elem 集合元素
     * @return {@code elem} 参数转为的 {@link List} 对象
     */
    @Contract("_ -> new")
    @SafeVarargs
    public static <T> @NotNull List<T> list(T... elem) {
        if (isEmpty(elem)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(elem));
    }

    /**
     * 将一个迭代器对象转化为 {@link List} 对象
     *
     * @param <T>  集合元素类型
     * @param iter 迭代器对象
     * @return {@code iter} 参数转为的 {@link List} 对象
     */
    public static <T> @NotNull List<T> list(Iterator<? extends T> iter) {
        var l = new ArrayList<T>();
        if (iter == null) {
            return l;
        }
        addAll(l, iter);
        return l;
    }

    /**
     * 将一个可迭代对象转换成 {@link List} 对象
     *
     * @param <T>      集合元素类型
     * @param iterable 可迭代集合对象
     * @return {@code iterable} 参数转为的 {@link List} 对象
     */
    public static <T> @NotNull List<T> list(Iterable<? extends T> iterable) {
        if (iterable instanceof Collection<? extends T> coll) {
            return new ArrayList<>(coll);
        }

        var l = new ArrayList<T>();
        addAll(l, iterable);
        return l;
    }

    /**
     * 将一个集合对象转化为 {@link List} 对象
     *
     * @param <T>  集合元素类型
     * @param coll 集合对象
     * @return {@code coll} 参数转为的 {@link List} 对象
     */
    @Contract("_ -> new")
    public static <T> @NotNull List<T> list(Collection<? extends T> coll) {
        if (isEmpty(coll)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(coll);
    }

    /**
     * 将一组不定参数转为 {@link Set} 对象
     *
     * @param <T>  集合元素类型
     * @param elem 不定参数
     * @return 参数 {@code elem} 转为的 {@link Set} 对象
     */
    @Contract("_ -> new")
    @SafeVarargs
    public static <T> @NotNull Set<T> set(T... elem) {
        if (isEmpty(elem)) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(Arrays.asList(elem));
    }

    /**
     * 将迭代器对象转化为 {@link Set} 对象
     *
     * @param <T>  集合元素类型
     * @param iter 迭代器对象
     * @return 参数 {@code iter} 转换而成的 {@link Set} 对象
     */
    public static <T> @NotNull Set<T> set(Iterator<? extends T> iter) {
        var set = new LinkedHashSet<T>();
        addAll(set, iter);
        return set;
    }

    /**
     * 将可迭代对象转化为 {@link Set} 对象
     *
     * @param <T>      集合元素类型
     * @param iterable 可迭代对象
     * @return 参数 {@code iterable} 转换而成的 {@link Set} 对象
     */
    public static <T> @NotNull Set<T> set(Iterable<? extends T> iterable) {
        if (iterable instanceof Collection<? extends T> coll) {
            return new LinkedHashSet<>(coll);
        }

        var set = new LinkedHashSet<T>();
        addAll(set, iterable);
        return set;
    }

    /**
     * 将集合对象转化为 {@link Set} 对象
     *
     * @param <T>  集合元素类型
     * @param coll 集合对象
     * @return 参数 {@code coll} 转换而成的 {@link Set} 对象
     */
    public static <T> @NotNull Set<T> set(Collection<? extends T> coll) {
        if (isEmpty(coll)) {
            return new LinkedHashSet<>();
        }
        return set((Iterable<? extends T>) coll);
    }

    /**
     * 将迭代器包含的元素依次添加到集合中
     *
     * @param <T>  集合元素类型
     * @param coll 集合对象
     * @param iter 迭代器对象
     * @return {@code coll} 集合是否被修改
     */
    public static <T> boolean addAll(Collection<? super T> coll, @NotNull Iterator<? extends T> iter) {
        boolean wasModified = false;
        while (iter.hasNext()) {
            wasModified |= coll.add(iter.next());
        }
        return wasModified;
    }

    /**
     * 将一个可迭代对象的内容依次添加到集合中
     *
     * @param <T>  集合元素类型
     * @param coll 集合对象
     * @param iter 可迭代对象
     * @return {@code coll} 集合是否被修改
     */
    public static <T> boolean addAll(Collection<? super T> coll, @NotNull Iterable<? extends T> iter) {
        boolean wasModified = false;
        for (var e : iter) {
            wasModified |= coll.add(e);
        }
        return wasModified;
    }

    /**
     * 通过不定参数向 {@link Set} 集合中添加元素
     *
     * <p>
     * 如果 {@code set} 参数为 {@code null}, 则会产生一个新的 {@link Set} 集合并添加元素
     * </p>
     *
     * @param <T>   元素类型
     * @param set   现有的 {@link Set} 集合
     * @param items 要追加的原始
     * @return 包含被添加元素的 {@link Set} 集合
     */
    @SafeVarargs
    public static <T> @NotNull Set<T> append(Set<? extends T> set, T... items) {
        var result = new LinkedHashSet<T>(set == null ? Set.of() : set);
        result.addAll(Arrays.asList(items));
        return result;
    }

    /**
     * 通过不定参数向集合中添加元素
     *
     * <p>
     * 如果 {@code coll} 参数为 {@code null}, 则会产生一个新的 {@link List} 集合并添加元素
     * </p>
     *
     * @param <T>   元素类型
     * @param coll  现有的集合对象
     * @param items 要追加的原始
     * @return 包含被添加元素的 {@link List} 集合
     */
    @SafeVarargs
    public static <T> @NotNull List<T> append(Collection<? extends T> coll, T... items) {
        var result = new ArrayList<T>(coll == null ? List.of() : coll);
        result.addAll(Arrays.asList(items));
        return result;
    }

    /**
     * 检查一个集合中是否包含了迭代器中的任意元素
     *
     * @param coll 集合对象
     * @param iter 迭代器对象
     * @return {@code true} 表示 {@code iter} 参数中至少一个元素包含在 {@code coll} 参数集合中
     */
    private static boolean anyInCollection(@NotNull Collection<?> coll, Iterator<?> iter) {
        if (coll.size() >= MIN_COLLECTION_SIZE) {
            coll = Set.copyOf(coll);
        }

        while (iter.hasNext()) {
            if (coll.contains(iter.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断集合中是否包含指定元素
     *
     * @param coll   集合对象
     * @param values 表示一组值的不定参数
     * @return {@code true} 表示 {@code coll} 参数包含 {@code values} 的任意一个
     */
    public static boolean containsAny(Collection<?> coll, Object... values) {
        if (isEmpty(coll) || isEmpty(values)) {
            return false;
        }
        return anyInCollection(coll, Iterators.forArray(values));
    }

    /**
     * 判断集合中是否包另一个集合中的任意元素
     *
     * @param left  原始集合
     * @param right 另一个集合
     * @return {@code true} 表示 {@code right} 集合中至少一个元素包含在 {@code left} 集合中
     */
    public static boolean containsAny(Collection<?> left, Collection<?> right) {
        if (isEmpty(left) || isEmpty(right)) {
            return false;
        }

        if (left == right) {
            return true;
        }
        return anyInCollection(left, right.iterator());
    }

    /**
     * 检查一个集合中是否包含了迭代器中的全部元素
     *
     * @param coll 集合对象
     * @param iter 迭代器对象
     * @return {@code true} 表示 {@code iter} 参数中的全部元素都包含在 {@code coll} 参数集合中
     */
    private static boolean allInCollection(@NotNull Collection<?> coll, Iterator<?> iter) {
        if (coll.size() >= MIN_COLLECTION_SIZE) {
            coll = Set.copyOf(coll);
        }

        while (iter.hasNext()) {
            if (!coll.contains(iter.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一个集合是否完全包含所给的元素
     *
     * @param coll   集合对象
     * @param values 不定参数定义的元素值
     * @return {@code coll} 集合中包含了 {@code values} 的全部值
     */
    public static boolean containsAll(Collection<?> coll, Object... values) {
        if (isEmpty(coll) || values.length == 0) {
            return false;
        }
        return allInCollection(coll, Iterators.forArray(values));
    }

    /**
     * 判断一个集合是否完全包含另一个集合的全部元素
     *
     * @param left  原始集合
     * @param right 另一个集合
     * @return {@code left} 集合中包含了 {@code right} 的全部元素
     */
    public static boolean containsAll(Collection<?> left, Collection<?> right) {
        if (isEmpty(left) || isEmpty(right)) {
            return false;
        }
        if (left == right) {
            return true;
        }
        return allInCollection(left, right.iterator());
    }

    /**
     * 得到一个在 {@code first} 集合但不在 {@code second} 集合中的结果集合
     *
     * @param first  第一个集合
     * @param second 第二个集合
     * @return 在 {@code left} 集合但不在 {@code right} 集合中的结果集合
     */
    public static <T> Collection<T> removeAll(Collection<? extends T> first, Collection<? extends T> second) {
        if (first == second || isEmpty(first)) {
            return List.of();
        }

        if (isEmpty(second)) {
            return Collections.unmodifiableCollection(first);
        }

        var set = new HashSet<T>(first);
        set.removeAll(second);
        return set;
    }

    /**
     * 获取两个集合中相交的元素子集
     *
     * @param left  第一个集合
     * @param right 第二个集合
     * @return 包含 {@code left} 和 {@code right} 中相同元素的子集
     */
    public static <T> Collection<T> intersect(Collection<? extends T> left, Collection<? extends T> right) {
        if (isEmpty(left) || isEmpty(right)) {
            return Set.of();
        }

        if (left == right) {
            return Collections.unmodifiableCollection(left);
        }

        var set = new HashSet<T>(left);
        set.retainAll(right);
        return set;
    }

    /**
     * 获取两个集合的差集
     *
     * @param left  第一个集合
     * @param right 第二个集合
     * @return 在一个集合且不在另一个集合的元素集合
     */
    public static <T> Collection<T> diff(Collection<? extends T> left, Collection<? extends T> right) {
        if (left == right) {
            return Set.of();
        }

        if (isEmpty(left)) {
            if (isEmpty(right)) {
                return Set.of();
            }
            return Collections.unmodifiableCollection(right);
        }

        if (isEmpty(right)) {
            return Collections.unmodifiableCollection(left);
        }

        return merge(true, removeAll(left, right), removeAll(right, left));
    }

    /**
     * 合并多个集合, 得到的集合不包含重复元素
     *
     * @param colls 多个集合
     * @return 由 {@code colls} 参数中各个集合组成的结果
     */
    @SafeVarargs
    public static <T> Collection<T> merge(boolean unique, Collection<? extends T>... colls) {
        if (isEmpty(colls)) {
            return List.of();
        }

        var result = unique ? new HashSet<T>() : new ArrayList<T>();
        for (var coll : colls) {
            result.addAll(coll);
        }
        return result;
    }

    /**
     * 组合两个 {@link Map} 对象
     *
     * @param <K>  {@link Map} 的 {@code Key} 类型
     * @param <V>  {@link Map} 的 {@code Value} 类型
     * @param maps 一组 {@link Map} 对象
     * @return {@code maps} 参数合并后的对象
     */
    @SafeVarargs
    public static <K, V> Map<K, V> merge(Map<? extends K, ? extends V>... maps) {
        if (isEmpty(maps)) {
            return Map.of();
        }
        var result = new LinkedHashMap<K, V>();
        for (var map : maps) {
            if (map != null) {
                result.putAll(map);
            }
        }
        return result;
    }

    /**
     * 打乱集合的顺序并返回为 {@link List} 对象
     *
     * @param <T>       集合元素类型
     * @param coll      集合对象
     * @param frequency 打乱次数
     * @return 打乱顺序后的 {@link List} 集合
     */
    public static <T> List<T> disorder(Collection<? extends T> coll, int frequency) {
        if (isEmpty(coll)) {
            return List.of();
        }

        var list = new ArrayList<T>(coll);
        for (var i = 0; i < frequency; i++) {
            // 生成两个集合长度范围内的随机数表示元素位置
            var a = RANDOM.nextInt(list.size());
            var b = RANDOM.nextInt(list.size());
            if (a != b) {
                // 交换两个随机位置的元素
                T v = list.get(a);
                list.set(a, list.get(b));
                list.set(b, v);
            }
        }
        return list;
    }

    /**
     * 将可迭代对象转换成 {@link List} 对象
     *
     * @param <T>      {@code iterable} 参数的的元素类型
     * @param <R>      返回集合的元素类型
     * @param iterable 可迭代对象
     * @param mapper   对象转换函数
     * @param omitNull 是否过滤 {@code null} 元素
     * @return {@code iterable} 转换得到的 {@link List} 对象
     */
    public static <T, R> List<R> toList(
        Iterable<? extends T> iterable, Function<? super T, ? extends R> mapper, boolean omitNull) {
        return Streams.toList(Streams.stream(iterable), mapper, omitNull);
    }

    /**
     * 将迭代器对象转换成 {@link List} 对象
     *
     * @param <T>      {@code iter} 参数的的元素类型
     * @param <R>      返回集合的元素类型
     * @param iter     迭代器对象
     * @param mapper   对象转换函数
     * @param omitNull 是否过滤 {@code null} 元素
     * @return {@code iter} 转换得到的 {@link List} 对象
     */
    public static <T, R> List<R> toList(
        Iterator<? extends T> iter, Function<? super T, ? extends R> mapper, boolean omitNull) {
        return Streams.toList(Streams.stream(iter), mapper, omitNull);
    }

    /**
     * 将可迭代对象转换成 {@link Set} 对象
     *
     * @param <T>      {@code iterable} 参数的的元素类型
     * @param <R>      返回集合的元素类型
     * @param iterable 可迭代对象
     * @param mapper   对象转换函数
     * @param omitNull 是否过滤 {@code null} 元素
     * @return {@code iterable} 转换得到的 {@link Set} 对象
     */
    public static <T, R> Set<R> toSet(
        Iterable<? extends T> iterable,
        Function<? super T, ? extends R> mapper,
        boolean omitNull) {
        return Streams.toSet(Streams.stream(iterable), mapper, omitNull);
    }

    /**
     * 将迭代器对象转换成 {@link Set} 对象
     *
     * @param <T>      {@code iter} 参数的的元素类型
     * @param <R>      返回集合的元素类型
     * @param iter     迭代器对象
     * @param mapper   对象转换函数
     * @param omitNull 是否过滤 {@code null} 元素
     * @return {@code iterable} 转换得到的 {@link Set} 对象
     */
    public static <T, R> Set<R> toSet(
        Iterator<? extends T> iter,
        Function<? super T, ? extends R> mapper,
        boolean omitNull) {
        return Streams.toSet(Streams.stream(iter), mapper, omitNull);
    }

    /**
     * 将可迭代对象转化为 {@link Map} 对象
     *
     * @param <K>         要返回 {@link Map} 的 {@code Key} 值类型
     * @param <V>         要返回 {@link Map} 的 {@code Value} 值类型
     * @param <U>         参数 {@code iterable} 的元素类型
     * @param iterable    {@link Iterable} 对象, 包含集合元素
     * @param keyMapper   将集合元素转为 {@code Key} 值的函数对象
     * @param valueMapper 将集合元素转为 {@code Value} 值的函数对象
     * @param omitKeyNull 是否过滤掉为 {@code null} 的 {@code Key} 值
     * @return {@link Map} 对象
     */
    public static <K, V, U> Map<K, V> toMap(
        Iterable<? extends U> iterable,
        Function<? super U, ? extends K> keyMapper,
        Function<? super U, ? extends V> valueMapper,
        boolean omitKeyNull) {
        return Streams.toMap(Streams.stream(iterable), keyMapper, valueMapper, omitKeyNull);
    }

    /**
     * 给可迭代对象进行分组, 转化为 {@link Map} 对象
     *
     * @param <K>         要返回 {@link Map} 的 {@code Key} 值类型
     * @param <V>         要返回 {@link Map} 的 {@code Value} 值类型, 同时也是 {@code iterable}
     *                    参数的元素类型
     * @param iterable    {@link Iterable} 对象, 包含集合元素
     * @param keyMapper   将集合元素转为 {@code Key} 值的函数对象
     * @param omitKeyNull 是否过滤掉为 {@code null} 的 {@code Key} 值
     * @return {@link Map} 对象
     */
    public static <K, V> Map<K, V> toMap(
        Iterable<? extends V> iterable,
        Function<? super V, ? extends K> keyMapper,
        boolean omitKeyNull) {
        return toMap(iterable, keyMapper, Function.identity(), omitKeyNull);
    }

    /**
     * 将迭代器对象转化为 {@link Map} 对象
     *
     * @param <K>         要返回 {@link Map} 的 {@code Key} 值类型
     * @param <V>         要返回 {@link Map} 的 {@code Value} 值类型
     * @param <U>         参数 {@code iter} 的元素类型
     * @param iter        {@link Iterator} 对象, 包含集合元素
     * @param keyMapper   将集合元素转为 {@code Key} 值的函数对象
     * @param valueMapper 将集合元素转为 {@code Value} 值的函数对象
     * @param omitKeyNull 是否过滤掉为 {@code null} 的 {@code Key} 值
     * @return {@link Map} 对象
     */
    public static <K, V, U> Map<K, V> toMap(
        Iterator<? extends U> iter,
        Function<? super U, ? extends K> keyMapper,
        Function<? super U, ? extends V> valueMapper,
        boolean omitKeyNull) {
        return Streams.toMap(Streams.stream(iter), keyMapper, valueMapper, omitKeyNull);
    }

    /**
     * 给迭代器对象进行分组, 转化为 {@link Map} 对象
     *
     * @param <K>         要返回 {@link Map} 的 {@code Key} 值类型
     * @param <V>         要返回 {@link Map} 的 {@code Value} 值类型, 同时也是 {@code iter}
     *                    参数的元素类型
     * @param iter        {@link Iterator} 对象, 包含集合元素
     * @param keyMapper   将集合元素转为 {@code Key} 值的函数对象
     * @param omitKeyNull 是否过滤掉为 {@code null} 的 {@code Key} 值
     * @return {@link Map} 对象
     */
    public static <K, V> Map<K, V> toMap(
        Iterator<? extends V> iter,
        Function<? super V, ? extends K> keyMapper,
        boolean omitKeyNull) {
        return toMap(iter, keyMapper, Function.identity(), omitKeyNull);
    }

    /**
     * 将迭代器对象平铺为 {@link List} 对象
     *
     * @param <T>      {@code iter} 参数的元素类型
     * @param <R>      返回结果的元素类型
     * @param iter     迭代器对象
     * @param mapper   类型转换函数对象
     * @param omitNull 是否规律结果中为 {@code null} 的元素项
     * @return 转换后的 {@link List} 对象
     */
    public static <T, R> List<T> flatList(
        Iterator<? extends R> iter, Function<? super R, Stream<? extends T>> mapper, boolean omitNull) {
        return Streams.flatList(Streams.stream(iter), mapper, omitNull);
    }

    /**
     * 将迭代器对象平铺为 {@link List} 对象
     *
     * @param <T>      {@code iterable} 参数的元素类型
     * @param iterable 可迭代对象
     * @param omitNull 是否规律结果中为 {@code null} 的元素项
     * @return 转换后的 {@link List} 对象
     */
    public static <T> List<T> flatList(Iterable<? extends Iterable<T>> iterable, boolean omitNull) {
        var rs = Streams.stream(iterable).flatMap(Streams::stream);
        if (omitNull) {
            rs = rs.filter(Objects::nonNull);
        }
        return rs.toList();
    }

    /**
     * 将可迭代对象平铺为 {@link Set} 对象
     *
     * @param <T>      {@code iterable} 参数的元素类型
     * @param <R>      返回结果的元素类型
     * @param iterable 可迭代对象
     * @param mapper   类型转换函数对象
     * @param omitNull 是否规律结果中为 {@code null} 的元素项
     * @return 转换后的 {@link Set} 对象
     */
    public static <T, R> Set<T> flatSet(
        Iterable<R> iterable, Function<? super R, Stream<? extends T>> mapper, boolean omitNull) {
        return Streams.flatSet(Streams.stream(iterable), mapper, omitNull);
    }

    /**
     * 将迭代器对象平铺为 Set 对象
     *
     * @param <T>      {@code iter} 参数的元素类型
     * @param <R>      返回结果的元素类型
     * @param iter     迭代器对象
     * @param mapper   类型转换函数对象
     * @param omitNull 是否规律结果中为 {@code null} 的元素项
     * @return 转换后的 {@link Set} 对象
     */
    public static <T, R> Set<T> flatSet(
        Iterator<R> iter, Function<? super R, Stream<? extends T>> mapper, boolean omitNull) {
        return Streams.flatSet(Streams.stream(iter), mapper, omitNull);
    }
}
