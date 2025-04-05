package alvin.study.se.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * 测试在并发环境下的 {@link java.util.stream.Stream Stream} 对象
 *
 * <p>
 * 通过并发 {@code Stream} 处理集合时, 会将集合进行分组,
 * 发送到多个线程中进行并行处理, 这种方式可以在很多场景中充分利用 CPU
 * 的多核心能力, 提升数据处理的效率
 * </p>
 *
 * <p>
 * 通过 {@link java.util.Collection#parallelStream()
 * Collection.parallelStream()} 方法或者第二个参数为 {@code true}
 * 的 {@link java.util.stream.StreamSupport#stream(
 * java.util.Spliterator, boolean)
 * StreamSupport.stream(Spliterator, boolean)} 方法可以获得并发
 * {@link java.util.stream.Stream Stream} 对象
 * </p>
 *
 * <p>
 * 并发 {@code Stream} 的缺点在于开发者无法控制并发数量 (线程数),
 * 这会导致不可预期资源占用, 从而影响其它并行任务的执行
 * </p>
 */
class ParallelStreamTest {
    /**
     * 通过并发 {@link java.util.stream.Stream Stream}
     * 处理集合数据
     */
    @Test
    void parallelStream_shouldCalculateByParallelStream() {
        // 用于保存数据和线程 ID 的类型
        class Record {
            // 该对象的值
            final int value;
            // 处理该对象的线程 ID
            long threadId;

            public Record(int value) {
                this.value = value;
            }
        }

        // 生成 10 个 Record 对象
        var records = IntStream.range(0, 10)
                .mapToObj(Record::new)
                .toList();

        // 记录起始时间
        var ts = System.currentTimeMillis();

        // 通过并行的 Stream 通过多线程处理数据
        // 本例中使用了 filter 算子, 用于过滤值为奇数的对象
        var results = records.parallelStream().filter(r -> {
            try {
                // 模拟数据处理时间
                Thread.sleep(100);
                // 过滤数据
                if (r.value % 2 == 0) {
                    // 记录执行该方法的线程 id
                    r.threadId = Thread.currentThread().threadId();
                    return true;
                }
            } catch (InterruptedException ignored) {}
            return false;
        }).toList();

        // 确认整体执行时间
        // 如果以单线程执行, 则需要 100 * 10 = 1s 来处理所有数据
        // 对于并发情况, 即便只有 2 个线程, 这个过程也会减少到 500ms
        await().atMost(10 * 100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> then(results).hasSize(5));

        then(System.currentTimeMillis() - ts).isLessThan(500);

        // 查看执行任务的线程 id, 这里共使用了 5 个线程, 即每个线程处理 2 个对
        var threadIds = results.stream().map(r -> r.threadId).distinct().toList();
        then(threadIds).hasSize(5);

        // 确认执行结果
        var evens = results.stream().map(r -> r.value).toList();
        then(evens).containsExactlyInAnyOrder(0, 2, 4, 6, 8);
    }
}
