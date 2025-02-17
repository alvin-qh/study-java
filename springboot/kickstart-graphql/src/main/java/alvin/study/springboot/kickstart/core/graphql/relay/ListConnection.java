package alvin.study.springboot.kickstart.core.graphql.relay;

import graphql.relay.DefaultEdge;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.Function;

/**
 * 表示通过列表集合承载的 {@link Connection} 对象
 *
 * <p>
 * 一个 {@link Connection} 对象标识一个可分页的集合, 包括如下几个要素
 * <ul>
 * <li>
 * {@link Edge} 集合, 每个 {@link Edge} 元素表示一个承载所查询对象信息. {@link Edge} 中包含一个
 * {@code node} 属性表示所查询对象的信息, 一个 {@code cursor} 属性表示所查询对象的位置游标
 * </li>
 * <li>
 * {@link PageInfo} 对象, 表示本次查询的分页信息, 包括 {@code startCursor}, {@code endCursor},
 * {@code hasPreviousPage} 和 {@code hasNextPage} 属性, 分别表示本次查询结果集合的 "第一条记录游标",
 * "最后一条记录游标", "是否具备上一页" 和 "是否具备下一页"
 * </li>
 * <li>
 * {@code totalCount} 属性, 表示总记录数. 标准的 Relay Connection 中不包含此属性, 是本例中特别添加的
 * </li>
 * </ul>
 * </p>
 *
 * @see ConnectionBuilder
 */
@ToString
@RequiredArgsConstructor
public class ListConnection<T> implements Connection<T> {
    // 表示具体负载内容的集合
    private final List<Edge<T>> edges;

    // 表示分页信息对象
    private final PageInfo pageInfo;

    // 表示总记录数
    private final int totalCount;

    /**
     * 获取 {@link Edge} 对象的 {@link List} 集合
     */
    @Override
    public List<Edge<T>> getEdges() { return edges; }

    /**
     * 获取 {@link PageInfo} 分页信息对象
     */
    @Override
    public PageInfo getPageInfo() { return pageInfo; }

    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    @Override
    public int getTotalCount() { return totalCount; }

    /**
     * 对负载内容进行类型转换, 得到一个新的 {@link Connection} 对象
     *
     * @param <R>    要转换为的负载对象类型
     * @param mapper 转换函数
     * @return 转换负载对象类型后的 {@link ListConnection} 对象
     */
    public <R> ListConnection<R> mapTo(Function<T, R> mapper) {
        var newEdges = edges.stream()
                .map(edge -> (Edge<R>) new DefaultEdge<>(mapper.apply(edge.getNode()), edge.getCursor()))
                .toList();
        return new ListConnection<>(newEdges, pageInfo, totalCount);
    }
}
