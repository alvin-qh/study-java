package alvin.study.guava.graphs;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.ValueGraphBuilder;

/**
 * "图"结构数据源, 用于测试"图"类型和"网络"类型
 */
public class GraphsDatasource<N, E> {
    // 保存"边"的集合
    private final List<Edge<N, E>> edges;

    /**
     * 构造器, 通过"边"集合构造对象
     *
     * @param edges "边"集合
     */
    public GraphsDatasource(List<Edge<N, E>> edges) {
        this.edges = List.copyOf(edges);
    }

    /**
     * 构造器, 通过"边"参数数组构造对象
     *
     * @param edges edges "边"参数数组
     */
    @SafeVarargs
    public GraphsDatasource(Edge<N, E>... edges) {
        this.edges = List.of(edges);
    }

    /**
     * 构建图对象
     *
     * <p>
     * 图是由一组"节点"两两连接组成的数据结构, 如果两个节点相连接,
     * 则表示这两个节点间存在一条"边", 根据边是否具有指向性,
     * 图又分为 "有向图" 和 "无向图", 所以图可以由如下二元组集合表示
     * {@code [(节点1, 节点2), (节点2, 节点3), ..., (节点 n-1, 节点 n)]}
     * </p>
     *
     * <p>
     * 本方法通过 {@code directed} 参数是否为 {@code true}
     * 指定创建 "有向图" 或 "无向图", 其中:
     * <ul>
     * <li>
     * 有向图的边由 {@code EndpointPair.Ordered} 类型对象表示,
     * 在该类型中, {@code A → B} 和 {@code B → A} 被认为不相等
     * </li>
     * <li>
     * 无向图的边由 {@code EndpointPair.Unordered} 类型对象表示,
     * 在该类型中, {@code A → B} 和 {@code B → A} 被认为是相等的
     * </li>
     * </ul>
     * </p>
     *
     * @param directed          {@code true} 表示要创建"有向图",
     *                          否则创建"无向图"
     * @param nodeOrder         设置图中"节点"的迭代顺序,
     *                          即 {@link com.google.common.graph.Graph#nodes() Graph.nodes()}
     *                          方法返回的集合元素顺序, 参见 {@link ElementOrder} 类型,
     *                          默认为 {@link ElementOrder#unordered()}
     * @param incidentEdgeOrder 设置图中"边"迭代顺序, 即
     *                          {@link com.google.common.graph.Graph#edges() Graph.edges()}
     *                          方法返回的集合元素顺序, 只能取值为 {@link ElementOrder#unordered()}
     *                          (默认值) 以及 {@link ElementOrder#stable()}
     * @param allowsSelfLoops   是否允许节点自环
     * @return {@link MutableGraph} 对象, 表示一个图对象 (包括"有向图"和"无向图")
     */
    public MutableGraph<N> buildGraph(
            boolean directed,
            ElementOrder<N> nodeOrder,
            ElementOrder<N> incidentEdgeOrder,
            boolean allowsSelfLoops) {
        // 创建无向图
        var graph = (directed ? GraphBuilder.directed() : GraphBuilder.undirected())
                // 设置"节点"迭代顺序
                .nodeOrder(nodeOrder)
                // 设置"边"的迭代顺序
                .incidentEdgeOrder(incidentEdgeOrder)
                // 是否允许产生节点自环
                .allowsSelfLoops(allowsSelfLoops)
                .build();

        // 为无向图添加边
        for (var edge : edges) {
            graph.putEdge(edge.node1(), edge.node2());
        }
        return graph;
    }

    /**
     * 构建图对象
     *
     * <p>
     * 图是由一组"节点"两两连接组成的数据结构, 如果两个节点相连接, 则表示这两个节点间存在一条"边",
     * 根据边是否具有指向性, 图又分为"有向图"和 "无向图",
     * {@code [(节点1, 节点2), (节点2, 节点3), ..., (节点 n-1, 节点 n)]}
     * </p>
     *
     * <p>
     * 本方法通过 {@code directed} 参数是否为 {@code true} 指定创建"有向图"或"无向图",
     * 其中:
     * <ul>
     * <li>
     * 有向图的边由 {@code EndpointPair.Ordered} 类型对象表示, 在该类型中,
     * {@code A → B} 和 {@code B → A} 被认为不相等
     * </li>
     * <li>
     * 无向图的边由 {@code EndpointPair.Unordered} 类型对象表示, 在该类型中,
     * {@code A → B} 和 {@code B → A} 被认为是相等的
     * </li>
     * </ul>
     * </p>
     *
     * @param directed          {@code true} 表示要创建"有向图", 否则创建"无向图"
     * @param nodeOrder         设置图中"节点"的迭代顺序, 即
     *                          {@link com.google.common.graph.Graph#nodes() Graph.nodes()}
     *                          方法返回的集合元素顺序, 参见 {@link ElementOrder} 类型,
     *                          默认为 {@link ElementOrder#unordered()}
     * @param incidentEdgeOrder 设置图中"边"迭代顺序, 即
     *                          {@link com.google.common.graph.Graph#edges() Graph.edges()}
     *                          方法返回的集合元素顺序, 只能取值为 {@link ElementOrder#unordered()}
     *                          (默认值) 以及 {@link ElementOrder#stable()}
     * @return {@link MutableGraph} 对象, 表示一个图对象 (包括"有向图"和"无向图")
     */
    public MutableGraph<N> buildGraph(
            boolean directed,
            ElementOrder<N> nodeOrder,
            ElementOrder<N> incidentEdgeOrder) {
        return buildGraph(directed, nodeOrder, incidentEdgeOrder, false);
    }

    /**
     * 创建具备"边权重值"的图对象
     *
     * <p>
     * 为图的每一条"边"赋予"权重值", 权重值可以表示任何含意 (距离, 费用, 时间等等),
     * 所以具备边权重值的图可以由如下三元组集合表示:
     * {@code [(节点1, 节点2, 权重值), (节点2, 节点3, 权重值), ..., (节点 n-1, 节点 n, 权重值)]}
     * </p>
     *
     * @param directed          {@code true} 表示要创建"有向图", 否则创建"无向图"
     * @param nodeOrder         设置图中"节点"的迭代顺序, 即
     *                          {@link com.google.common.graph.ValueGraph#nodes() ValueGraph.nodes()}
     *                          方法返回的集合元素顺序, 参见 {@link ElementOrder} 类型,
     *                          默认为 {@link ElementOrder#unordered()}
     * @param incidentEdgeOrder 设置图中"边"迭代顺序, 即
     *                          {@link com.google.common.graph.ValueGraph#edges() ValueGraph.edges()}
     *                          方法返回的集合元素顺序, 只能取值为 {@link ElementOrder#unordered()}
     *                          (默认值) 以及 {@link ElementOrder#stable()}
     * @return {@link MutableValueGraph} 对象, 表示一个具有边权重的图对象 (包括"有向图"和"无向图")
     */
    public MutableValueGraph<N, E> buildValueGraph(
            boolean directed,
            ElementOrder<N> nodeOrder,
            ElementOrder<N> incidentEdgeOrder) {
        var graph = (directed ? ValueGraphBuilder.directed() : ValueGraphBuilder.undirected())
                .nodeOrder(nodeOrder)
                .incidentEdgeOrder(incidentEdgeOrder)
                .<N, E>build();

        for (var edge : edges) {
            graph.putEdgeValue(edge.node1(), edge.node2(), edge.weight());
        }
        return graph;
    }

    /**
     * 构建一个"网络"类型对象
     *
     * <p>
     * 连通的两个节点可以具备多条边 (平行边), 每个边由一个对象值表示,
     * 所以具备边权重值的图可以由如下三元组集合表示:
     * {@code [(节点1, 节点2, 边1), (节点1, 节点2, 边2), (节点2, 节点3, 边3), ..., (节点 m, 节点 n, 边4)]},
     * 可以看到, 相同的两个节点可以对应不同的边
     * </p>
     *
     * <p>
     * 注意, {@code Network} 和 {@code ValueGraph} 的边表示形式非常不同,
     * 前者的"边"由一个独立对象表示, 每两个节点间可以具备多个"边对象",
     * 后者的边是一个 {@link EndpointPair} 类型对象, 表示组成边的两个节点
     * </p>
     *
     * @param directed  {@code true} 表示要创建"有向图", 否则创建"无向图"
     * @param nodeOrder 设置"节点"的迭代顺序, 即
     *                  {@link com.google.common.graph.Network#nodes() Network.nodes()}
     *                  方法返回的集合元素顺序, 参见 {@link ElementOrder} 类型,
     *                  默认为 {@link ElementOrder#unordered()}
     * @param edgeOrder 设置图对象中"边"迭代顺序, 即
     *                  {@link com.google.common.graph.Network#edges() Network.edges()}
     *                  方法返回的集合元素顺序, 参见 {@link ElementOrder} 接口类型,
     *                  默认为 {@link ElementOrder#unordered()}
     * @return {@link MutableNetwork} 对象, 表示一个网络对象 (包括"有向网络"和"无向网络")
     */
    public MutableNetwork<N, E> buildNetwork(
            boolean directed,
            ElementOrder<N> nodeOrder,
            ElementOrder<E> edgeOrder) {
        var network = (directed ? NetworkBuilder.directed() : NetworkBuilder.undirected())
                .nodeOrder(nodeOrder)
                .edgeOrder(edgeOrder)
                // 允许平行边
                .allowsParallelEdges(true)
                .build();

        for (var edge : edges) {
            network.addEdge(edge.node1(), edge.node2(), edge.weight());
        }
        return network;
    }

    /**
     * 获取原数据中的节点集合
     *
     * @return 原数据中的节点集合
     */
    public Set<N> nodes() {
        return edges.stream().flatMap(
            e -> Stream.of(e.node1(), e.node2())).collect(Collectors.toSet());
    }

    /**
     * 以"有方向"模式获取原数据中的"边"集合
     *
     * @param inverse 反转"边"中的"原"和"目标"节点,
     *                即 {@code nodeU} 和 {@code nodeV} 节点
     * @return 原数据中的"边"集合
     */
    public List<EndpointPair<N>> orderedEdges(boolean inverse) {
        var stream = inverse ? edges.stream().map(e -> EndpointPair.ordered(e.node2(), e.node1()))
                             : edges.stream().map(e -> EndpointPair.ordered(e.node1(), e.node2()));
        return stream.toList();
    }

    /**
     * 以"无方向"模式获取原数据中的"边"集合
     *
     * @param inverse 反转"边"中的"原"和"目标"节点,
     *                即 {@code nodeU} 和 {@code nodeV} 节点
     * @return 原数据中的"边"集合
     */
    public List<EndpointPair<N>> unOrderedEdges(boolean inverse) {
        var stream = inverse ? edges.stream().map(e -> EndpointPair.unordered(e.node2(), e.node1()))
                             : edges.stream().map(e -> EndpointPair.unordered(e.node1(), e.node2()));
        return stream.toList();
    }

    /**
     * 获取原数据中指定节点的"相邻节点"
     *
     * <p>
     * 所谓原数据相邻节点, 即若"边"数组中, 如果有一个节点值和所给节点值相等,
     * 则另一个节点值即为所给节点的"相邻节点"
     * </p>
     *
     * @param node 指定节点值
     * @return 指定节点相邻节点值的集合
     */
    public Set<N> neighbors(N node) {
        var result = new LinkedHashSet<N>();

        // 遍历原数据中的所有边
        for (var edge : edges) {
            // 如果所给节点和边中的某个节点相等, 则记录另一个节点为相邻节点
            if (edge.node2().equals(node)) {
                result.add(edge.node1());
            } else if (edge.node1().equals(node)) {
                result.add(edge.node2());
            }
        }
        return result;
    }

    /**
     * 获取原数据中指定节点的"前序相邻节点"
     *
     * <p>
     * 所谓原数据相邻节点, 即若"边"数组中, 如果第二个节点值和所给节点值相等,
     * 则第一个节点值即为所给节点的"前序相邻节点"
     * </p>
     *
     * @param node 指定节点值
     * @return 指定节点相邻节点值的集合
     */
    public Set<N> preNeighbors(N node) {
        var result = new LinkedHashSet<N>();

        for (var edge : edges) {
            if (edge.node2.equals(node)) {
                result.add(edge.node1());
            }
        }
        return result;
    }

    /**
     * 获取原数据中指定节点的"后续相邻节点"
     *
     * <p>
     * 所谓原数据相邻节点, 即若"边"数组中, 如果第一个节点值和所给节点值相等,
     * 则第二个节点值即为所给节点的"后续相邻节点"
     * </p>
     *
     * @param node 指定节点值
     * @return 指定节点相邻节点值的集合
     */
    public Set<N> postNeighbors(N node) {
        var result = new LinkedHashSet<N>();

        for (var edge : edges) {
            if (edge.node1().equals(node)) {
                result.add(edge.node2());
            }
        }
        return result;
    }

    /**
     * 定义"图"的"边"数据的类型
     *
     * <p>
     * 每个"边"数据由两个节点值及边权重值组成
     * </p>
     *
     * <p>
     * 对于构建无权重图 ({@link com.google.common.graph.Graph Graph}) 类型来说,
     * 只有两个节点值有效, 边权重值的类型总是为 {@link Void} 且其值为 {@code null}
     * </p>
     *
     * <p>
     * 对于构建有权重图 ({@link com.google.common.graph.ValueGraph ValueGraph})
     * 和网络 ({@link com.google.common.graph.Network Network}) 来说,
     * 需要同时包括两个节点值和边权重值
     * </p>
     */
    public static class Edge<N, E> {
        // 节点 1
        private final N node1;
        // 节点 2
        private final N node2;
        // 节点值
        private final E weight;

        /**
         * 构造器, 用于构造一个边数据对象
         *
         * @param node1  节点 1 对象引用
         * @param node2  节点 2 对象引用
         * @param weight 边权重
         */
        private Edge(N node1, N node2, E weight) {
            this.node1 = node1;
            this.node2 = node2;
            this.weight = weight;
        }

        /**
         * 通过两个节点值来构建边数据对象
         *
         * @param <N>   节点值类型
         * @param node1 节点 1 的值
         * @param node2 节点 2 的值
         * @return 边数据对象
         */
        public static <N> Edge<N, Void> of(N node1, N node2) {
            return new Edge<>(node1, node2, null);
        }

        /**
         * 通过两个节点值来构建边数据对象
         *
         * @param <N>   节点值类型
         * @param <E>   边权重值类型
         * @param node1 节点 1 的值
         * @param node2 节点 2 的值
         * @param value 边的值
         * @return 边数据对象
         */
        public static <N, E> Edge<N, E> of(N node1, N node2, E value) {
            return new Edge<>(node1, node2, value);
        }

        /**
         * 获取边中节点 1 的值
         *
         * @return 节点 1 的值
         */
        public N node1() {
            return node1;
        }

        /**
         * 获取边中节点 2 的值
         *
         * @return 节点 2 的值
         */
        public N node2() {
            return node2;
        }

        /**
         * 获取边的权重值
         *
         * @return 边的权重值
         */
        public E weight() {
            return weight;
        }
    }
}
