package alvin.study.guava.collect;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Iterator;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Guava 提供了两种特殊迭代器类型, 用于在此基础上定义特殊迭代器
 *
 * <p>
 * {@link AbstractIterator} 类型为一个抽象超类, 包含了 {@link java.util.Iterator Iterator} 接口的大部分方法实现, 可以协助扩展
 * {@link java.util.Iterator Iterator} 类型
 * </p>
 *
 * <p>
 * {@link com.google.common.collect.PeekingIterator PeekingIterator} 类型迭代器支持 {@code peek} 操作, 即获取当前迭代值, 且
 * 不对迭代器进行迭代 (即不移动迭代器的元素指针), 适合要重复获取当前迭代元素的情况
 * </p>
 */
class IteratorExtensionTest {
    /**
     * 测试通过 {@link AbstractIterator} 类型对迭代器类型进行扩展
     *
     * <p>
     * 通过 {@link AbstractIterator} 类型扩展 {@link java.util.Iterator Iterator}, 只需重写一个
     * {@code AbstractIterator.computeNext()} 方法即可
     * </p>
     *
     * <p>
     * {@link AbstractIterator#next()} 方法和 {@link AbstractIterator#hasNext()} 方法均会调用
     * {@code AbstractIterator.computeNext()} 方法, 如果迭代器有下一个元素, 则 {@code AbstractIterator.computeNext()} 方法
     * 返回元素值, 否则返回 {@code AbstractIterator.endOfData()} 结果表示迭代完成
     * </p>
     *
     * <p>
     * 本例演示了通过代理 {@link java.util.Iterator Iterator} 类型对象, 产生一个会过滤掉偶数元素的迭代器对象
     * </p>
     */
    @Test
    void abstract_shouldExtendIteratorType() {
        // 源集合对象, 元素必须有序
        var src = ImmutableList.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);

        // 通过匿名类创建一个 AbstractIterator 类型迭代器对象
        var iter = new AbstractIterator<Integer>() {
            // 被代理的迭代器对象
            private final Iterator<Integer> delegate = src.iterator();

            /**
             * 计算当前迭代器对象的下一个元素值
             *
             * @return 如果下一个元素存在, 则返回元素值, 且迭代器向前迭代一次, 否则返回 {@link #endOfData()} 结果表示迭代结束
             */
            @Override
            protected Integer computeNext() {
                if (!delegate.hasNext()) {
                    // 如果没有下一个元素, 则迭代结束
                    return endOfData();
                }

                // 获取下一个元素
                var elem = delegate.next();
                while (Objects.requireNonNull(elem) % 2 == 0) {
                    // 如果元素是偶数, 则继续迭代, 直到得到一个奇数或者迭代结束
                    elem = delegate.hasNext() ? delegate.next() : endOfData();
                }
                return elem;
            }
        };

        // 确认集合对象的元素不包括偶数
        then(ImmutableList.copyOf(iter)).containsExactly(1, 1, 3, 3, 5, 5);
    }

    /**
     * 测试通过 {@link com.google.common.collect.PeekingIterator PeekingIterator} 类型实现一个支持 {@code peek} 操作的迭代器
     *
     * <p>
     * 下面的例子中, 通过遍历迭代器, 且跳过重复元素, 将一个集合的元素不重复的复制到另一个集合对象中
     * </p>
     */
    @Test
    void peeking_shouldWrapIteratorAsPeekingIterator() {
        // 源集合对象, 元素必须有序
        var src = ImmutableList.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
        // 目标集合对象
        var dst = Lists.newArrayList();

        // 将迭代器包装为 PeekingIterator 对象
        var srcIter = Iterators.peekingIterator(src.iterator());
        while (srcIter.hasNext()) {
            // 获取迭代器的当前元素
            var current = srcIter.next();

            // 如果当前元素和下一个元素相等, 则继续循环
            while (srcIter.hasNext() && srcIter.peek().equals(current)) {
                // 跳过重复元素
                srcIter.next();
            }

            // 将当前元素添加到目标集合
            dst.add(current);
        }

        // 确认目标集合中不包含重复项
        then(dst).containsExactly(1, 2, 3, 4, 5);
    }
}
