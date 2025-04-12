package alvin.study.se.concurrent.task;

import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 计算任务类
 *
 * <p>
 * 从 {@link RecursiveTask} 类继承, 表示任务具备 {@code List<Integer>} 类型返回值
 * </p>
 */
public class EvenRecursiveTask extends RecursiveTask<List<Integer>> {
    public static class Context {
        // 记录 fork 任务被执行的次数
        private final AtomicInteger forkCount = new AtomicInteger();

        // 记录计算任务被执行的次数
        private final AtomicInteger computedTimes = new AtomicInteger();

        /**
         * 记录 fork 任务被执行的次数
         */
        public void incrementForkCount() {
            forkCount.incrementAndGet();
        }

        /**
         * 记录计算任务被执行的次数
         *
         * @param num 计算任务被执行的次数
         */
        public void addComputedComputedTimes(int num) {
            computedTimes.addAndGet(num);
        }

        /**
         * 获取已计算的数值数量
         *
         * @return 已计算的数值数量
         */
        public int getComputedTimes() { return computedTimes.get(); }

        /**
         * 获取 fork 任务被执行的次数
         *
         * @return fork 任务被执行的次数
         */
        public int getForkCount() { return forkCount.get(); }
    }

    // 要计算数值的起始值
    private final int start;

    // 要计算数值的结束值, 本例中要计算 start ~ end 区间内所有数值中包含的偶数结果
    private final int end;

    // 上下文对象, 记录 fork 任务被执行的次数和计算任务被执行的次数
    private Context context;

    /**
     * 构造器, 构造计算任务
     *
     * @param start 要计算数值的起始值
     * @param end   要计算数值的结束值
     */
    public EvenRecursiveTask(Context ctx, int start, int end) {
        this.context = ctx;
        this.start = start;
        this.end = end;
    }

    /**
     * 进行计算, 返回计算结果, 即 start ~ end 区间内所有数值中包含的偶数组成的集合
     */
    @Override
    protected List<Integer> compute() {
        // 记录一次 fork 任务被执行
        context.incrementForkCount();

        // 记录本次要计算的数值数量
        var size = end + 1 - start;

        // 如果要计算的数值小于 5, 则开始计算
        if (size < 5) {
            // 记录已计算的数值数量
            context.addComputedComputedTimes(size);

            // 计算 start 和 end 区间内的偶数值
            return IntStream.range(start, end + 1)
                    .filter(value -> value % 2 == 0)
                    .boxed()
                    .toList();
        }

        // 计算中间值, 通过中间值将要计算的数值分为两部分
        var mid = (start + end) / 2;

        // 构建分支 1, 计算前一半数值
        var task1 = new EvenRecursiveTask(context, start, mid);
        // 执行分支
        task1.fork();

        // 构建分支 2, 计算后一半数值
        var task2 = new EvenRecursiveTask(context, mid + 1, end);
        // 执行分支
        task2.fork();

        // 将分支的计算结果进行合并
        return Stream.concat(task1.join().stream(), task2.join().stream()).toList();
    }
}
