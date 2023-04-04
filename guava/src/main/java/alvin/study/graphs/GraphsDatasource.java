package alvin.study.graphs;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

/**
 * "图"结构数据源, 用于测试 {@link MutableGraph} 类型
 */
public class GraphsDatasource<T> {
    // 保存"边"的集合
    private final List<T[]> edges;

    /**
     * 构造器, 通过"边"集合构造对象
     *
     * @param edges "边"集合
     */
    public GraphsDatasource(List<T[]> edges) {
        this.edges = List.copyOf(edges);
    }

    /**
     * 构造器, 通过"边"参数数组构造对象
     *
     * @param edges edges "边"参数数组
     */
    @SafeVarargs
    public GraphsDatasource(T[]... edges) {
        this.edges = List.of(edges);
    }

    /**
     * 构建无向图
     *
     * <p>
     * 无向图的边由 {@link EndpointPair.Unordered} 类型对象表示, 在该类型中, {@code A → B} 和 {@code B → A} 被认为是相等的
     * </p>
     *
     * @param order 设置节点的遍历顺序, 即 {@link MutableGraph#nodes()} 返回的集合元素顺序, 参见 {@link ElementOrder} 接口类型
     * @return {@link MutableGraph} 对象, 表示一个无向图
     */
    public MutableGraph<T> buildUndirected(ElementOrder<T> order) {
        // 创建无向图
        var graph = GraphBuilder.undirected()
                // 设置节点迭代顺序
                .nodeOrder(order)
                .<T>build();

        // 为无向图添加边
        for (var edge : edges) {
            graph.putEdge(edge[0], edge[1]);
        }
        return graph;
    }

    /**
     * 构建有向图
     *
     * <p>
     * 有向图的边由 {@link EndpointPair.Ordered} 类型对象表示, 在该类型中, {@code A → B} 和 {@code B → A} 被认为不相等
     * </p>
     *
     * @param order 设置节点的遍历顺序, 即 {@link MutableGraph#nodes()} 返回的集合元素顺序, 参见 {@link ElementOrder} 接口类型
     * @return {@link MutableGraph} 对象, 表示一个有向图
     */
    public MutableGraph<T> buildDirected(ElementOrder<T> order) {
        // 创建有向图
        var graph = GraphBuilder.directed()
                // 设置节点迭代顺序
                .nodeOrder(order)
                .<T>build();

        // 为有向图添加边
        for (var edge : edges) {
            graph.putEdge(edge[0], edge[1]);
        }
        return graph;
    }

    /**
     * 获取原数据中的节点集合
     *
     * @return 原数据中的节点集合
     */
    public Set<T> nodes() {
        return edges.stream().flatMap(Arrays::stream).collect(Collectors.toSet());
    }

    /**
     * 以"有方向"模式获取原数据中的"边"集合
     *
     * @param inverse 反转"边"中的"原"和"目标"节点, 即 {@code nodeU} 和 {@code nodeV} 节点
     * @return 原数据中的"边"集合
     */
    public List<EndpointPair<T>> orderedEdges(boolean inverse) {
        var stream = inverse ? edges.stream().map(edge -> EndpointPair.ordered(edge[1], edge[0]))
                             : edges.stream().map(edge -> EndpointPair.ordered(edge[0], edge[1]));
        return stream.toList();
    }

    /**
     * 以"无方向"模式获取原数据中的"边"集合
     *
     * @param inverse 反转"边"中的"原"和"目标"节点, 即 {@code nodeU} 和 {@code nodeV} 节点
     * @return 原数据中的"边"集合
     */
    public List<EndpointPair<T>> unOrderedEdges(boolean inverse) {
        var stream = inverse ? edges.stream().map(edge -> EndpointPair.unordered(edge[1], edge[0]))
                             : edges.stream().map(edge -> EndpointPair.unordered(edge[0], edge[1]));
        return stream.toList();
    }

    /**
     * 获取原数据中指定节点的"相邻节点"
     *
     * <p>
     * 所谓原数据相邻节点, 即若"边"数组中, 如果有一个节点值和所给节点值相等, 则另一个节点值即为所给节点的"相邻节点"
     * </p>
     *
     * @param node 指定节点值
     * @return 指定节点相邻节点值的集合
     */
    public Set<T> neighbors(T node) {
        var result = new LinkedHashSet<T>();

        // 遍历原数据中的所有边
        for (var edge : edges) {
            // 如果所给节点和边中的某个节点相等, 则记录另一个节点为相邻节点
            if (edge[1] == node) {
                result.add(edge[0]);
            } else if (edge[0] == node) {
                result.add(edge[1]);
            }
        }
        return result;
    }

    /**
     * 获取原数据中指定节点的"前序相邻节点"
     *
     * <p>
     * 所谓原数据相邻节点, 即若"边"数组中, 如果第二个节点值和所给节点值相等, 则第一个节点值即为所给节点的"前序相邻节点"
     * </p>
     *
     * @param node 指定节点值
     * @return 指定节点相邻节点值的集合
     */
    public Set<T> preNeighbors(T node) {
        var result = new LinkedHashSet<T>();

        for (var edge : edges) {
            if (edge[1] == node) {
                result.add(edge[0]);
            }
        }
        return result;
    }

    /**
     * 获取原数据中指定节点的"后续相邻节点"
     *
     * <p>
     * 所谓原数据相邻节点, 即若"边"数组中, 如果第一个节点值和所给节点值相等, 则第二个节点值即为所给节点的"后续相邻节点"
     * </p>
     *
     * @param node 指定节点值
     * @return 指定节点相邻节点值的集合
     */
    public Set<T> postNeighbors(T node) {
        var result = new LinkedHashSet<T>();

        for (var edge : edges) {
            if (edge[0] == node) {
                result.add(edge[1]);
            }
        }
        return result;
    }
}
