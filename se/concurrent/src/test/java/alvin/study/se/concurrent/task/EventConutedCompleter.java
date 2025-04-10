package alvin.study.se.concurrent.task;

import java.util.List;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 计算任务类
 *
 * <p>
 * 从 {@link CountedCompleter} 类继承, 任务结果为 {@code List<Integer>} 类型值
 * </p>
 */
public class EventConutedCompleter extends CountedCompleter<List<Integer>> {
    // 要计算数值的起始值
    private final int start;

    // 要计算数值的结束值, 本例中要计算 start ~ end 区间内所有数值中包含的偶数结果
    private final int end;

    // 保持任务的计算结果
    private List<Integer> result;

    // 保持当前任务 fork 出的两个子任务引用
    private ForkJoinTask<List<Integer>> child1;
    private ForkJoinTask<List<Integer>> child2;

    /**
     * 构造器, 设置父任务引用以及当前任务计算区间范围
     *
     * @param parent 父任务引用
     * @param start  计算任务区间范围起始值
     * @param end    计算任务区间范围结束值
     */
    public EventConutedCompleter(CountedCompleter<List<Integer>> parent, int start, int end) {
        super(parent);
        this.start = start;
        this.end = end;
    }

    /**
     * 执行计算过程
     */
    @Override
    public void compute() {
        // 记录本次要计算的数值数量
        var size = end + 1 - start;

        // 如果要计算的数值小于 5, 则开始计算
        if (size < 5) {
            setRawResult(
                IntStream.range(start, end + 1)
                        .filter(value -> value % 2 == 0)
                        .boxed()
                        .toList());
        } else {
            // 计算中间值, 通过中间值将要计算的数值分为两部分
            var mid = (start + end) / 2;

            // 增加任务计数器, 表示当前任务要分裂出 2 个子任务
            addToPendingCount(2);

            // 构建分支 1, 计算前一半数值, 并执行分支, 保存子任务引用
            child1 = new EventConutedCompleter(this, start, mid).fork();

            // 构建分支 2, 计算后一半数值, 并执行分支, 保存子任务引用
            child2 = new EventConutedCompleter(this, mid + 1, end).fork();
        }

        // 当前任务结束, 尝试结束根任务
        tryComplete();
    }

    /**
     * 当前任务结束后调用, 合并子任务执行完毕后的结果
     *
     * @param caller 调用该方法的 {@link CountedCompleter} 对象, 即某个子任务 (或当前任务) 在执行
     *               {@code tryComplete}
     *               方法时, 会调用父任务 (或自身) 的 {@code onCompletion} 方法, {@code caller}
     *               参数即表示调用该方法的那个对象
     */
    @Override
    public void onCompletion(CountedCompleter<?> caller) {
        // 本例中, 只有子任务执行完毕, 调用父任务 (即 caller 不是当前任务) 时,
        // 方需执行, 因为本方法要合并的结果存储于子任务中
        if (caller != this && result == null) {
            setRawResult(Stream.concat(
                child1.getRawResult().stream(),
                child2.getRawResult().stream()).toList());
        }
    }

    @Override
    public List<Integer> getRawResult() { return result == null ? List.of() : result; }

    @Override
    protected void setRawResult(List<Integer> result) { this.result = result; }
}
