package alvin.study.guava.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingListIterator;

/**
 * 代理 {@link ArrayList} 类型的类型
 */
public class StringList extends ForwardingList<String> {
    // 设置被代理的对象
    private final List<String> delegatedList = new ArrayList<>();

    /**
     * 获取被代理的对象
     *
     * @return 被代理的 {@link List} 类型对象
     */
    @Override
    protected List<String> delegate() {
        return delegatedList;
    }

    /**
     * 重写 {@link List#add(Object)} 方法
     *
     * <p>
     * 对于值为空字符串或 {@code null} 的字符串值参数, 本方法会抛出异常
     * </p>
     *
     * @param str 要添加的元素
     * @return 如果添加成功则返回 {@code true}, 反之返回 {@code false}
     * @throws IllegalArgumentException 如果 {@code element} 参数为 {@code null} 或空字符串时,
     *                                  抛出该异常
     */
    @Override
    public boolean add(String str) {
        if (Strings.isNullOrEmpty(str)) {
            throw new IllegalArgumentException("str argument must not null or empty string");
        }
        return delegatedList.add(str);
    }

    /**
     * 重写 {@link List#addAll(Collection)} 方法
     *
     * <p>
     * 对于值为空字符串或 {@code null} 的字符串值参数, 本方法会抛出异常
     * </p>
     *
     * @param collection 要添加的元素集合
     * @return 如果添加成功则返回 {@code true}, 反之返回 {@code false}
     * @throws IllegalArgumentException 如果 {@code collection} 参数中包含值为空字符串或
     *                                  {@code null} 的元素 时, 抛出该异常
     */
    @Override
    public boolean addAll(Collection<? extends String> collection) {
        Preconditions.checkNotNull(collection).forEach(v -> {
            if (Strings.isNullOrEmpty(v)) {
                throw new IllegalArgumentException("collection argument cannot contain null or empty string");
            }
        });

        return delegatedList.addAll(collection);
    }

    /**
     * 通过 {@link ForwardingIterator} 接口代理 {@link #delegatedList} 对象的
     * {@link List#iterator()} 方法返回值
     *
     * @return {@link ForwardingIterator} 类型代理对象
     */
    @Override
    public Iterator<String> iterator() {
        // 通过匿名类创建对象并设置被代理对象
        return new ForwardingIterator<>() {
            // 设置要代理的迭代器对象
            private final Iterator<String> delegatedIterator = StringList.this.delegatedList.iterator();

            /**
             * 获取被代理的迭代器对象
             *
             * @return 被代理的迭代器对象, 即 {@link #delegatedIterator} 字段的值
             */
            @Override
            protected Iterator<String> delegate() {
                return delegatedIterator;
            }
        };
    }

    /**
     * 通过 {@link ForwardingListIterator} 接口代理 {@link #delegatedList} 对象的
     * {@link List#listIterator()} 方法返回值
     *
     * @return {@link ForwardingListIterator} 类型代理对象
     */
    @Override
    public ListIterator<String> listIterator() {
        // 通过匿名类创建对象并设置被代理对象
        return new ForwardingListIterator<>() {
            // 设置要代理的迭代器对象
            private final ListIterator<String> delegatedIterator = StringList.this.delegatedList.listIterator();

            /**
             * 获取代理的迭代器对象
             *
             * @return 被代理的迭代器对象, 即 {@link #delegatedIterator} 字段的值
             */
            @Override
            protected ListIterator<String> delegate() {
                return delegatedIterator;
            }
        };
    }
}
