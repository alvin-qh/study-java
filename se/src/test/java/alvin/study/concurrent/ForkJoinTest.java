package alvin.study.concurrent;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * 演示 Fork/Join 线程池
 *
 * <p>
 * Fork/Join 框架由 {@link ForkJoinPool} 线程池类型, {@link java.util.concurrent.ForkJoinTask ForkJoinTask} 任务类型以及
 * {@link ForkJoinWorkerThread} 工作线程类型组成
 * </p>
 *
 * <p>
 * Fork/Join 框架的作用是通过并发的方式进行对任务进行分治处理, 即将一个大任务在过程中拆分为一系列小任务, 并逐个进行处理, 并将各任务结果进行合并,
 * 通过这种方式来充分利用当前系统的多核心
 * </p>
 *
 * <p>
 * <img src="assets/fork_join_pool.png">
 * </p>
 *
 * <p>
 * 与 {@link java.util.concurrent.ScheduledThreadPoolExecutor ScheduledThreadPoolExecutor} 相比, {@link ForkJoinPool}
 * 会为每一个线程创建一个任务队列, 拆解后的任务会分配到各个线程的队列中, 而且 {@code ScheduledThreadPoolExecutor}
 * 线程池中的任务之间并无先后关联, {@code ForkJoinPool} 线程池则允许任务之间存在先后关系, 即后一个任务必须基于前一个任务的结果
 * </p>
 *
 * <p>
 * 若一个线程工作队列中的任务执行完毕, 进入"空闲"状态后, 该线程会从其它工作线程的队列"窃取"一个任务来执行, 从而保证整体的并发性, 其中,
 * 工作线程总是从其队列的头部获取任务, 而空闲线程是从其它工作线程队列的"尾部"窃取任务
 * </p>
 *
 * <p>
 * Fork/Join 线程池应该用于"计算密集型"任务的分解和处理, 而非 IO 密集型, 因为 IO 的阻塞并不会实际占用 CPU 资源, 但会占用线程池中的线程,
 * 而 Fork/Join 模式父任务会等待子任务结束, 从而导致连锁的阻塞情况
 * </p>
 *
 * <p>
 * JDK 8 中引入的 Parallel Stream (并行流) 内部就是通过 Fork/Join 框架实现的
 * </p>
 */
class ForkJoinTest {
    /**
     * 自定义 Fork/Join 工作线程类型
     */
    static class CustomWorkerThread extends ForkJoinWorkerThread {
        /**
         * 构造器, 通过 Fork/Join 线程池创建工作线程对象
         *
         * @param pool Fork/Join 线程池对象
         */
        CustomWorkerThread(ForkJoinPool pool) {
            super(pool);
        }
    }

    /**
     * 自定义 Fork/Join 线程工厂类型
     */
    static class CustomWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
        /**
         * 创建新的 Fork/Join 工作线程对象
         *
         * @param pool Fork/Join 工作线程池对象
         * @return 新的 Fork/Join 工作线程池对象, 或者 {@code null} 表示拒绝产生新线程
         */
        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new CustomWorkerThread(pool);
        }
    }

    /**
     * 测试创建 Fork/Join 线程池并提交任务
     *
     * <p>
     * 通过 {@link ForkJoinPool} 类的构造器来创建 Fork/Join 线程池对象, 其参数包括:
     * <ul>
     * <li>
     * {@code parallelism}: 并行度, 默认为当前系统的逻辑内核数 ({@link Runtime#availableProcessors()} 返回值)
     * </li>
     * <li>
     * {@code factory}: 一个实现了 {@link ForkJoinWorkerThreadFactory} 接口的工厂类对象, 用于创建工作线程对象
     * </li>
     * <li>
     * {@code handler}: 一个 {@link java.lang.Thread.UncaughtExceptionHandler UncaughtExceptionHandler} 接口对象,
     * 当执行任务过程中抛出异常时, 由该 handler 对象来处理异常, 该参数为 {@code null} 表示使用默认的异常处理
     * </li>
     * <li>
     * {@code asyncMode}: 若该参数为 {@code true}, 则每个线程使用 FIFO (先进先出) 的方式从队列中获取任务; 默认为 {@code false},
     * 表示每个线程通过 LIFO (后进先出) 的方式获取任务. FIFO 方式更有利于 {@link java.util.concurrent.RecursiveAction
     * RecursiveAction} 这类无需返回值的并行任务
     * </li>
     * <li>
     * {@code corePoolSize}: 线程池中保持活动的线程数量, 一般情况下与 {@code parallelism} 保持相同即可, 但如果发现任务经常被阻塞,
     * 则可以将该数值设置的大一些以减少任务调度的开销. 设置为较小的值 (例如 {@code 0}) 和默认值具备相同的效果
     * </li>
     * <li>
     * {@code maximumPoolSize}: 线程池允许的最大线程数. 当线程池中的线程都处于工作状态, 但工作队列中仍有未完成任务时,
     * 线程池会创建新的线程, 但如果线程总数已经到达该数量, 则创建新线程会失败
     * </li>
     * <li>
     * {@code minimumRunnable}: 最小活动线程, 即不被阻塞的预留线程数, 该数值默认为 {@code 1}, 即一直保留一个非阻塞的活动线程,
     * 提高该数字可能会提高响应能力, 但并不是绝对的, 反而可能会因为过多的活动线程增加资源损耗. 另外, 如果线程池的所有任务都相互不依赖
     * (即不需要在父任务等待子任务的结果), 也可以将值设置为 {@code 0}
     * </li>
     * <li>
     * {@code saturate}: 一个 {@link java.util.function.Predicate Predicate} 类型的回调对象, 当尝试创建超出最大线程数限制的线程时,
     * 会回调此对象, 以决策是否要创建该线程, 如果该回调返回 {@code true}, 则创建新线程, 否则抛出
     * {@link java.lang.Thread.UncaughtExceptionHandler UncaughtExceptionHandler} 异常. 默认值为 {@code null},
     * 即使用默认的 {@code Predicate} 对象, 表示不允许创建超出最大线程数要求的线程
     * </li>
     * <li>
     * {@code keepAliveTime} 和 {@code unit}: 表示当一个线程从开始空闲到被销毁的等待时间, 一个线程执行完其任务后,
     * 如果其工作队列中无要执行的任务, 且总线程数大于 {@code corePoolSize}, 则该空闲线程将会被销毁, 但也有可能其它线程 fork
     * 出了新的任务加入其工作队列, 所以等待一段时间, 以尽量保证不要频繁的销毁, 创建线程
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * Fork/Join 线程池提供了若干创建线程池的简便方式, 包括:
     * <ul>
     * <li>
     * {@link ForkJoinPool#commonPool()} 方法, 返回一个公共的线程池, 可以在整个应用中公用该线程池, 一般情况下用这个线程池即可
     * </li>
     * <li>
     * {@link ForkJoinPool#ForkJoinPool()} 构造器, 所有的参数都使用默认值取代, 可以满足大部分使用场景
     * </li>
     * <li>
     * {@link ForkJoinPool#ForkJoinPool(int)} 构造器, 只需指定并发数, 可以控制线程数量, 以便调整并发响应或资源占用情况
     * </li>
     * <li>
     * {@link ForkJoinPool#ForkJoinPool(int, ForkJoinWorkerThreadFactory, java.lang.Thread.UncaughtExceptionHandler, boolean)
     * ForkJoinPool.ForkJoinPool(int, ForkJoinWorkerThreadFactory, UncaughtExceptionHandler, boolean)} 构造器,
     * 只保留部分关键参数 ({@code parallelism}, {@code factory}, {@code handler}, {@code asyncMode}), 其余参数均为默认值
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 构建 {@link RecursiveTask} 类型的子类作为计算任务类, 且每个计算任务需要返回值 (如果无返回值可使用
     * {@link java.util.concurrent.RecursiveAction RecursiveAction} 类型子类), 可以通过 {@link RecursiveTask#fork()}
     * 产生子任务, 并通过 {@link RecursiveTask#join()} 方法等待子任务结束并获取子任务的返回值
     * </p>
     *
     * <p>
     * 本例中, 需要计算 1~10000 所有数字中的偶数, 当计算量大于 5 个数时, fork 出两个新的任务各自计算一半的数值, 这样就可以将 10000
     * 个数值的计算任务不断分裂成很多个小于 5 个数值的计算任务, 最终将所有计算任务的结果合并在一起形成最终结果
     * </p>
     */
    @Test
    void forkJoinPool_shouldCreateForkJoinThreadPoolAndSubmitTasks() throws Exception {
        // 经过计算的数字总和, 最终应该为 10000, 即所有要计算的数字
        var computedNums = new AtomicInteger();

        // 所有 fork 出任务的总和 (包括第一个任务)
        var forkCount = new AtomicInteger();

        /**
         * 计算任务类, 从 {@link RecursiveTask} 类继承, 表示任务具备 {@code List<Integer>} 类型返回值
         */
        class EvenTask extends RecursiveTask<List<Integer>> {
            // 要计算数值的起始值
            private final int start;

            // 要计算数值的结束值, 本例中要计算 start ~ end 区间内所有数值中包含的偶数结果
            private final int end;

            /**
             * 构造器, 构造计算任务
             *
             * @param start 要计算数值的起始值
             * @param end   要计算数值的结束值
             */
            public EvenTask(int start, int end) {
                this.start = start;
                this.end = end;
            }

            /**
             * 进行计算, 返回计算结果, 即 start ~ end 区间内所有数值中包含的偶数组成的集合
             */
            @Override
            protected List<Integer> compute() {
                // 记录一次 fork 任务被执行
                forkCount.getAndIncrement();

                // 记录本次要计算的数值数量
                var size = end + 1 - start;

                // 如果要计算的数值小于 5, 则开始计算
                if (size < 5) {
                    // 记录已计算的数值数量
                    computedNums.addAndGet(size);

                    // 计算 start 和 end 区间内
                    return IntStream.range(start, end + 1)
                            .filter(value -> value % 2 == 0)
                            .boxed()
                            .toList();
                }

                // 计算中间值, 通过中间值将要计算的数值分为两部分
                var mid = (start + end) / 2;

                // 构建分支 1, 计算前一半数值
                var task1 = new EvenTask(start, mid);
                // 执行分支
                task1.fork();

                // 构建分支 2, 计算后一半数值
                var task2 = new EvenTask(mid + 1, end);
                // 执行分支
                task2.fork();

                // 将分支的计算结果进行合并
                return Stream.concat(task1.join().stream(), task2.join().stream()).toList();
            }
        }

        // 获取当前系统的逻辑内核数
        var kernelCount = Runtime.getRuntime().availableProcessors();

        // 创建 Fork/Join 线程池
        var pool = new ForkJoinPool(
            kernelCount,
            new CustomWorkerThreadFactory(),
            null,
            false,
            kernelCount,
            256,
            1,
            null,
            60,
            TimeUnit.SECONDS);

        // 提交计算任务, 计算 1~10000 之间的所有偶数
        var task = pool.submit(new EvenTask(1, 10000));

        // 等待所有数值均已被计算过
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> then(computedNums.get()).isEqualTo(10000));

        // 确定任务已结束
        then(task.isDone()).isTrue();

        // 确认计算结果为 5000 个数值, 且均为偶数
        then(task.get()).hasSize(5000).allMatch(n -> n % 2 == 0);

        // 确认共产生了 7711 个任务
        then(forkCount.get()).isEqualTo(7711);
    }
}
