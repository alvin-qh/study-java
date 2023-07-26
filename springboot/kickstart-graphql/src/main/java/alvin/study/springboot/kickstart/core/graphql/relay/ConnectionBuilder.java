package alvin.study.springboot.kickstart.core.graphql.relay;

import com.baomidou.mybatisplus.core.metadata.IPage;
import graphql.relay.DefaultEdge;
import graphql.relay.DefaultPageInfo;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

/**
 * 创建 {@link ListConnection} 对象
 *
 * <p>
 * Relay 中定义的 {@link graphql.relay.Connection Connection} 是一个承载了若干 {@link Edge}
 * 对象和分页信息的对象, 而本类型表示 {@link Edge} 对象通过 {@link List} 集合对象来组织
 * </p>
 */
@Slf4j
public final class ConnectionBuilder {
    /**
     * 私有构造器, 禁止创建对象
     */
    private ConnectionBuilder() {
    }

    /**
     * 构建 {@link ListConnection} 对象
     *
     * @param <T>  {@link ListConnection} 负载对象的类型
     * @param page Mybatis 分页对象
     * @return {@link ListConnection} 对象
     */
    @Contract("_ -> new")
    public static <T> @NotNull ListConnection<T> build(IPage<T> page) {
        log.debug("Build graphql connection with: startPos={}, pageSize={}, dataCount={} and totalCount={}",
            page.offset(), page.getSize(), page.getRecords().size(), page.getTotal());

        // 创建 ListConnection 负载的 Edge 对象
        var edges = buildEdges(page);

        // 创建 ListConnection 负载的 Edge 对象
        var pageInfo = buildPageInfo(page);

        // 创建 ListConnection 对象
        return new ListConnection<>(edges, pageInfo, (int) page.getTotal());
    }

    /**
     * 创建 {@link Edge} 对象集合
     *
     * <p>
     * 本例中使用了 {@link DefaultEdge} 作为 {@link Edge} 的实现类型, 包含一个 {@code node}
     * 属性表示承载的实际对象, 一个 {@code cursor} 表示相关记录的位置游标
     * </p>
     *
     * @param <T>  {@link Edge} 承载的对象类型
     * @param page Mybatis 分页对象
     * @return {@link Edge} 对象的 {@link List} 集合
     */
    private static <T> List<Edge<T>> buildEdges(@NotNull IPage<T> page) {
        // 如果查询结果总数量为 0, 则返回空集合
        if (page.getTotal() == 0) {
            return List.of();
        }

        // 获取查询记录
        var records = page.getRecords();

        // 获取查询结果的偏移量值
        var offset = (int) page.offset();

        // 创建 Edge 集合
        return IntStream.range(0, page.getRecords().size())
            .mapToObj(n -> (Edge<T>) new DefaultEdge<>(records.get(n), Cursors.makeConnCursor(offset + n)))
            .toList();
    }

    /**
     * 构建 {@link PageInfo} 对象
     *
     * <p>
     * {@link PageInfo} 对象表示分页信息, 包括: 表示本次查询结果中第一条记录游标的
     * {@link PageInfo#getStartCursor()} 属性, 表示本次查询结果中最后一条记录游标的
     * {@link PageInfo#getEndCursor()} 属性, 表示是否具有上一页的
     * {@link PageInfo#isHasPreviousPage()} 属性以及表示是否具备下一页的
     * {@link PageInfo#isHasNextPage()} 属性
     * </p>
     *
     * @param page Mybatis 分页对象
     * @return {@link PageInfo} 对象
     */
    @Contract("_ -> new")
    private static @NotNull PageInfo buildPageInfo(@NotNull IPage<?> page) {
        //
        return new DefaultPageInfo(
            Cursors.makeConnCursor((int) page.offset()),
            Cursors.makeConnCursor((int) page.offset() + page.getRecords().size()),
            page.getCurrent() > 1,
            page.getCurrent() < page.getPages());
    }
}
