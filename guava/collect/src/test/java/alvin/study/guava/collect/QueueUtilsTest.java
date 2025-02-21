package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.ArrayDeque;
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
 *
 * <p>
 * 对于队列, 表示一个实现了 {@link java.util.Queue Queue} 接口的对象, 其中:
 * <ul>
 * <li>
 * 通过 {@link java.util.Queue#offer(Object) Queue.offer(T)} 方法进行入队
 * </li>
 * <li>
 * 通过 {@link java.util.Queue#poll() Queue.poll()} 方法进行出队
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 对于栈, 表示一个实现了 {@link java.util.Stack Stack} 接口的对象, 其中:
 * <ul>
 * <li>
 * 通过 {@link java.util.Stack#push(Object) Stack.push(T)} 方法进行入栈
 * </li>
 * <li>
 * 通过 {@link java.util.Stack#pop() Stack.pop()} 方法进行出栈
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 对于 {@link java.util.Deque} 接口, 表示一个"双端队列", 即同时具备队列和栈的能力, 可以在 FIFO 与 FILO 两种数据进出形式间随意进行处理,
 * 包括:
 * <ul>
 * <li>
 * 通过 {@link java.util.Deque#push(Object) Deque.push(T)} / {@link java.util.Deque#addFirst(Object) Deque.addFirst(T)}
 * 这两个类似方法完成入栈 (添加在集合首位) 操作
 * </li>
 * <li>
 * 通过 {@link java.util.Deque#pop() Deque.pop(T)} / {@link java.util.Deque#removeFirst() Deque.removeFirst()}
 * 这两个类似方法完成出栈 (获取集合首元素并删除) 操作
 * </li>
 * <li>
 * 通过 {@link java.util.Deque#offer(Object) Deque.offer(T)} / {@link java.util.Deque#add(Object) Deque.add(T)}
 * 这两个类似方法完成入队 (添加在集合末尾) 操作
 * </li>
 * <li>
 * 通过 {@link java.util.Deque#poll() Deque.poll()} / {@link java.util.Deque#removeFirst() Deque.removeFirst()}
 * 这两个类似方法完成出队 (获取集合首元素并删除) 操作
 * </li>
 * <li>
 * 通过 {@link java.util.Deque#peek() Deque.peek()} / {@link java.util.Deque#getFirst() Deque.getFirst()}
 * 这两个类似方法获取"队首 (栈顶)" 元素
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 对于阻塞队列, 表示一个实现了 {@link java.util.concurrent.BlockingQueue BlockingQueue} 接口的对象, 除了 {@code Queue}
 * 接口相关的方法外, 还提供了:
 * <ul>
 * <li>
 * 通过 {@link java.util.concurrent.BlockingQueue#offer(Object, long, TimeUnit) BlockingQueue.offer(T, long, TimeUnit)}
 * 方法进行入队, 并在队列内元素到达上限时阻塞, 直到队列元素被消费, 有空余位置后完成入队
 * </li>
 * <li>
 * 通过 {@link java.util.concurrent.BlockingQueue#take() BlockingQueue.take()} 方法进行出队, 并在队列为空时阻塞,
 * 直到队列具有可出队的元素. 类似的方法还有 {@link java.util.concurrent.BlockingQueue#poll(long, TimeUnit)},
 * 可以设置队列为空时出队所阻塞的时间
 * </li>
 * <li>
 * 通过 {@link java.util.concurrent.BlockingQueue#drainTo(java.util.Collection) BlockingQueue.drainTo(Collection)} 方法,
 * 可以将队列中现有的元素写入一个集合中
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 对于双端阻塞队列, 表示一个实现了 {@link java.util.concurrent.BlockingDeque BlockingDeque} 接口的对象
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
     * 测试创建 {@link java.util.ArrayDeque ArrayDeque} 循环双端队列
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

            // 将产生的元素依次入栈
            createRangedElements(0, 5, false).forEach(deque::push);

            // 创建一个保存出队结果的 List 集合
            var elements = Lists.<Integer>newArrayList();

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

            // 将产生的元素依次入队
            createRangedElements(0, 5, false).forEach(deque::offer);

            // 创建一个保存出队结果的 List 集合
            var elements = Lists.<Integer>newArrayList();

            // 确认可以从队列中获取入队的元素
            while (!deque.isEmpty()) {
                elements.add(deque.poll());
            }
            // 确认从队列中获取元素的顺序
            then(elements).containsExactly(0, 1, 2, 3, 4);
        }
    }

    /**
     * 测试创建 {@link java.util.concurrent.ArrayBlockingQueue ArrayBlockingQueue} 阻塞循环双端队列
     *
     * <p>
     * 通过 {@link Queues#newArrayBlockingQueue(int)} 方法可以创建一个 {@code ArrayBlockingQueue} 类型队列,
     * 并设置该队列可以容纳元素的上限, 该上限值会导致在队列元素数量达到上限值后:
     * <ul>
     * <li>
     * {@link java.util.concurrent.ArrayBlockingQueue#offer(Object) ArrayBlockingQueue.offer(T)} 方法会返回
     * {@code false}, 表示入队失败
     * </li>
     * <li>
     * {@link java.util.concurrent.ArrayBlockingQueue#offer(Object, long, TimeUnit)
     * ArrayBlockingQueue.offer(T, long, TimeUnit)} 方法会进入阻塞,直到队列有元素被消费, 方能继续完成入队, 如果超时未完成则返回
     * {@code false}
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 如果队列为空时, 则会导致:
     * <ul>
     * <li>
     * {@link java.util.concurrent.ArrayBlockingQueue#poll() ArrayBlockingQueue.poll()} 方法返回 {@code null},
     * 表示队列此时为空
     * </li>
     * <li>
     * {@link java.util.concurrent.ArrayBlockingQueue#poll(long, TimeUnit) ArrayBlockingQueue.poll(long, TimeUint)}
     * 方法会进入阻塞, 直到队列加入新元素, 方能继续完成出队, 如果超时未完成则返回 {@code null} 值
     * </li>
     * <li>
     * {@link java.util.concurrent.ArrayBlockingQueue#take() ArrayBlockingQueue.take()} 方法会进入持续阻塞, 直到队列加入新元素,
     * 方能继续完成出队
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void newArrayBlockingQueue_shouldCreateArrayBlockingQueue() throws InterruptedException {
        // 创建 ArrayBlockingQueue 对象, 队列最大容量为 1
        var deque = Queues.<Integer>newArrayBlockingQueue(1);

        // 创建新线程, 在线程内进行入队操作
        var thread = new Thread(() -> {
            var ts = System.currentTimeMillis();
            // 产生 5 个有序元素, 依次入队
            for (var e : createRangedElements(0, 5, false)) {
                try {
                    // 模拟 IO 延迟, 每 100ms 入队一个元素
                    Thread.sleep(100);
                    deque.offer(e, 1, TimeUnit.SECONDS);
                } catch (InterruptedException ignore) {
                    break;
                }
            }
            // 入队 5 个元素时间持续至少 1600ms (而非 500ms)
            // 原因: 1. 队列最大容量只有 1, 所以队列每入队一次, 就必须出队一次, 方能进行下一次入队操作; 2. 出队的速度较慢,
            // 最终影响了除第一个外的后续元素入队速度
            then(System.currentTimeMillis() - ts).isGreaterThanOrEqualTo(1600);
        });

        thread.start();

        // 创建一个保存出队结果的 List 集合
        var elements = Lists.<Integer>newArrayList();

        var ts = System.currentTimeMillis();

        // 从队列出队 5 个元素
        while (elements.size() < 5) {
            elements.add(deque.poll(1, TimeUnit.SECONDS));
            // 模拟消费延迟, 每 500ms 处理队列中的一项
            Thread.sleep(500);
        }

        // 完成 5 个元素出队共计至少 2600ms
        // 后 4 个元素入队需 2500ms, 第一个元素需 100ms
        then(System.currentTimeMillis() - ts).isGreaterThanOrEqualTo(2600);

        // 确认从栈中获取元素的顺序
        then(elements).containsExactly(0, 1, 2, 3, 4);
    }

    /**
     * 测试创建 {@link java.util.PriorityQueue PriorityQueue} 优先队列
     *
     * <p>
     * 通过 {@link Queues#newPriorityQueue()} 方法可以创建一个 {@code PriorityQueue} 类型优先队列
     * </p>
     *
     * <p>
     * 通过 {@link Queues#newPriorityQueue(Iterable)} 方法可以创建一个 {@code PriorityQueue} 类型优先队列并初始化队列元素
     * </p>
     *
     * <p>
     * {@code PriorityQueue} 优先队列是一个特殊队列, 底层通过"堆"结构实现, 进行出队操作时, 会将队列中优先级最高的元素优先出队
     * </p>
     *
     * <p>
     * Guava 并未为 {@link java.util.PriorityQueue#PriorityQueue(java.util.Comparator) PriorityQueue(Comparator)}
     * 构造器提供相应的方法, 所以无法通过 Guava 库对优先队列中的元素设置比较规则, 所以元素类型必须实现
     * {@link java.util.Comparator Comparator} 接口
     * </p>
     */
    @Test
    void newPriorityQueue_shouldCreateNewPriorityQueue() {
        // 创建优先队列, 通过无序数值初始化队列元素
        var priQue = Queues.newPriorityQueue(createRangedElements(0, 5, true));

        // 进行出队操作
        for (var i = 0; !priQue.isEmpty(); i++) {
            // 确认出队元素有序, 即优先级高 (较小的) 元素优先出队
            then(priQue.poll()).isEqualTo(i);
        }
    }

    /**
     * 测试创建 {@link java.util.concurrent.PriorityBlockingQueue PriorityBlockingQueue} 对象
     *
     * <p>
     * 通过 {@link Queues#newPriorityBlockingQueue()} 方法可以创建一个 {@code PriorityBlockingQueue} 类型优先队列
     * </p>
     *
     * <p>
     * 通过 {@link Queues#newPriorityBlockingQueue(Iterable)} 方法可以创建一个 {@code PriorityBlockingQueue}
     * 类型优先队列并初始化队列元素
     * </p>
     *
     * <p>
     * {@code PriorityBlockingQueue} 表示一个按优先级出队的阻塞队列, 其出队优先规则和 {@link java.util.PriorityQueue
     * PriorityQueue} 保持一致
     * </p>
     */
    @Test
    void newPriorityBlockingQueue_shouldCreatePriorityBlockingQueue() throws InterruptedException {
        // 创建阻塞优先队列
        var priQue = Queues.<Integer>newPriorityBlockingQueue();

        // 创建线程, 写入阻塞优先队列
        var thread = new Thread(() -> {
            try {
                // 休眠 1 秒模拟 IO 延迟
                Thread.sleep(1000);
                // 向阻塞队列写入一系列无序整数值
                priQue.addAll(createRangedElements(0, 5, true));
            } catch (InterruptedException ignored) {}
        });

        // 启动线程, 对队列进行写操作
        thread.start();

        var timestamp = System.currentTimeMillis();

        // 等待, 直到可以从队列中读取到内容 (即队列不再为空)
        await().atMost(2, TimeUnit.SECONDS).until(() -> !priQue.isEmpty());
        // 确认等待的时间不小于 1 秒
        then(System.currentTimeMillis() - timestamp).isGreaterThanOrEqualTo(1000);

        // 确认队列元素按有序 (优先序) 的次序出队
        for (var i = 0; !priQue.isEmpty(); i++) {
            then(priQue.poll(1, TimeUnit.SECONDS)).isEqualTo(i);
        }
    }

    /**
     * 从 {@link java.util.concurrent.BlockingDeque BlockingDeque} 中将当前元素全部出队, 存入另一个集合对象中
     *
     * <p>
     * 通过 {@link Queues#drain(java.util.concurrent.BlockingQueue, java.util.Collection, int, long, TimeUnit)
     * Queues.drain(BlockingQueue, Collection, int, long, TimeUnit)} 方法可以将一个阻塞队列中当前存在的元素全部出队,
     * 并存入给定的一个集合对象中
     * </p>
     *
     * <p>
     * 该方法类似于 {@link java.util.concurrent.BlockingDeque#drainTo(java.util.Collection, int)
     * BlockingDeque.drainTo(Collection, int)} 方法, 与之不同的是, 后者只会在调用的那一时刻, 将队列的元素进行出队转存,
     * 而前者则具有一个超时时间的设定, 可以将队列本身内容和一段时间内入队的元素均进行出队转存
     * </p>
     */
    @Test
    void drain_shouldDrainedElementsFromBlockingQueue() throws InterruptedException {
        // 定义一个元素上限为 5 的阻塞循环队列
        var que = Queues.<Integer>newArrayBlockingQueue(5);

        // 定义用来存储出队元素的集合
        var elements = Lists.<Integer>newArrayList();

        // 预期在 2 秒内, 将队列出队 5 个元素, 转存入集合
        // 因为此时队列为空, 也无生产者向队列写入元素, 所以在超时时间到达时, 队列仍为空
        // 所以确认最终结果为转存了 0 个元素
        var len = Queues.drain(que, elements, 5, 2, TimeUnit.SECONDS);
        then(len).isEqualTo(0);

        // 启动一个线程向队列写入元素
        var thread = new Thread(() -> {
            try {
                // 总共向队列写入 5 个元素, 每次写入耗时 500ms
                for (var v : createRangedElements(0, 5, false)) {
                    // 模拟 IO 耗时
                    Thread.sleep(500);
                    // 元素入队
                    que.offer(v);
                }
            } catch (InterruptedException ignore) {}
        });

        thread.start();

        // 预期在 2.1 秒内, 将队列出队 5 个元素, 转存入集合
        // 因为在规定时间内, 生产方最多可以存入 4 个元素
        // 所以确认最终结果为转存了 4 个元素
        len = Queues.drain(que, elements, 5, 2100, TimeUnit.MILLISECONDS);
        then(len).isEqualTo(4);
        then(elements).containsExactly(0, 1, 2, 3);
    }

    /**
     * 从 {@link java.util.concurrent.BlockingDeque BlockingDeque} 中将当前元素全部出队, 存入另一个集合对象中
     *
     * <p>
     * 通过
     * {@link Queues#drainUninterruptibly(java.util.concurrent.BlockingQueue, java.util.Collection, int, long, TimeUnit)
     * Queues.drainUninterruptibly(BlockingQueue, Collection, int, long, TimeUnit)} 方法可以将一个阻塞队列中当前存在的元素全部出队,
     * 并存入给定的一个集合对象中
     * </p>
     *
     * <p>
     * 该方法类似于 {@link Queues#drain(java.util.concurrent.BlockingQueue, java.util.Collection, int, long, TimeUnit)
     * Queues.drain(BlockingQueue, Collection, int, long, TimeUnit)} 方法 (参考
     * {@link #drain_shouldDrainedElementsFromBlockingQueue()} 方法), 和后者不同, {@code drainUninterruptedly}
     * 方法不会在线程中断时抛出 {@link InterruptedException} 异常, 而是结束转存
     * </p>
     */
    @Test
    void drainUninterruptedly_shouldDrainedElementsFromBlockingQueue() {
        // 定义一个元素上限为 5 的阻塞循环队列
        var que = Queues.<Integer>newArrayBlockingQueue(5);

        // 定义用来存储出队元素的集合
        var elements = Lists.<Integer>newArrayList();

        // 预期在 2 秒内, 将队列出队 5 个元素, 转存入集合
        // 因为此时队列为空, 也无生产者向队列写入元素, 所以在超时时间到达时, 队列仍为空
        // 所以确认最终结果为转存了 0 个元素
        var len = Queues.drainUninterruptibly(que, elements, 5, 2, TimeUnit.SECONDS);
        then(len).isEqualTo(0);

        // 启动一个线程向队列写入元素
        var thread = new Thread(() -> {
            try {
                // 总共向队列写入 5 个元素, 每次写入耗时 500ms
                for (var v : createRangedElements(0, 5, false)) {
                    // 模拟 IO 耗时
                    Thread.sleep(500);
                    // 元素入队
                    que.offer(v);
                }
            } catch (InterruptedException ignore) {}
        });

        thread.start();

        // 预期在 2.1 秒内, 将队列出队 5 个元素, 转存入集合
        // 因为在规定时间内, 生产方最多可以存入 4 个元素
        // 所以确认最终结果为转存了 4 个元素
        len = Queues.drainUninterruptibly(que, elements, 5, 2100, TimeUnit.MILLISECONDS);
        then(len).isEqualTo(4);
        then(elements).containsExactly(0, 1, 2, 3);
    }

    /**
     * 创建链式阻塞队列
     *
     * <p>
     * 通过 {@link Queues#newLinkedBlockingQueue()} 方法可以创建一个阻塞式链表队列, 阻塞式队列参考
     * {@link #newArrayBlockingQueue_shouldCreateArrayBlockingQueue()} 范例
     * </p>
     *
     * <p>
     * 通过 {@link Queues#newLinkedBlockingDeque()} 方法可以创建一个阻塞式链表双端队列, 双端队列的使用方法参考
     * {@link #newArrayDeque_shouldCreateArrayDeque()} 范例
     * </p>
     *
     * <p>
     * 若需要非阻塞式的链表队列, 需使用 {@link Lists#newLinkedList()} 创建双端循环列表对象
     * </p>
     */
    @Test
    void linkedQueue_shouldCreateLinkedBlockingQueue() throws InterruptedException {
        // 测试阻塞式链表单端队列
        {
            // 定义一个阻塞式链表队列
            var que = Queues.<Integer>newLinkedBlockingQueue();

            // 启动一个线程向队列写入元素
            var thread = new Thread(() -> {
                try {
                    // 总共向队列写入 5 个元素, 每次写入耗时 500ms
                    for (var v : createRangedElements(0, 5, false)) {
                        // 模拟 IO 耗时
                        Thread.sleep(500);
                        // 元素入队
                        que.offer(v);
                    }
                } catch (InterruptedException ignore) {}
            });

            thread.start();

            var ts = System.currentTimeMillis();
            for (var i = 0; i < 5; i++) {
                // 确认元素出队
                then(que.take()).isEqualTo(i);
            }

            // 确认 5 个元素全部出队需要至少 2.5 秒
            then(System.currentTimeMillis() - ts).isGreaterThanOrEqualTo(2500);
        }

        // 测试阻塞式链表双端队列
        {
            // 定义一个阻塞式链表队列
            var deque = Queues.<Integer>newLinkedBlockingDeque();

            // 启动一个线程向队列写入元素
            var thread = new Thread(() -> {
                try {
                    // 总共向队列写入 5 个元素, 每次写入耗时 500ms
                    for (var v : createRangedElements(0, 5, false)) {
                        // 模拟 IO 耗时
                        Thread.sleep(500);
                        // 元素入栈
                        deque.push(v);
                    }
                } catch (InterruptedException ignore) {}
            });

            thread.start();

            var ts = System.currentTimeMillis();
            for (var i = 0; i < 5; i++) {
                // 确认元素出栈
                then(deque.take()).isEqualTo(i);
            }

            // 确认 5 个元素全部出栈需要至少 2.5 秒
            then(System.currentTimeMillis() - ts).isGreaterThanOrEqualTo(2500);
        }
    }

    /**
     * 创建同步队列
     *
     * <p>
     * 通过 {@link Queues#newSynchronousQueue()} 方法可以创建一个同步队列 ({@link java.util.concurrent.SynchronousQueue
     * SynchronousQueue} 对象), 通过该队列可以在多个线程之间同步数据
     * </p>
     *
     * <p>
     * 对于 {@code SynchronousQueue} 类型队列, 虽然实现了 {@link java.util.concurrent.BlockingQueue BlockingQueue} 接口,
     * 但并不真实的存储元素, 而是像一个通道一样, 生产者线程的入队操作只有同时被消费者线程同步出队, 方能入队成功, 否则会一直阻塞直到超时,
     * 这样可以让生产者和消费者线程在某个时刻达到完全同步
     * </p>
     */
    @Test
    void newSynchronousQueue_shouldCreateSynchronousQueue() throws InterruptedException {
        // 创建一个同步队列
        var que = Queues.<Integer>newSynchronousQueue();

        // 启动线程
        new Thread(() -> {
            try {
                var ts = System.currentTimeMillis();

                // 总共入队 5 个元素
                for (var v : createRangedElements(0, 5, false)) {
                    // 元素入队
                    then(que.offer(v, 1, TimeUnit.SECONDS)).isTrue();
                }

                // 确认入队最少消耗 2 秒
                // 入队花费时间是因为出队速度慢导致
                then(System.currentTimeMillis() - ts).isGreaterThanOrEqualTo(2000);
            } catch (InterruptedException ignore) {}
        }).start();

        var elems = Lists.<Integer>newArrayList();

        // 读取同步队列的内容 (共读取 5 个), 转存到集合中
        for (var i = 0; i < 5; i++) {
            elems.add(que.poll(1, TimeUnit.SECONDS));
            Thread.sleep(500);
        }

        // 确认出队元素
        then(elems).containsExactly(0, 1, 2, 3, 4);
    }

    /**
     * 将非线程安全队列包装为线程安全队列
     *
     * <p>
     * 通过 {@link Queues#synchronizedQueue(java.util.Queue) Queues.synchronizedQueue(Queue)}
     * 方法可以将一个非线程安全的队列对象包装为线程安全的队列对象
     * </p>
     *
     * <p>
     * 通过 {@link Queues#synchronizedDeque(java.util.Deque) Queues.synchronizedDeque(Deque)}
     * 方法可以将一个非线程安全的双端队列对象包装为线程安全的双端队列对象
     * </p>
     *
     * <p>
     * 线程安全的双端队列可以在多个线程之间进行操作且不会产生读写问题. 其内部是通过 {@code synchronized(obj)} 关键字来保证同步
     * </p>
     */
    @Test
    void synchronizedQueue_shouldWrapQueueObjectAsSynchronized() {
        // 将队列对象包装为线程安全的队列对象
        {
            var que = Queues.synchronizedQueue(new ArrayDeque<Integer>());

            // 在一个线程中进行入队操作
            new Thread(() -> {
                try {
                    for (var v : createRangedElements(0, 5, false)) {
                        Thread.sleep(200);
                        // 元素入队
                        then(que.offer(v)).isTrue();
                    }
                } catch (InterruptedException ignore) {}
            }).start();

            // 在另一个线程进行出队操作
            var elems = Lists.<Integer>newArrayList();
            for (var i = 0; i < 5; i++) {
                await().atMost(1, TimeUnit.SECONDS).until(() -> !que.isEmpty());
                elems.add(que.poll());
            }

            // 确认出队元素
            then(elems).containsExactly(0, 1, 2, 3, 4);
        }

        // 将双端队列对象包装为线程安全的双端队列对象
        {
            var que = Queues.synchronizedDeque(Lists.<Integer>newLinkedList());

            // 在一个线程中进行入队操作
            new Thread(() -> {
                try {
                    for (var v : createRangedElements(0, 5, false)) {
                        Thread.sleep(200);
                        // 元素入队
                        then(que.offer(v)).isTrue();
                    }
                } catch (InterruptedException ignore) {}
            }).start();

            // 在另一个线程进行出队操作
            var elems = Lists.<Integer>newArrayList();
            for (var i = 0; i < 5; i++) {
                await().atMost(1, TimeUnit.SECONDS).until(() -> !que.isEmpty());
                elems.add(que.poll());
            }

            // 确认出队元素
            then(elems).containsExactly(0, 1, 2, 3, 4);
        }
    }
}
