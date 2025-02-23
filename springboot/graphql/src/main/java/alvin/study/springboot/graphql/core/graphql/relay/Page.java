package alvin.study.springboot.graphql.core.graphql.relay;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;

/**
 * 兼容 Mybatis-Plus 分页插件的分页类型
 *
 * <p>
 * 本类型从 {@link IPage} 类型继承, 实现了 Mybatis-Plus 分页插件
 * </p>
 *
 * <p>
 * Mybatis-Plus 默认的分页类型
 * {@link com.baomidou.mybatisplus.extension.plugins.pagination.Page Page} 不支持
 * Graphql 的分页参数 (after/first, before/last 等)
 * </p>
 */
class Page<T> implements IPage<T> {
    /**
     * 总记录数
     */
    private long total = 0L;

    /**
     * 记录集合
     */
    private List<T> records = List.of();

    /**
     * 排序集合
     */
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * 每页记录数
     */
    private long size;

    /**
     * 当前记录偏移量
     */
    private long offset;

    /**
     * MappedStatement 的 id
     */
    private String countId;

    /**
     * 获取排序字段
     *
     * @return 排序字段
     */
    @Override
    public List<OrderItem> orders() {
        return orderItems;
    }

    /**
     * 添加新的排序条件，构造条件可以使用工厂：{@link OrderItem#build(String, boolean)}
     *
     * @param items 条件
     * @return 返回分页参数本身
     */
    public Page<T> addOrder(OrderItem... items) {
        orderItems.addAll(List.of(items));
        return this;
    }

    /**
     * 添加新的排序条件，构造条件可以使用工厂：{@link OrderItem#build(String, boolean)}
     *
     * @param items 条件
     * @return 返回分页参数本身
     */
    public Page<T> addOrder(List<OrderItem> items) {
        orderItems.addAll(items);
        return this;
    }

    /**
     * 获取记录集合
     */
    @Override
    public List<T> getRecords() { return records; }

    /**
     * 设置记录集合
     */
    @Override
    public Page<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    /**
     * 获取记录总数
     */
    @Override
    public long getTotal() { return total; }

    /**
     * 设置记录总数
     */
    @Override
    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    /**
     * 获取每页记录数
     */
    @Override
    public long getSize() { return size; }

    /**
     * 设置每页记录数
     */
    @Override
    public Page<T> setSize(long size) {
        this.size = size;
        return this;
    }

    /**
     * 获取当前页码
     */
    @Override
    public long getCurrent() {
        // 根据偏移量和每页记录数计算当前页码
        return offset / size + 1;
    }

    /**
     * 设置当前页码
     */
    @Override
    public Page<T> setCurrent(long current) {
        // 根据页码计算偏移量
        offset = Math.max(current - 1, 0) * size;
        return this;
    }

    /**
     * 获取当前记录的偏移量, 即当前第一条记录在总记录的位置索引
     */
    @Override
    public long offset() {
        return offset;
    }

    /**
     * 设置当前分页的记录偏移量
     *
     * @param offset 偏移量值
     * @return 当前对象
     */
    public Page<T> setOffset(long offset) {
        this.offset = offset;
        return this;
    }

    /**
     * 设置 MappedStatement 的 id
     *
     * @param countId id 值
     * @return 当前对象
     */
    public Page<T> setCountId(String countId) {
        this.countId = countId;
        return this;
    }

    /**
     * 获取 MappedStatement 的 id
     */
    @Override
    public String countId() {
        return this.countId;
    }
}
