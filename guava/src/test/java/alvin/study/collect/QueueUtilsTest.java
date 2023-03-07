package alvin.study.collect;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

/**
 * 测试 Guava 队列工具类
 *
 * <p>
 * {@link Queues} 类具备一系列方法, 可以产生各种所需的队列
 * </p>
 */
class QueueUtilsTest {
    /**
     * 创建一个值在指定区间内的整数元素列表对象
     *
     * @param lower    元素最小值
     * @param upper    元素最大值
     * @param shuffled 是否打乱元素顺序, {@code false} 时元素按顺序排列, {@code true} 时元素顺序打乱
     * @return 区间元素组成的 {@link List} 集合对象
     */
    static List<Integer> createRangedElements(int lower, int upper, boolean shuffled) {
        var list = Lists.newArrayList(ContiguousSet.closedOpen(lower, upper));
        if (shuffled) {
            Collections.shuffle(list);
        }
        return list;
    }

    /**
     * 创建 {@link java.util.ArrayDeque ArrayDeque} 类型队列
     *
     * <p>
     * 通过 {@link Queues#newArrayDeque()} 方法可以创建一个 {@code ArrayDeque} 类型队列
     * </p>
     *
     * <p>
     * 通过 {@link Queues#newArrayDeque(Iterable)} 方法可以创建一个 {@code ArrayDeque} 类型队列并初始化队列的元素
     * </p>
     *
     * <p>
     * {@code ArrayDeque} 是一个循环数组结构的双端队列, 在队列元素大于数组容量时会进行扩容操作 (类似于 {@code ArrayList} 类型),
     * 其作为"队列"或者"栈"时, 时间效率要高于 {@link java.util.LinkedList LinkedList}
     * </p>
     */
    @Test
    void newArrayDeque_shouldCreateArrayDeque() {
        // 测试将 ArrayDeque 用作"栈"
        {
            // 创建 ArrayDeque 对象
            var deque = Queues.<Integer>newArrayDeque();

            // 创建一个保存出队结果的 List 集合
            var elements = Lists.<Integer>newArrayList();

            // 将产生的元素依次入栈
            createRangedElements(0, 5, false).forEach(v -> deque.push(v));
            // 确认可以从栈中获取入栈的元素
            while (!deque.isEmpty()) {
                elements.add(deque.pop());
            }
            // 确认从栈中获取元素的顺序
            then(elements).containsExactly(4, 3, 2, 1, 0);
        }

        // 测试将 ArrayDeque 用作"队列"
        {
            // 创建 ArrayDeque 对象
            var deque = Queues.<Integer>newArrayDeque();

            // 创建一个保存出队结果的 List 集合
            var elements = Lists.<Integer>newArrayList();

            // 将产生的元素依次入队
            createRangedElements(0, 5, false).forEach(v -> deque.offer(v));
            // 确认可以从队列中获取入队的元素
            while (!deque.isEmpty()) {
                elements.add(deque.poll());
            }
            // 确认从队列中获取元素的顺序
            then(elements).containsExactly(0, 1, 2, 3, 4);
        }
    }

    @Test
    void newPriorityQueue_shouldCreateNewPriorityQueue() {
        var priQue = Queues.newPriorityQueue(createRangedElements(0, 5, true));

        for (var i = 0; !priQue.isEmpty(); i++) {
            then(priQue.poll()).isEqualTo(i);
        }
    }

    @Test
    @SuppressWarnings("java:S2925")
    void newPriorityBlockingQueue_shouldCreatePriorityBlockingQueue() throws InterruptedException {
        var priQue = Queues.<Integer>newPriorityBlockingQueue();

        var thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                priQue.addAll(createRangedElements(0, 5, true));
            } catch (InterruptedException e) {}
        });

        thread.start();

        var timestamp = System.currentTimeMillis();
        await().atMost(2, TimeUnit.SECONDS).until(() -> !priQue.isEmpty());
        then(System.currentTimeMillis() - timestamp).isGreaterThanOrEqualTo(1000);

        for (var i = 0; !priQue.isEmpty(); i++) {
            then(priQue.take()).isEqualTo(i);
        }
    }
}
