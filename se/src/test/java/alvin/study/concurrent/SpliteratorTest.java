package alvin.study.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

/**
 * 测试 {@link Spliterator} 对象, 即可分割迭代器对象
 *
 * <p>
 * Splittable Iterator, 是 Java 为并发应用程序设计的
 * </p>
 */
@SuppressWarnings("java:S2925")
class SpliteratorTest {
    /**
     * 获取 {@link Spliterator} 类型对象的特性
     *
     * <p>
     * 通过 {@link Spliterator#hasCharacteristics(int)} 可以判断是否具备指定的特性; 而通过 {@link Spliterator#characteristics()}
     * 方法可以获取全部的特性
     * </p>
     *
     * <p>
     * {@link Spliterator} 有 {@code 8} 个特征, 包括:
     * <ul>
     * <li>{@link Spliterator#DISTINCT}, 值为 {@code 0x00000001}, 表示元素不重复</li>
     * <li>{@link Spliterator#SORTED}, 值为 {@code 0x00000004}, 表示元素是按一定规律进行排列 (有指定比较器)</li>
     * <li>{@link Spliterator#ORDERED}, 值为 {@code 0x00000010}, 表示元素是有序的 (每一次遍历结果相同)</li>
     * <li>{@link Spliterator#SIZED}, 值为 {@code 0x00000040}, 表示大小是固定的</li>
     * <li>{@link Spliterator#NONNULL}, 值为 {@code 0x00000100}, 表示没有 {@code null} 元素</li>
     * <li>{@link Spliterator#IMMUTABLE}, 值为 {@code 0x00000400}, 表示元素不可变</li>
     * <li>{@link Spliterator#CONCURRENT}, 值为 {@code 0x00001000}, 表示迭代器可以多线程操作</li>
     * <li>{@link Spliterator#SUBSIZED}, 值为 {@code 0x00004000}, 表示子 {@link Spliterator} 都具有 {@code SIZED} 特性</li>
     * </ul>
     * </p>
     */
    @Test
    void characteristics_shouldGetCharacteristicsOfSpliterator() {
        // 测试基于 List 的 Spliterator 对象的特性
        var listSp = IntStream.range(0, 10).boxed().spliterator();
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
     * 通过 {@link Spliterator#estimateSize()} 方法可以获取对象中待处理的元素个数
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
        // 确认每部分元素个数为分割前元素个数的 1/2
        then(part1.estimateSize()).isEqualTo(5);
        then(part2.estimateSize()).isEqualTo(5);
    }

    /**
     * 将一个 {@link Spliterator} 对象进行分割
     *
     * <p>
     * 通过 {@link Spliterator#trySplit()} 方法对指定的 {@link Spliterator} 对象进行分割, 分割后得到一个新的 {@link Spliterator}
     * 对象, 该对象包含原 {@link Spliterator} 对象的一半元素, 原 {@link Spliterator} 也剩余一半元素
     * </p>
     */
    @Test
    void trySplit_shouldSplitCollectionIntoSlice() throws InterruptedException {
        //
        var part1 = IntStream.range(0, 10).boxed().toList().spliterator();
        then(part1.estimateSize()).isEqualTo(10);

        var part2 = part1.trySplit();
        then(part1.estimateSize()).isEqualTo(5);
        then(part2.estimateSize()).isEqualTo(5);

        then(toList(part1, part2)).containsExactlyInAnyOrder(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    /**
     * 将多个 {@link Spliterator} 对象合并到一个 {@link List} 集合对象中
     *
     * @param <T>          集合元素类型
     * @param spliterators 多个 {@link Spliterator} 对象
     * @return 包含所有 {@link Spliterator} 对象元素的 {@link List} 集合对象
     */
    @SafeVarargs
    private static <T> List<T> toList(Spliterator<T>... spliterators) {
        var result = new ArrayList<T>();
        for (var sp : spliterators) {
            StreamSupport.stream(sp, false).forEach(result::add);
        }
        return result;
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
        var executor = new ThreadPoolExecutor(
            processorCount,
            processorCount,
            0,
            TimeUnit.NANOSECONDS,
            new ArrayBlockingQueue<>(1000));

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
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> then(tasks).allMatch(t -> t.isDone()));

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
            101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199,
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
