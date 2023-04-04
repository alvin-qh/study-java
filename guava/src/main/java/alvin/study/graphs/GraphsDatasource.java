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
    private final List<T[]> edges;

    public GraphsDatasource(List<T[]> edges) {
        this.edges = List.copyOf(edges);
    }

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

    public Set<T> nodes() {
        return edges.stream()
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
    }

    public List<EndpointPair<T>> orderedEdges(boolean inverse) {
        var stream = inverse ? edges.stream().map(edge -> EndpointPair.ordered(edge[1], edge[0]))
                             : edges.stream().map(edge -> EndpointPair.ordered(edge[0], edge[1]));
        return stream.toList();
    }

    public List<EndpointPair<T>> unOrderedEdges(boolean inverse) {
        var stream = inverse ? edges.stream().map(edge -> EndpointPair.unordered(edge[1], edge[0]))
                             : edges.stream().map(edge -> EndpointPair.unordered(edge[0], edge[1]));
        return stream.toList();
    }

    public Set<T> neighbors(T node) {
        var result = new LinkedHashSet<T>();

        for (var edge : edges) {
            if (edge[1] == node) {
                result.add(edge[0]);
            } else if (edge[0] == node) {
                result.add(edge[1]);
            }
        }
        return result;
    }

    public Set<T> preNeighbors(T node) {
        var result = new LinkedHashSet<T>();

        for (var edge : edges) {
            if (edge[1] == node) {
                result.add(edge[0]);
            }
        }
        return result;
    }

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
