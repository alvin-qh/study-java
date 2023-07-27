package alvin.study.se.collection;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试集合迭代器对象
 *
 * <p>
 * 任何一个可迭代集合 {@link Iterable} 对象都具备 {@link Iterable#iterator()} 方法, 返回一个 {@link java.util.Iterator Iterator}
 * 类型对象, 通过该对象可对集合进行逐元素迭代
 * </p>
 *
 * <p>
 * 迭代器具备一种称为"快速失败"的特性, 即迭代器在迭代过程中, 一旦被迭代的集合被修改, 则迭代器立即抛出
 * {@link java.util.ConcurrentModificationException ConcurrentModificationException} 异常, 除非集合对象本身支持"并发访问",
 * 包括: {@link java.util.concurrent.CopyOnWriteArrayList CopyOnWriteArrayList},
 * {@link java.util.concurrent.ConcurrentHashMap ConcurrentHashMap}, {@link java.util.concurrent.LinkedBlockingQueue
 * LinkedBlockingQueue}, {@link java.util.concurrent.PriorityBlockingQueue PriorityBlockingQueue},
 * {@link java.util.concurrent.ConcurrentLinkedQueue ConcurrentLinkedQueue} 等
 * </p>
 */
class IteratorTest {
    /**
     * 简单类型迭代器
     *
     * <p>
     * 从 JDK 8 开始, 增加了针对简单类型的迭代器类型, 包括:
     * <ul>
     * <li>
     * {@link java.util.PrimitiveIterator.OfInt OfInt}, 通过 {@link java.util.PrimitiveIterator.OfInt#nextInt()
     * OfInt.nextInt()} 方法返回每次迭代的 {@code int} 值, 通过
     * {@link java.util.PrimitiveIterator.OfInt#forEachRemaining(IntConsumer) OfInt.forEachRemaining(IntConsumer)}
     * 方法对集合中的 {@code int} 值进行遍历
     * </li>
     * <li>
     * {@link java.util.PrimitiveIterator.OfLong OfLong}, 通过 {@link java.util.PrimitiveIterator.OfLong#nextLong()
     * OfLong.nextLong()} 方法返回每次迭代的 {@code long} 值, 通过
     * {@link java.util.PrimitiveIterator.OfLong#forEachRemaining(java.util.function.LongConsumer)
     * OfLong.forEachRemaining(LongConsumer)} 方法对集合中的 {@code long} 值进行遍历
     * </li>
     * <li>
     * {@link java.util.PrimitiveIterator.OfDouble OfDouble}, 通过
     * {@link java.util.PrimitiveIterator.OfDouble#nextDouble() OfDouble.nextDouble()} 方法返回每次迭代的 {@code double} 值,
     * 通过 {@link java.util.PrimitiveIterator.OfDouble#forEachRemaining(java.util.function.DoubleConsumer)
     * OfLong.forEachRemaining(DoubleConsumer)} 方法对集合中的 {@code double} 值进行遍历
     * </li>
     * </ul>
     * 使用这些针对于简单类型的 {@link java.util.Iterator Iterator}, 目的是减少在运算过程中频繁的装箱和拆箱操作, 对于大量的简单类型数据,
     * 可以用类似方法进行处理
     * </p>
     */
    @Test
    void primitive_shouldCreatePrimitiveSpliterator() {
        // 获取 OfInt 迭代器
        var ints = IntStream.range(0, 100).iterator();
        // 对 OfInt 迭代器进行迭代, 通过 nextInt 方法获取 int 类型值
        while (ints.hasNext()) {
            then(ints.nextInt()).isGreaterThanOrEqualTo(0).isLessThan(100);
        }

        // 获取 OfDouble 迭代器
        var doubles = DoubleStream.of(1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7).iterator();
        // 通过 DoubleConsumer 对迭代器进行遍历
        doubles.forEachRemaining((DoubleConsumer) d -> then(d).isGreaterThanOrEqualTo(1.1).isLessThan(1.8));
    }
}
