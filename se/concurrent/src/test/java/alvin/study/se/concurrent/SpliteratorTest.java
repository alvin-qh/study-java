package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterator.OfDouble;
import java.util.Spliterator.OfInt;
import java.util.Spliterator.OfLong;
import java.util.Spliterators;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 测试 {@link Spliterator} 对象, 即可分割迭代器对象
 *
 * <p>
 * Splittable Iterator, 是 Java 为并发应用程序设计的特殊迭代器, 可以将一个集合 (或 {@link Stream})
 * 分割为多份, 在不同的线程中进行处理
 * </p>
 *
 * <p>
 * 通过 {@link java.util.Collection#spliterator()} 方法或
 * {@link Stream#spliterator()} 方法可以从一个集合或 {@link Stream}
 * 对象中获取 {@link Spliterator} 对象
 * </p>
 *
 * <p>
 * 和 {@link java.util.Iterator Iterator} 类似, {@link Spliterator} 对象也支持"快速失败"特性,
 * 即在使用
 * {@link Spliterator#tryAdvance(Consumer)} 方法或
 * {@link Spliterator#forEachRemaining(Consumer)} 方法时,
 * 如果同时修改了集合本身, 则会抛出 {@link java.util.ConcurrentModificationException
 * ConcurrentModificationException} 异常.
 * 本身支持并发访问的集合除外
 * </p>
 *
 * <p>
 * 所以 {@link Spliterator} 对象不是线程安全的, 无法在多个线程之间使用, 但通过
 * {@link Spliterator#trySplit()} 方法分隔为多个
 * {@link Spliterator} 对象后, 每个线程可以独立访问一个 {@link Spliterator} 对象
 * </p>
 *
 * <p>
 * 另外, 大多数情况下, {@link Spliterator} 是延迟绑定 (<i>late-binding</i>), 即在通过
 * {@link Spliterator} 对象访问,
 * 遍历或拆分集合元素时, 才会真实的绑定数据源 (集合) 对象. 除非 {@link Spliterator} 对象是通过 {@code Iterator}
 * 对象产生的
 * </p>
 */
class SpliteratorTest {
    /**
     * 将多个 {@link Spliterator} 对象合并到一个 {@link List} 集合对象中
     *
     * <p>
     * 通过 {@link StreamSupport#stream(Spliterator, boolean)} 方法可以将一个
     * {@link Spliterator} 对象转为
     * {@link Stream} 类型对象, 其中包含了 {@link Spliterator} 对象中剩余的元素
     * </p>
     *
     * <p>
     * {@link StreamSupport#stream(Spliterator, boolean)} 方法和
     * {@link Stream#spliterator()} 方法相互为逆操作
     * </p>
     *
     * @param <T>          集合元素类型
     * @param spliterators 多个 {@link Spliterator} 对象
     * @return 包含所有 {@link Spliterator} 对象元素的 {@link List} 集合对象
     */
    @SafeVarargs
    private static <T> List<T> toList(Spliterator<T>... spliterators) {
        var result = new ArrayList<T>();
        for (var sp : spliterators) {
            sp.forEachRemaining(result::add);
        }
        return result;
    }

    /**
     * 获取 {@link Spliterator} 类型对象的特性
     *
     * <p>
     * 通过 {@link Spliterator#hasCharacteristics(int)} 可以判断是否具备指定的特性; 而通过
     * {@link Spliterator#characteristics()}
     * 方法可以获取全部的特性
     * </p>
     *
     * <p>
     * {@link Spliterator} 有 {@code 8} 个特征, 包括:
     * <ul>
     * <li>{@link Spliterator#DISTINCT}, 值为 {@code 0x00000001}, 表示元素不重复</li>
     * <li>{@link Spliterator#SORTED}, 值为 {@code 0x00000004}, 表示元素是按一定规律进行排列
     * (有指定比较器)</li>
     * <li>{@link Spliterator#ORDERED}, 值为 {@code 0x00000010}, 表示元素是有序的
     * (每一次遍历结果相同)</li>
     * <li>{@link Spliterator#SIZED}, 值为 {@code 0x00000040}, 表示大小是固定的</li>
     * <li>{@link Spliterator#NONNULL}, 值为 {@code 0x00000100}, 表示没有 {@code null}
     * 元素</li>
     * <li>{@link Spliterator#IMMUTABLE}, 值为 {@code 0x00000400}, 表示元素不可变</li>
     * <li>{@link Spliterator#CONCURRENT}, 值为 {@code 0x00001000}, 表示迭代器可以多线程操作</li>
     * <li>{@link Spliterator#SUBSIZED}, 值为 {@code 0x00004000}, 表示子
     * {@link Spliterator} 都具有 {@code SIZED} 特性</li>
     * </ul>
     * </p>
     */
    @Test
    void characteristics_shouldGetCharacteristicsOfSpliterator() {
        // 测试基于 List 的 Spliterator 对象的特性
        var listSp = IntStream.range(0, 10).boxed().toList().spliterator();
        then(listSp.hasCharacteristics(Spliterator.SIZED)).isTrue();
        then(listSp.hasCharacteristics(Spliterator.ORDERED)).isTrue();
        then(listSp.hasCharacteristics(Spliterator.SUBSIZED)).isTrue();
        then(listSp.hasCharacteristics(Spliterator.NONNULL)).isFalse();
        then(listSp.hasCharacteristics(Spliterator.DISTINCT)).isFalse();
        then(listSp.hasCharacteristics(Spliterator.IMMUTABLE)).isFalse();
        then(listSp.hasCharacteristics(Spliterator.SORTED)).isFalse();
        // 确认该 Spliterator 对象包含的所有特性
        then(listSp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.ORDERED | Spliterator.SUBSIZED);

        // 测试基于 Set 的 Spliterator 对象的特性
        var setSp = IntStream.range(0, 10).boxed().collect(Collectors.toSet()).spliterator();
        then(setSp.hasCharacteristics(Spliterator.SIZED)).isTrue();
        then(setSp.hasCharacteristics(Spliterator.DISTINCT)).isTrue();
        then(setSp.hasCharacteristics(Spliterator.ORDERED)).isFalse();
        then(setSp.hasCharacteristics(Spliterator.SUBSIZED)).isFalse();
        then(setSp.hasCharacteristics(Spliterator.NONNULL)).isFalse();
        then(setSp.hasCharacteristics(Spliterator.IMMUTABLE)).isFalse();
        then(setSp.hasCharacteristics(Spliterator.SORTED)).isFalse();
        // 确认该 Spliterator 对象包含的所有特性
        then(setSp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.DISTINCT);
    }

    /**
     * 获取 {@link Spliterator} 对象中待处理的元素个数
     *
     * <p>
     * 通过 {@link Spliterator#estimateSize()} 方法可以获取对象中待处理的元素个数, 如果返回
     * {@link Long#MAX_VALUE} 表示该
     * {@link Spliterator} 对象的剩余元素数量未知
     * </p>
     *
     * <p>
     * 要求 {@link Spliterator#characteristics()} 结果中包含 {@link Spliterator#SIZED} 特性,
     * 否则返回 {@link Long#MAX_VALUE}
     * </p>
     *
     * <p>
     * 另外, 本例中通过 {@link Spliterator#trySplit()} 方法对一个 {@link Spliterator} 对象进行分割,
     * 分割后各个 {@link Spliterator} 的元素个数是分割前的 {@code 1/2}
     * </p>
     */
    @Test
    void estimateSize_shouldGetEstimateSize() {
        // 通过 List 对象创建 Spliterator 对象
        var part1 = IntStream.range(0, 10).boxed().toList().spliterator();
        // 确认可以获取元素个数
        then(part1.hasCharacteristics(Spliterator.SIZED)).isTrue();
        // 确认元素个数
        then(part1.estimateSize()).isEqualTo(10);

        // 将 Spliterator 对象分割为两部分
        var part2 = part1.trySplit();
        // 确认可以获取元素个数
        then(part2.hasCharacteristics(Spliterator.SIZED)).isTrue();
        // 确认每部分元素个数为分割前元素个数的 1/2
        then(part1.estimateSize()).isEqualTo(5);
        then(part2.estimateSize()).isEqualTo(5);
    }

    /**
     * 获取 {@link Spliterator} 对象中元素剩余元素的确切值
     *
     * <p>
     * 可通过 {@link Spliterator#getExactSizeIfKnown()} 方法获取 {@link Spliterator}
     * 对象中剩余元素的个数
     * </p>
     *
     * <p>
     * 默认实现下, 如果 {@link Spliterator#characteristics()} 方法的结果中包含
     * {@link Spliterator#SIZED} 特性, 则返回
     * 和 {@link Spliterator#estimateSize()} 方法一致的结果, 否则返回 {@code -1} 表示
     * {@link Spliterator} 中包含元素个数未知
     * </p>
     */
    @Test
    void getExactSizeIfKnown_shouldGetExactSizeIfKnown() {
        // 通过 List 对象创建 Spliterator 对象
        var sp = IntStream.range(0, 10).boxed().toList().spliterator();

        // 确认 getExactSizeIfKnown 方法的结果和 estimateSize 一致
        then(sp.getExactSizeIfKnown()).isEqualTo(sp.estimateSize()).isEqualTo(10);

        // 处理掉一个元素
        sp.tryAdvance(n -> {});
        // 确认 getExactSizeIfKnown 方法的结果和 estimateSize 一致
        then(sp.getExactSizeIfKnown()).isEqualTo(sp.estimateSize()).isEqualTo(9);
    }

    /**
     * 访问 {@link Spliterator} 对象中的元素
     *
     * <p>
     * 通过 {@link Spliterator#tryAdvance(java.util.function.Consumer)
     * Spliterator.tryAdvance(Consumer)}
     * 方法可以将 {@link Spliterator} 对象中的下一个元素作为参数传递给指定的 {@code Consumer} 对象, 以对该元素进行访问
     * </p>
     *
     * <p>
     * {@code tryAdvance} 一次处理一个元素, 并返回 {@code true} 表示本次有元素被处理, 或者 {@code false} 表示
     * {@link Spliterator}
     * 对象中已经不包含剩余元素
     * </p>
     */
    @Test
    void tryAdvance_shouldProcessAnElement() {
        // 通过 List 对象创建 Spliterator 对象
        var sp = IntStream.range(0, 10).boxed().toList().spliterator();

        // 保存结果的集合对象
        var results = new ArrayList<Integer>();

        // 获取剩余元素的数量
        var size = sp.estimateSize();

        // 逐个处理剩余元素, 直到无剩余元素
        while (sp.tryAdvance(results::add)) {
            // 确认每完成一次处理后, 剩余元素数量减一
            then(sp.estimateSize()).isEqualTo(--size);
        }

        // 确认元素处理结果
        then(results).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    /**
     * 遍历 {@link Spliterator} 对象中的元素
     *
     * <p>
     * 通过 {@link Spliterator#forEachRemaining(java.util.function.Consumer)
     * Spliterator.forEachRemaining(Consumer)}
     * 方法可以将 {@link Spliterator} 对象中的剩余元素逐一传递给指定的 {@code Consumer} 对象,
     * 从而达到对剩余元素遍历的效果
     * </p>
     *
     * <p>
     * {@code forEachRemaining} 方法相当于重复执行 {@link Spliterator#tryAdvance(Consumer)}
     * 方法直到返回 {@code false}
     * </p>
     */
    @Test
    void forEachRemaining_shouldTraversingAllRemainedElements() {
        // 通过 List 对象创建 Spliterator 对象
        var sp = IntStream.range(0, 10).boxed().toList().spliterator();

        // 保存结果的集合对象
        var results = new ArrayList<Integer>();

        // 对剩余元素进行遍历
        sp.forEachRemaining(results::add);

        // 确认遍历完成后无剩余元素
        then(sp.estimateSize()).isEqualTo(0);

        // 确认元素遍历结果
        then(results).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    /**
     * 将一个 {@link Spliterator} 对象进行分割
     *
     * <p>
     * 通过 {@link Spliterator#trySplit()} 方法对指定的 {@link Spliterator} 对象进行分割,
     * 分割后得到一个新的 {@link Spliterator}
     * 对象, 该对象包含原 {@link Spliterator} 对象的一半元素, 原 {@link Spliterator} 也剩余一半元素
     * </p>
     */
    @Test
    void trySplit_shouldSplitCollectionIntoSlice() {
        // 通过 List 对象创建 Spliterator 对象
        var part1 = IntStream.range(0, 10).boxed().toList().spliterator();
        // 确认未分割的 Spliterator 对象剩余元素数为集合全部元素
        then(part1.estimateSize()).isEqualTo(10);

        // 将 Spliterator 对象分割为两部分
        var part2 = part1.trySplit();
        // 确认分割后各 Spliterator 对象包含原 1/2 个元素
        then(part1.estimateSize()).isEqualTo(5);
        then(part2.estimateSize()).isEqualTo(5);

        // 确认分割后的两部分合并在一起为原本集合的全部元素
        then(toList(part1, part2)).containsExactlyInAnyOrder(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    /**
     * 通过并行方式计算 {@code 1000} 以内的所有质数
     *
     * <p>
     * 本例演示了如何通过对 {@link Spliterator} 对象进行递归分割, 直到元素数符合要求后, 送入线程池进行计算
     * </p>
     */
    @Test
    void parallel_shouldUseSpliteratorInThreadPool() {
        // 建立线程池对象
        var processorCount = Runtime.getRuntime().availableProcessors();
        try (var executor = new ThreadPoolExecutor(
            processorCount,
            processorCount,
            0,
            TimeUnit.NANOSECONDS,
            new ArrayBlockingQueue<>(260))) {

            // 设置要计算的数据
            var data = IntStream.range(3, 1000).boxed().toList();

            // 创建用于迭代的队列对象
            var que = new ArrayDeque<Spliterator<Integer>>();
            que.offer(data.spliterator());

            // 保存异步计算结果的集合
            var tasks = new ArrayList<Future<List<Integer>>>();

            // 通过队列进行任务分配
            while (!que.isEmpty()) {
                // 从队列中获取一个 Spliterator 对象
                var sp = que.poll();
                // 如果该 Spliterator 对象包含的元素数量大于 5, 则分割为两部分放入队列继续迭代
                if (sp.estimateSize() > 5) {
                    then(que.offer(sp.trySplit())).isTrue();
                    then(que.offer(sp)).isTrue();
                } else {
                    // 如果 Spliterator 对象包含元素在 5 个以内, 则计算其中包含的质数
                    tasks.add(
                        // 向线程池提交计算任务
                        executor.submit(() -> StreamSupport.stream(sp, false).filter(n -> {
                            // 将 Spliterator 转为 Stream 后, 对元素进行过滤, 留下质数
                            for (var i = 2; i <= Math.sqrt(n); i++) {
                                if (n % i == 0) {
                                    return false;
                                }
                            }
                            return true;
                        }).toList()));
                }
            }

            // 确认共产生了 256 个子任务, 并行执行
            then(tasks).hasSize(256);

            // 等待所有的任务都结束
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> then(tasks).allMatch(Future::isDone));

            // 将结果合并到一个集合中
            var result = tasks.stream().flatMap(t -> {
                try {
                    return t.get().stream();
                } catch (InterruptedException | ExecutionException e) {
                    return Stream.empty();
                }
            }).sorted().toList();

            // 确认计算结果
            then(result).containsExactly(
                3, 5, 7,
                11, 13, 17, 19,
                23, 29,
                31, 37,
                41, 43, 47,
                53, 59,
                61, 67,
                71, 73, 79,
                83, 89,
                97,
                101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197,
                199,
                211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293,
                307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397,
                401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499,
                503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599,
                601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691,
                701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797,
                809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887,
                907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997);

        }
    }

    /**
     * 针对于简单类型的 {@link Spliterator}
     *
     * <p>
     * 类似于 {@code Iterator}, 自 JDK 1.8 之后, 也提供了针对于简单类型的 {@link Spliterator} 类型对象
     * <ul>
     * <li>
     * {@link OfInt} 类型, 通过 {@link OfInt#tryAdvance(IntConsumer)} 方法对
     * {@link Spliterator} 中的下一个 {@code int}
     * 值进行处理; 通过 {@link OfInt#forEachRemaining(IntConsumer)} 方法对 {@link Spliterator}
     * 中的剩余 {@code int} 值进行遍历
     * </li>
     * <li>
     * {@link OfLong} 类型, 通过
     * {@link OfLong#tryAdvance(java.util.function.LongConsumer)
     * OfLong.tryAdvance(LongConsumer)}
     * 方法对 {@link Spliterator} 中的下一个 {@code long} 值进行处理, 通过
     * {@link OfLong#forEachRemaining(java.util.function.LongConsumer)
     * OfLong.forEachRemaining(LongConsumer)} 方法对
     * {@link Spliterator} 中的剩余 {@code long} 值进行遍历
     * </li>
     * <li>
     * {@link OfDouble} 类型, 通过
     * {@link OfDouble#tryAdvance(java.util.function.DoubleConsumer)
     * OfDouble.tryAdvance(DoubleConsumer)} 方法对 {@link Spliterator} 中的下一个
     * {@code double} 值进行处理, 通过
     * {@link OfDouble#forEachRemaining(java.util.function.DoubleConsumer)
     * OfDouble.forEachRemaining(DoubleConsumer)}
     * 方法对 {@link Spliterator} 中的剩余 {@code double} 值进行遍历
     * </li>
     * </ul>
     * 使用这些针对于简单类型的 {@link Spliterator}, 目的是减少在运算过程中频繁的装箱和拆箱操作, 对于大量的简单类型数据,
     * 可以用类似方法进行处理
     * </p>
     */
    @Test
    void primitive_shouldCreatePrimitiveSpliterator() {
        var sp = Arrays.stream(new int[] { 1, 2, 3 }).spliterator();

        sp.tryAdvance((IntConsumer) n -> then(n).isEqualTo(1));
        sp.forEachRemaining((IntConsumer) n -> then(n).isIn(2, 3));
    }

    /**
     * 测试 {@link Spliterators} 工具类
     *
     * <p>
     * {@link Spliterators} 工具类提供了一系列方法, 可以在一些特殊场合更为方便的产生 {@link Spliterator} 对象
     * </p>
     */
    @Nested
    class SpliteratorsTest {
        /**
         * 产生不包含任何元素的空 {@link Spliterator} 对象
         *
         * <p>
         * 通过 {@link Spliterators#emptySpliterator()} 方法可以创建一个空 {@link Spliterator} 对象,
         * 即:
         * <ul>
         * <li>
         * {@link Spliterator#estimateSize()} 和
         * {@link Spliterator#getExactSizeIfKnown()} 方法返回 {@code 0}
         * </li>
         * <li>
         * {@link Spliterator#tryAdvance(Consumer)} 和
         * {@link Spliterator#forEachRemaining(Consumer)} 方法不会执行回调
         * </li>
         * <li>
         * {@link Spliterator#trySplit()} 方法返回 {@code null}, 表示无法被再次分割
         * </li>
         * <li>
         * 空 {@link Spliterator} 对象仅具备 {@link Spliterator#SIZED} 和
         * {@link Spliterator#SUBSIZED} 特性
         * </li>
         * </ul>
         * </p>
         *
         * <p>
         * 另外, 通过 {@link Spliterators#emptyIntSpliterator()},
         * {@link Spliterators#emptyLongSpliterator()} 和
         * {@link Spliterators#emptyDoubleSpliterator()} 方法可以获取到简单类型的"空"
         * {@link Spliterator} 对象
         * </p>
         */
        @Test
        void emptySpliterator_shouldCreateSpliterator() {
            // 测试泛型的空 Spliterator 对象
            {
                var sp = Spliterators.<String>emptySpliterator();

                then(sp.estimateSize()).isEqualTo(0);
                then(sp.getExactSizeIfKnown()).isEqualTo(0);

                then(sp.tryAdvance(n -> fail())).isFalse();
                sp.forEachRemaining(n -> fail());

                then(sp.trySplit()).isNull();
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED);
            }

            // 测试 int 类型的空 Spliterator 对象
            {
                var sp = Spliterators.emptyIntSpliterator();

                then(sp.estimateSize()).isEqualTo(0);

                then(sp.estimateSize()).isEqualTo(0);
                then(sp.getExactSizeIfKnown()).isEqualTo(0);

                then(sp.tryAdvance((IntConsumer) n -> fail())).isFalse();
                sp.forEachRemaining((IntConsumer) n -> fail());

                then(sp.trySplit()).isNull();
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED);
            }

            // 测试 long 类型的空 Spliterator 对象
            {
                var sp = Spliterators.emptyLongSpliterator();

                then(sp.estimateSize()).isEqualTo(0);

                then(sp.estimateSize()).isEqualTo(0);
                then(sp.getExactSizeIfKnown()).isEqualTo(0);

                then(sp.tryAdvance((LongConsumer) n -> fail())).isFalse();
                sp.forEachRemaining((LongConsumer) n -> fail());

                then(sp.trySplit()).isNull();
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED);
            }

            // 测试 double 类型的空 Spliterator 对象
            {
                var sp = Spliterators.emptyDoubleSpliterator();

                then(sp.estimateSize()).isEqualTo(0);

                then(sp.estimateSize()).isEqualTo(0);
                then(sp.getExactSizeIfKnown()).isEqualTo(0);

                then(sp.tryAdvance((DoubleConsumer) n -> fail())).isFalse();
                sp.forEachRemaining((DoubleConsumer) n -> fail());

                then(sp.trySplit()).isNull();
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED);
            }
        }

        /**
         * 将数组包装为 {@link Spliterator} 对象
         *
         * <p>
         * 通过 {@link Spliterators#spliterator(Object[], int)} 方法可以创建一个基于指定数组的
         * {@link Spliterator} 对象,
         * 且其具备 {@link Spliterator#SIZED} 和 {@link Spliterator#SUBSIZED} 特性
         * </p>
         *
         * <p>
         * 另外, {@code spliterator} 方法还具有一组重载, 用于针对简单类型数组, 包括: {@code int[]},
         * {@code long[]} 以及
         * {@code double[]} 类型
         * </p>
         */
        @Test
        void spliterator_shouldCreateSpliteratorFromArray() {
            // 基于引用类型数组创建 Spliterator 对象
            {
                // 基于数组创建 Spliterator 对象, 并附加 SORTED 属性
                var sp = Spliterators.spliterator(new String[] { "A", "B", "C", "D", "E" }, Spliterator.SORTED);

                // 确认产生的 Spliterator 对象具备除附加的 SORTED 属性外, 还包括 SIZED 和 SUBSIZED 属性
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.SORTED);
                then(sp.estimateSize()).isEqualTo(5);
            }

            // 基于 int[] 类型数组创建 Spliterator 对象
            {
                // 基于数组创建 Spliterator 对象, 并附加 SORTED 属性
                var sp = Spliterators.spliterator(new int[] { 1, 2, 3, 4, 5 }, Spliterator.SORTED);

                // 确认产生的 Spliterator 对象具备除附加的 SORTED 属性外, 还包括 SIZED 和 SUBSIZED 属性
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.SORTED);
                // 确认产生的 Spliterator 对象为 Spliterator.OfInt 类型
                then(sp).isInstanceOf(OfInt.class);
                then(sp.estimateSize()).isEqualTo(5);
            }

            // 基于 long[] 类型数组创建 Spliterator 对象
            {
                // 基于数组创建 Spliterator 对象, 并附加 SORTED 属性
                var sp = Spliterators.spliterator(new long[] { 1, 2, 3, 4, 5 }, Spliterator.SORTED);

                // 确认产生的 Spliterator 对象具备除附加的 SORTED 属性外, 还包括 SIZED 和 SUBSIZED 属性
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.SORTED);
                // 确认产生的 Spliterator 对象为 Spliterator.OfLong 类型
                then(sp).isInstanceOf(OfLong.class);
                then(sp.estimateSize()).isEqualTo(5);
            }

            // 基于 double[] 类型数组创建 Spliterator 对象
            {
                // 基于数组创建 Spliterator 对象, 并附加 SORTED 属性
                var sp = Spliterators.spliterator(new double[] { 1.1, 1.2, 1.3, 1.4, 1.5 }, Spliterator.SORTED);

                // 确认产生的 Spliterator 对象具备除附加的 SORTED 属性外, 还包括 SIZED 和 SUBSIZED 属性
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.SORTED);
                // 确认产生的 Spliterator 对象为 Spliterator.OfDouble 类型
                then(sp).isInstanceOf(OfDouble.class);
                then(sp.estimateSize()).isEqualTo(5);
            }

            // 基于 List<T> 类型数组创建 Spliterator 对象
            {
                // 基于数组创建 Spliterator 对象, 并不附加属性
                var sp = Spliterators.spliterator(List.of("A", "B", "C", "D", "E"), 0);

                // 确认产生的 Spliterator 对象具备 SIZED 和 SUBSIZED 属性
                then(sp.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED);
                then(sp.estimateSize()).isEqualTo(5);
            }
        }

        /**
         * 将 {@link java.util.Iterator Iterator} 对象转为 {@link Spliterator} 对象
         *
         * <p>
         * 通过 {@link Spliterators#spliteratorUnknownSize(java.util.Iterator, int)}
         * 方法可以将一个
         * {@link java.util.Iterator Iterator} 对象转为 {@link Spliterator} 而无需知道确切的元素个数
         * </p>
         *
         * <p>
         * 将 {@code Iterator} 对象转为 {@link Spliterator} 对象时, 不会使用"延迟绑定"方式
         * (<i>late-binding</i>),
         * 因为其已经直接绑定到了 {@code Iterator} 对象本身, 所以对 {@code Iterator} 对象的操作也会影响到
         * {@link Spliterator} 对象
         * </p>
         *
         * <p>
         * 将 {@code Iterator} 对象转为的 {@link Spliterator} 对象支持有限的分割能力, 即在使用
         * {@link Spliterator#trySplit()}
         * 方法时, 分割的结果可能会不符合预期
         * </p>
         */
        @Test
        void spliteratorUnknownSize_shouldConvertIteratorToSpliterator() {
            // 产生一个迭代器对象
            var iter = IntStream.range(0, 10).boxed().iterator();

            // 将迭代器转为 Spliterator 对象, 并附加 SORTED 特性
            var sp = Spliterators.spliteratorUnknownSize(iter, Spliterator.SORTED);
            // 确认得到的 Spliterator 对象具备指定的特性
            then(sp.characteristics()).isEqualTo(Spliterator.SORTED);
            // 该 Spliterator 对象无确定长度, 即长度未知
            then(sp.getExactSizeIfKnown()).isEqualTo(-1);
            // 确认 Spliterator 对象包含指定的元素
            then(toList(sp)).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

            // 重新产生一个迭代器对象
            iter = IntStream.range(0, 10).boxed().iterator();
            // 将迭代器对象转为 Spliterator 对象
            var sp1 = Spliterators.spliteratorUnknownSize(iter, 0);
            // 对得到的 Spliterator 对象进行分割
            var sp2 = sp1.trySplit();

            // 确认分割后的第一部分无内容
            then(sp1.getExactSizeIfKnown()).isEqualTo(-1);
            then(toList(sp1)).isEmpty();

            // 确认分割后的另一部分具备确定长度, 可继续分隔
            then(sp2.getExactSizeIfKnown()).isEqualTo(-1);
            then(sp2.characteristics()).isEqualTo(0);
            then(toList(sp2)).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        }
    }
}
