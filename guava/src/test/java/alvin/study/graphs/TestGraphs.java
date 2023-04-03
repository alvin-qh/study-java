package alvin.study.graphs;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

/**
 * 测试 Guava 的图数据结构类
 *
 * <p>
 * Guava 提供了 {@link MutableGraph} 集合类型, 用于构建"图"数据结构, 通过该数据类型, 可以方便的获取"图"数据结构的相关结构化数据,
 * 例如: "节点", "边", "前趋", "邻接表"
 * </p>
 *
 * <p>
 * 本例中的所有操作都通过如下图表示的图结构演示:
 * <img src="assets/undirected_graph.png"/>
 * </p>
 */
class TestGraphs {
    // 边集合列表, 图的每个边由相连的两个节点组成
    private List<int[]> edges = List.of(
        new int[] { 1, 2 },
        new int[] { 2, 3 },
        new int[] { 2, 4 },
        new int[] { 2, 5 },
        new int[] { 2, 7 },
        new int[] { 3, 8 },
        new int[] { 4, 5 },
        new int[] { 4, 6 },
        new int[] { 4, 8 },
        new int[] { 5, 6 },
        new int[] { 1, 2 },
        new int[] { 6, 7 },
        new int[] { 7, 8 },
        new int[] { 8, 9 },
        new int[] { 9, 1 });

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
    private MutableGraph<Integer> buildUndirected(ElementOrder<Integer> order) {
        // 创建无向图
        var graph = GraphBuilder.undirected()
                // 设置节点迭代顺序
                .nodeOrder(order)
                .<Integer>build();

        then(graph.isDirected()).isFalse();

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
    private MutableGraph<Integer> buildDirected(ElementOrder<Integer> order) {
        // 创建有向图
        var graph = GraphBuilder.directed()
                // 设置节点迭代顺序
                .nodeOrder(order)
                .<Integer>build();

        then(graph.isDirected()).isTrue();

        // 为有向图添加边
        for (var edge : edges) {
            graph.putEdge(edge[0], edge[1]);
        }
        return graph;
    }

    /**
     * 测试构建无向图
     *
     * <p>
     * 通过 {@link #buildUndirected(ElementOrder)} 方法可以构建一个表示无向图的 {@link MutableGraph} 对象
     * </p>
     *
     * <p>
     * 无向图的边是通过 {@link EndpointPair.Unordered} 类型对象表示, 通过 {@link EndpointPair#unordered(Object, Object)
     * EndpointPair.unordered(T, T)} 方法创建
     * </p>
     *
     * <p>
     * 通过 {@link MutableGraph#nodes()} 方法可以获取图的节点对象集合
     * </p>
     *
     * <p>
     * 通过 {@link MutableGraph#edges()} 方法可以获取无向图的边对象集合, 是一个元素类型为 {@link EndpointPair.Unordered} 的
     * {@code Set} 集合
     * </p>
     */
    @Test
    void build_shouldBuildUndirectedGraphs() {
        // 构建无向图
        var graph = buildUndirected(ElementOrder.insertion());

        // 确认无向图包含的节点
        then(graph.nodes()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 7, 8, 6, 9);

        // 确认无向图包含的边, 无向图的边由 EndpointPair.Unordered 类型对象表示
        then(graph.edges()).containsExactlyInAnyOrder(
            EndpointPair.unordered(1, 2),
            EndpointPair.unordered(2, 3),
            EndpointPair.unordered(2, 4),
            EndpointPair.unordered(2, 5),
            EndpointPair.unordered(2, 7),
            EndpointPair.unordered(3, 8),
            EndpointPair.unordered(4, 8),
            EndpointPair.unordered(4, 5),
            EndpointPair.unordered(4, 6),
            EndpointPair.unordered(5, 6),
            EndpointPair.unordered(7, 8),
            EndpointPair.unordered(6, 7),
            EndpointPair.unordered(8, 9),
            EndpointPair.unordered(9, 1));

        // 确认无向图包含的边, 可以看到, 无向图中, 边的两个节点顺序可以为任意顺序
        then(graph.edges()).containsExactlyInAnyOrder(
            EndpointPair.unordered(2, 1),
            EndpointPair.unordered(3, 2),
            EndpointPair.unordered(4, 2),
            EndpointPair.unordered(2, 5),
            EndpointPair.unordered(7, 2),
            EndpointPair.unordered(8, 3),
            EndpointPair.unordered(8, 4),
            EndpointPair.unordered(5, 4),
            EndpointPair.unordered(6, 4),
            EndpointPair.unordered(6, 5),
            EndpointPair.unordered(8, 7),
            EndpointPair.unordered(7, 6),
            EndpointPair.unordered(9, 8),
            EndpointPair.unordered(9, 1));
    }

    /**
     * 测试构建有向图
     *
     * <p>
     * 通过 {@link #buildDirected(ElementOrder)} 方法可以构建一个表示有向图的 {@link MutableGraph} 对象
     * </p>
     *
     * <p>
     * 有向图的边是通过 {@link EndpointPair.Ordered} 类型对象表示, 通过 {@link EndpointPair#ordered(Object, Object)
     * EndpointPair.ordered(T, T)} 方法创建
     * </p>
     *
     * <p>
     * 通过 {@link MutableGraph#nodes()} 方法可以获取图的节点对象集合
     * </p>
     *
     * <p>
     * 通过 {@link MutableGraph#edges()} 方法可以获取无向图的边对象集合, 是一个元素类型为 {@link EndpointPair.Ordered} 的
     * {@code Set} 集合
     * </p>
     */
    @Test
    void build_shouldBuildDirectedGraphs() {
        // 构建有向图
        var graph = buildDirected(ElementOrder.insertion());

        // 确认有向图包含的节点
        then(graph.nodes()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 7, 8, 6, 9);

        // 确认有向图包含的边, 有向图的边由 EndpointPair.Ordered 类型对象表示
        then(graph.edges()).containsExactlyInAnyOrder(
            EndpointPair.ordered(1, 2),
            EndpointPair.ordered(2, 3),
            EndpointPair.ordered(2, 4),
            EndpointPair.ordered(2, 5),
            EndpointPair.ordered(2, 7),
            EndpointPair.ordered(3, 8),
            EndpointPair.ordered(4, 8),
            EndpointPair.ordered(4, 5),
            EndpointPair.ordered(4, 6),
            EndpointPair.ordered(5, 6),
            EndpointPair.ordered(7, 8),
            EndpointPair.ordered(6, 7),
            EndpointPair.ordered(8, 9),
            EndpointPair.ordered(9, 1));

        // 确认有向图包含的边, 可以看到, 有向图中, 边的两个节点顺序不能改变
        then(graph.edges()).doesNotContain(EndpointPair.ordered(2, 1));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(3, 2));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(4, 2));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(5, 2));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(7, 2));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(8, 3));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(8, 4));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(5, 4));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(6, 4));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(6, 5));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(8, 7));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(7, 6));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(9, 8));
        then(graph.edges()).doesNotContain(EndpointPair.ordered(1, 9));
    }

    /**
     * 获取无向图中某个节点的前趋节点列表
     *
     * <p>
     * 通过 {@link MutableGraph#predecessors(Object) MutableGraph.predecessors(T)} 方法可以获取由指定某个节点的前趋节点组成的集合
     * </p>
     *
     * <p>
     * 前趋节点即"图"中能和指定节点组成"边"的节点集合, 对于无向图来说, 凡是能和指定节点连接的节点, 都属于该指定节点的前趋节点
     * </p>
     */
    @Test
    void predecessors_shouldGetPredecessorsNodesOfUndirectedGraph() {
        var graph = buildUndirected(ElementOrder.insertion());

        // 确认无向图中各个节点的前趋节点
        var nodes = graph.predecessors(1);
        then(nodes).containsExactlyInAnyOrder(2, 9);

        nodes = graph.predecessors(2);
        then(nodes).containsExactlyInAnyOrder(1, 3, 4, 5, 7);

        nodes = graph.predecessors(3);
        then(nodes).containsExactlyInAnyOrder(2, 8);

        nodes = graph.predecessors(4);
        then(nodes).containsExactlyInAnyOrder(2, 8, 5, 6);

        nodes = graph.predecessors(5);
        then(nodes).containsExactlyInAnyOrder(2, 4, 6);

        nodes = graph.predecessors(6);
        then(nodes).containsExactlyInAnyOrder(4, 5, 7);

        nodes = graph.predecessors(7);
        then(nodes).containsExactlyInAnyOrder(2, 6, 8);

        nodes = graph.predecessors(8);
        then(nodes).containsExactlyInAnyOrder(3, 4, 7, 9);

        nodes = graph.predecessors(9);
        then(nodes).containsExactlyInAnyOrder(1, 8);
    }

    /**
     * 获取有向图中某个节点的前趋节点列表
     *
     * <p>
     * 通过 {@link MutableGraph#predecessors(Object) MutableGraph.predecessors(T)} 方法可以获取由指定某个节点的前趋节点组成的集合
     * </p>
     *
     * <p>
     * 前趋节点即"图"中能和指定节点组成"边"的节点集合, 对于有向图来说, 指定节点之前的节点为该节点的前趋节点
     * </p>
     */
    @Test
    void predecessors_shouldGetPredecessorsNodesOfDirectedGraph() {
        var graph = buildDirected(ElementOrder.insertion());

        // 确认有向图中各个节点的前趋节点
        var nodes = graph.predecessors(1);
        then(nodes).containsExactlyInAnyOrder(9);

        nodes = graph.predecessors(2);
        then(nodes).containsExactlyInAnyOrder(1);

        nodes = graph.predecessors(3);
        then(nodes).containsExactlyInAnyOrder(2);

        nodes = graph.predecessors(4);
        then(nodes).containsExactlyInAnyOrder(2);

        nodes = graph.predecessors(5);
        then(nodes).containsExactlyInAnyOrder(2, 4);

        nodes = graph.predecessors(6);
        then(nodes).containsExactlyInAnyOrder(4, 5);

        nodes = graph.predecessors(7);
        then(nodes).containsExactlyInAnyOrder(2, 6);

        nodes = graph.predecessors(8);
        then(nodes).containsExactlyInAnyOrder(3, 4, 7);

        nodes = graph.predecessors(9);
        then(nodes).containsExactlyInAnyOrder(8);
    }

    /**
     * 获取无向图中任意两个节点是否联通
     *
     * <p>
     * 通过 {@link MutableGraph#hasEdgeConnecting(Object, Object) MutableGraph.hasEdgeConnecting(T, T)}
     * 方法可以判断两个节点是否联通, 如果联通则返回 {@code true}
     * </p>
     *
     * <p>
     * 对于无向图的任意两个节点 {@code A} 和 {@code B}, 如果有 {@code A → B} 的联通成立, 则一定有 {@code B → A} 的联通成立
     * </p>
     */
    @Test
    void hasEdgeConnecting_shouldCheckedIfHasConnectedEdgeBetweenTwoUndirectedNodes() {
        var graph = buildUndirected(ElementOrder.insertion());

        var connected = graph.hasEdgeConnecting(2, 5);
        then(connected).isTrue();

        connected = graph.hasEdgeConnecting(5, 2);
        then(connected).isTrue();
    }

    /**
     * 获取有向图中任意两个节点是否联通
     */
    @Test
    void hasEdgeConnecting_shouldCheckedIfHasConnectedEdgeBetweenTwoDirectedNodes() {
        var graph = buildDirected(ElementOrder.insertion());

        var connected = graph.hasEdgeConnecting(2, 5);
        then(connected).isTrue();

        connected = graph.hasEdgeConnecting(5, 2);
        then(connected).isFalse();
    }
}
