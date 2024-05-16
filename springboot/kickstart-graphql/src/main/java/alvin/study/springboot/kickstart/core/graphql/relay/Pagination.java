package alvin.study.springboot.kickstart.core.graphql.relay;

import alvin.study.springboot.kickstart.core.exception.InputException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.google.common.base.Splitter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 辅助分页工具类
 */
@Getter
@Component
public class Pagination {
    // 默认每页记录数
    private int defaultPageSize;

    // 默认最大页数
    private int maxPageSize;

    /**
     * 构造器, 注入默认的参数
     *
     * @param defaultPageSize 默认每页记录数
     * @param maxPageSize     最大每页记录数, 分页结果不能超过该限定值
     */
    public Pagination(
        @Value("${spring.data.web.pageable.default-page-size:null}") Integer defaultPageSize,
        @Value("${spring.data.web.pageable.max-page-size:null}") Integer maxPageSize) {
        if (defaultPageSize != null && defaultPageSize > 0) {
            this.defaultPageSize = defaultPageSize;
        }
        if (maxPageSize != null && maxPageSize > 0) {
            this.maxPageSize = maxPageSize;
        }
    }

    /**
     * 实例化一个 {@link PageBuilder} 对象
     *
     * @param <T> 分页对应的数据类型
     * @return {@link PageBuilder} 对象
     */
    public <T> PageBuilder<T> newBuilder() {
        return new PageBuilder<>();
    }

    /**
     * 通过请求中包含的分页参数构建分页对象
     *
     * <p>
     * 本例中, 分页对象由 {@link IPage} 类型对象表示
     * </p>
     *
     * <p>
     * Relay 定义的分页参数包括
     * <ul>
     * <li>
     * {@code after}/{@code first}, 表示从 {@code after} 游标开始, 取之后 {@code first} 条记录
     * </li>
     * <li>
     * {@code before}/{@code last}, 表示从 {@code before} 游标开始, 取之前 {@code last} 条记录
     * </li>
     * <li>
     * {@code offset}/{@code limit}, 表示从 {@code offset} 位置开始, 取之后 {@code limit} 条记录,
     * 这组参数非标准的 Relay 定义参数, 但在实际中比较常用, 直接通过记录位置开始计算, 符合大多数分页场景
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 当然, 为了简单起见, 本例中的所有游标也都是通过数据的位置索引计算的, 除此方法外, 还可以通过 {@code id},
     * {@code timestamp} 等有序字段来计算游标
     * </p>
     */
    public final class PageBuilder<T> {
        private Integer first;
        private Integer after;
        private Integer last;
        private Integer before;
        private List<String> orders;

        public PageBuilder<T> withFirst(Integer first) {
            if (first != null) {
                this.first = first;
            }
            return this;
        }

        public PageBuilder<T> withLast(Integer last) {
            if (last != null) {
                this.last = last;
            }
            return this;
        }

        public PageBuilder<T> withOffset(Integer offset) {
            if (offset != null) {
                this.first = offset;
            }
            return this;
        }

        public PageBuilder<T> withCursor(String cursor) {
            if (cursor != null) {
                this.last = Cursors.parseCursor(cursor);
            }
            return this;
        }

        public PageBuilder<T> withBefore(String before) {
            if (before != null) {
                this.before = Cursors.parseCursor(before);
            }
            return this;
        }

        public PageBuilder<T> withAfter(String after) {
            if (after != null) {
                this.after = Cursors.parseCursor(after);
            }
            return this;
        }

        public PageBuilder<T> withOrder(String order) {
            if (order == null) {
                this.orders = List.of();
            } else {
                this.orders = Splitter.on(",").omitEmptyStrings().splitToList(order);
            }
            return this;
        }

        public PageBuilder<T> withLimit(Integer limit) {
            if (limit != null) {
                this.last = limit;
            }
            return this;
        }

        public PageBuilder<T> withQueryParams(@NotNull Map<String, Object> queryParams) {
            try {
                return withBefore((String) queryParams.get("before"))
                    .withAfter((String) queryParams.get("after"))
                    .withFirst((Integer) queryParams.get("first"))
                    .withLast((Integer) queryParams.get("last"))
                    .withOffset((Integer) queryParams.get("offset"))
                    .withLimit((Integer) queryParams.get("limit"))
                    .withOrder((String) queryParams.get("order"));
            } catch (ClassCastException e) {
                throw new InputException(e);
            }
        }

        /**
         * 根据 Relay 分页参数构建 {@link IPage} 分页对象
         *
         * @return {@link IPage} 分页对象
         */
        public IPage<T> build() {
            // 构建分页对象
            var page = relayToPage();

            // 添加排序属性
            if (orders != null) {
                page.addOrder(buildOrderItems());
            }
            return page;
        }

        /**
         * 将 Relay 分页参数转化为 Mybatis-Plus 分页对象
         *
         * <p>
         * 为了减少复杂性, 这里重新实现了 Mybatis-Plus 分页对象, 参考 {@link Page} 类型
         * </p>
         *
         * @return {@link Page} 类型 Mybatis-Plus 分页对象
         */
        private Page<T> relayToPage() {
            if (after == null && before == null) {
                after = 0;
            }

            if (after != null) {
                if (first == null) {
                    first = defaultPageSize;
                }
                // 返回 after/first 形成的分页对象
                return new Page<T>().setOffset(after).setSize(Math.min(first, maxPageSize));
            }

            // 确认 before 参数存在
            if (last == null) {
                last = defaultPageSize;
            }
            // 返回 before/last 形成的分页对象
            return new Page<T>().setOffset(Math.max(before - last, 0)).setSize(Math.min(last, maxPageSize));
        }

        /**
         * 构建排序字段集合
         *
         * <p>
         * 标准的 Relay 中, 排序定义是一个 JSON, 类似
         *
         * <pre>
         * {
         *   "order": {
         *     "field1": "DESC",
         *     "field3": "ASC",
         *     "field2": "DESC"
         *   }
         * }
         * </pre>
         * <p>
         * 本例中为了简化, 通过一个字符串表示排序信息, 例如: {@code "-field1,+field3,-field2"}
         * </p>
         *
         * @return Mybatis-Plus 排序元素集合
         */
        private List<OrderItem> buildOrderItems() {
            // 将排序字符串分割后转为 OrderItem 集合
            return orders.stream().map(s -> {
                    if (s.startsWith("-")) {
                        return OrderItem.desc(s.substring(1));
                    }
                    if (s.startsWith("+")) {
                        return OrderItem.asc(s.substring(1));
                    }
                    return OrderItem.asc(s);
                })
                .toList();
        }
    }
}
