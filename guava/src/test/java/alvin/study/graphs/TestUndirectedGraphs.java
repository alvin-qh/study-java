package alvin.study.graphs;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableGraph;

/**
 * 测试无向图数据结构类
 *
 * <p>
 * Guava 提供了 {@link MutableGraph} 集合类型, 用于构建"图"数据结构, 所谓"无向图", 即图的"边"不具有指向性, 例如有节点 {@code A} 和
 * {@code B}, 则边 {@code A → B} 等于边 {@code B → A}
 * </p>
 *
 * <p>
 * 本例中的所有操作都通过如下图表示的图结构演示: <img src="assets/directed.png"/>
 * </p>
 */
class TestUndirectedGraphs {
    // 边集合列表, 图的每个边由相连的两个节点组成
    private GraphsDatasource<Integer> datasource = new GraphsDatasource<>(
        new Integer[] { 1, 2 },
        new Integer[] { 2, 3 },
        new Integer[] { 2, 4 },
        new Integer[] { 2, 5 },
        new Integer[] { 2, 7 },
        new Integer[] { 3, 8 },
        new Integer[] { 4, 5 },
        new Integer[] { 4, 6 },
        new Integer[] { 4, 8 },
        new Integer[] { 5, 6 },
        new Integer[] { 6, 7 },
        new Integer[] { 7, 8 },
        new Integer[] { 8, 9 },
        new Integer[] { 9, 1 });

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
        var graph = datasource.buildUndirected(ElementOrder.insertion());

        // 确认无向图包含的节点
        then(graph.nodes()).containsExactlyInAnyOrderElementsOf(datasource.nodes());

        // 确认无向图包含的边, 无向图的边由 EndpointPair.Unordered 类型对象表示
        // 对于无向图来说, 边的节点顺序对调之后, 仍和之前相等
        then(graph.edges()).containsExactlyInAnyOrderElementsOf(datasource.unOrderedEdges(false));
        then(graph.edges()).containsExactlyInAnyOrderElementsOf(datasource.unOrderedEdges(true));
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
        var graph = datasource.buildDirected(ElementOrder.insertion());

        // 确认有向图包含的节点
        then(graph.nodes()).containsExactlyInAnyOrderElementsOf(datasource.nodes());

        // 确认有向图包含的边, 有向图的边由 EndpointPair.Ordered 类型对象表示
        then(graph.edges()).containsExactlyInAnyOrderElementsOf(datasource.orderedEdges(false));

        // 确认有向图包含的边, 可以看到, 有向图中, 边的两个节点顺序不能改变
        for (var edge : datasource.orderedEdges(true)) {
            then(graph.edges()).doesNotContain(edge);
        }
    }

    /**
     * 获取无向图中任意两个节点是否连通
     *
     * <p>
     * 通过 {@link MutableGraph#hasEdgeConnecting(Object, Object) MutableGraph.hasEdgeConnecting(T, T)}
     * 方法可以判断两个节点是否连通, 如果连通则返回 {@code true}
     * </p>
     *
     * <p>
     * 对于无向图的任意两个节点 {@code A} 和 {@code B}, 如果有 {@code A → B} 的连通成立, 则一定有 {@code B → A} 的连通成立
     * </p>
     */
    @Test
    void hasEdgeConnecting_shouldCheckedIfHasConnectedEdgeBetweenTwoUndirectedNodes() {
        var graph = datasource.buildUndirected(ElementOrder.insertion());

        // 确认具有边的两个节点是连通的
        var connected = graph.hasEdgeConnecting(2, 5);
        then(connected).isTrue();

        // 确认在无向图中, 连通的两个节点反过来也是联通的
        connected = graph.hasEdgeConnecting(5, 2);
        then(connected).isTrue();

        // 确认跨两个边的节点不具备连通性
        connected = graph.hasEdgeConnecting(2, 8);
        then(connected).isFalse();
    }

    // /**
    // * 获取有向图中任意两个节点是否连通
    // *
    // * <p>
    // * 通过 {@link MutableGraph#hasEdgeConnecting(Object, Object) MutableGraph.hasEdgeConnecting(T, T)}
    // * 方法可以判断两个节点是否连通, 如果连通则返回 {@code true}
    // * </p>
    // *
    // * <p>
    // * 对于有向图的任意两个节点 {@code A} 和 {@code B}, 如果有 {@code A → B} 的连通成立, 则 {@code B → A} 的连通必然不成立
    // * </p>
    // */
    // @Test
    // void hasEdgeConnecting_shouldCheckedIfHasConnectedEdgeBetweenTwoDirectedNodes() {
    // var graph = buildDirected(ElementOrder.insertion());

    // // 确认具有边的两个节点是连通的
    // var connected = graph.hasEdgeConnecting(2, 5);
    // then(connected).isTrue();

    // // 确认在有向图中, 连通的两个节点反过来不能连通
    // connected = graph.hasEdgeConnecting(5, 2);
    // then(connected).isFalse();

    // // 确认跨两个边的节点不具备连通性
    // connected = graph.hasEdgeConnecting(2, 8);
    // then(connected).isFalse();
    // }

    // /**
    // * 获取无向图中某个节点的前趋节点列表
    // *
    // * <p>
    // * 通过 {@link MutableGraph#predecessors(Object) MutableGraph.predecessors(T)} 方法可以获取由指定某个节点的前趋节点组成的集合
    // * </p>
    // *
    // * <p>
    // * 对于无向图来说, 凡是能和指定节点连接的节点, 都属于该节点的前趋节点, 即无向图某个节点的前趋节点同时也是其后继节点
    // * </p>
    // */
    // @Test
    // void predecessors_shouldGetPredecessorsNodesOfUndirectedGraph() {
    // var graph = buildUndirected(ElementOrder.insertion());

    // // 获取无向图中各个节点的前趋节点, 确认和后继节点一致
    // var nodes = graph.predecessors(1);
    // then(nodes).containsExactlyInAnyOrder(2, 9);

    // nodes = graph.predecessors(2);
    // then(nodes).containsExactlyInAnyOrder(1, 3, 4, 5, 7);

    // nodes = graph.predecessors(3);
    // then(nodes).containsExactlyInAnyOrder(2, 8);

    // nodes = graph.predecessors(4);
    // then(nodes).containsExactlyInAnyOrder(2, 8, 5, 6);

    // nodes = graph.predecessors(5);
    // then(nodes).containsExactlyInAnyOrder(2, 4, 6);

    // nodes = graph.predecessors(6);
    // then(nodes).containsExactlyInAnyOrder(4, 5, 7);

    // nodes = graph.predecessors(7);
    // then(nodes).containsExactlyInAnyOrder(2, 6, 8);

    // nodes = graph.predecessors(8);
    // then(nodes).containsExactlyInAnyOrder(3, 4, 7, 9);

    // nodes = graph.predecessors(9);
    // then(nodes).containsExactlyInAnyOrder(1, 8);
    // }

    // /**
    // * 获取有向图中某个节点的前趋节点列表
    // *
    // * <p>
    // * 通过 {@link MutableGraph#predecessors(Object) MutableGraph.predecessors(T)} 方法可以获取由指定某个节点的前趋节点组成的集合
    // * </p>
    // *
    // * <p>
    // * 对于有向图来说, 以指定节点为后继节点组成边的节点为该节点的前趋节点
    // * </p>
    // */
    // @Test
    // void predecessors_shouldGetPredecessorsNodesOfDirectedGraph() {
    // var graph = buildDirected(ElementOrder.insertion());

    // // 确认有向图中各个节点的前趋节点
    // var nodes = graph.predecessors(1);
    // then(nodes).containsExactlyInAnyOrder(9);

    // nodes = graph.predecessors(2);
    // then(nodes).containsExactlyInAnyOrder(1);

    // nodes = graph.predecessors(3);
    // then(nodes).containsExactlyInAnyOrder(2);

    // nodes = graph.predecessors(4);
    // then(nodes).containsExactlyInAnyOrder(2);

    // nodes = graph.predecessors(5);
    // then(nodes).containsExactlyInAnyOrder(2, 4);

    // nodes = graph.predecessors(6);
    // then(nodes).containsExactlyInAnyOrder(4, 5);

    // nodes = graph.predecessors(7);
    // then(nodes).containsExactlyInAnyOrder(2, 6);

    // nodes = graph.predecessors(8);
    // then(nodes).containsExactlyInAnyOrder(3, 4, 7);

    // nodes = graph.predecessors(9);
    // then(nodes).containsExactlyInAnyOrder(8);
    // }

    // /**
    // * 获取无向图中某个节点的后继节点列表
    // *
    // * <p>
    // * 通过 {@link MutableGraph#successors(Object) MutableGraph.successors(T)} 方法可以获取由指定某个节点的后继节点组成的集合
    // * </p>
    // *
    // * <p>
    // * 对于无向图来说, 凡是能和指定节点连接的节点, 都属于该节点的后继节点, 即无向图某个节点的后继节点同时也是其前趋节点
    // * </p>
    // */
    // @Test
    // void successors_shouldGetSuccessorsNodesOfUndirectedGraph() {
    // var graph = buildUndirected(ElementOrder.insertion());

    // // 获取无向图中各个节点的后继节点, 确认和前驱节点一致
    // var nodes = graph.successors(1);
    // then(nodes).containsExactlyInAnyOrder(2, 9);

    // nodes = graph.successors(2);
    // then(nodes).containsExactlyInAnyOrder(1, 3, 4, 5, 7);

    // nodes = graph.successors(3);
    // then(nodes).containsExactlyInAnyOrder(2, 8);

    // nodes = graph.successors(4);
    // then(nodes).containsExactlyInAnyOrder(2, 8, 5, 6);

    // nodes = graph.successors(5);
    // then(nodes).containsExactlyInAnyOrder(2, 4, 6);

    // nodes = graph.successors(6);
    // then(nodes).containsExactlyInAnyOrder(4, 5, 7);

    // nodes = graph.successors(7);
    // then(nodes).containsExactlyInAnyOrder(2, 6, 8);

    // nodes = graph.successors(8);
    // then(nodes).containsExactlyInAnyOrder(3, 4, 7, 9);

    // nodes = graph.successors(9);
    // then(nodes).containsExactlyInAnyOrder(1, 8);
    // }

    // /**
    // * 获取有向图中某个节点的后继节点列表
    // *
    // * <p>
    // * 通过 {@link MutableGraph#successors(Object) MutableGraph.successors(T)} 方法可以获取由指定某个节点的后继节点组成的集合
    // * </p>
    // *
    // * <p>
    // * 对于有向图来说, 以指定节点为前趋节点组成边的节点为该节点的后继节点
    // * </p>
    // */
    // @Test
    // void successors_shouldGetSuccessorsNodesOfDirectedGraph() {
    // var graph = buildDirected(ElementOrder.insertion());

    // // 确认有向图中各个节点的后继节点
    // var nodes = graph.successors(1);
    // then(nodes).containsExactlyInAnyOrder(2);

    // nodes = graph.successors(2);
    // then(nodes).containsExactlyInAnyOrder(3, 4, 5, 7);

    // nodes = graph.successors(3);
    // then(nodes).containsExactlyInAnyOrder(8);

    // nodes = graph.successors(4);
    // then(nodes).containsExactlyInAnyOrder(8, 5, 6);

    // nodes = graph.successors(5);
    // then(nodes).containsExactlyInAnyOrder(6);

    // nodes = graph.successors(6);
    // then(nodes).containsExactlyInAnyOrder(7);

    // nodes = graph.successors(7);
    // then(nodes).containsExactlyInAnyOrder(8);

    // nodes = graph.successors(8);
    // then(nodes).containsExactlyInAnyOrder(9);

    // nodes = graph.successors(9);
    // then(nodes).containsExactlyInAnyOrder(1);
    // }

    // /**
    // * 获取无向图中指定节点的邻接节点
    // *
    // * <p>
    // * 通过 {@link MutableGraph#adjacentNodes(Object) MutableGraph.adjacentNodes(T)} 方法可以获取指定某个节点的邻接节点
    // * </p>
    // *
    // * <p>
    // * 一个图中, 某个节点的邻接节点即其前趋和后继节点
    // * </p>
    // */
    // @Test
    // void adjacentNodes_shouldGetAdjacentNodesOfUndirectedGraph() {
    // var graph = buildUndirected(ElementOrder.insertion());

    // // 获取无向图中各个节点的邻接节点, 确认由其前趋和后继节点组成
    // var nodes = graph.adjacentNodes(1);
    // then(nodes).containsExactlyInAnyOrder(2, 9);

    // nodes = graph.adjacentNodes(2);
    // then(nodes).containsExactlyInAnyOrder(1, 3, 4, 5, 7);

    // nodes = graph.adjacentNodes(3);
    // then(nodes).containsExactlyInAnyOrder(2, 8);

    // nodes = graph.adjacentNodes(4);
    // then(nodes).containsExactlyInAnyOrder(2, 8, 5, 6);

    // nodes = graph.adjacentNodes(5);
    // then(nodes).containsExactlyInAnyOrder(2, 4, 6);

    // nodes = graph.adjacentNodes(6);
    // then(nodes).containsExactlyInAnyOrder(4, 5, 7);

    // nodes = graph.adjacentNodes(7);
    // then(nodes).containsExactlyInAnyOrder(2, 6, 8);

    // nodes = graph.adjacentNodes(8);
    // then(nodes).containsExactlyInAnyOrder(3, 4, 7, 9);

    // nodes = graph.adjacentNodes(9);
    // then(nodes).containsExactlyInAnyOrder(1, 8);
    // }

    // /**
    // * 获取有向图中指定节点的邻接节点
    // *
    // * <p>
    // * 通过 {@link MutableGraph#adjacentNodes(Object) MutableGraph.adjacentNodes(T)} 方法可以获取指定某个节点的邻接节点
    // * </p>
    // *
    // * <p>
    // * 一个图中, 某个节点的邻接节点即其前趋和后继节点
    // * </p>
    // */
    // @Test
    // void adjacentNodes_shouldGetAdjacentNodesOfDirectedGraph() {
    // var graph = buildUndirected(ElementOrder.insertion());

    // // 获取无向图中各个节点的邻接节点, 确认由其前趋和后继节点组成
    // var nodes = graph.adjacentNodes(1);
    // then(nodes).containsExactlyInAnyOrder(2, 9);

    // nodes = graph.adjacentNodes(2);
    // then(nodes).containsExactlyInAnyOrder(1, 3, 4, 5, 7);

    // nodes = graph.adjacentNodes(3);
    // then(nodes).containsExactlyInAnyOrder(2, 8);

    // nodes = graph.adjacentNodes(4);
    // then(nodes).containsExactlyInAnyOrder(2, 8, 5, 6);

    // nodes = graph.adjacentNodes(5);
    // then(nodes).containsExactlyInAnyOrder(2, 4, 6);

    // nodes = graph.adjacentNodes(6);
    // then(nodes).containsExactlyInAnyOrder(4, 5, 7);

    // nodes = graph.adjacentNodes(7);
    // then(nodes).containsExactlyInAnyOrder(2, 6, 8);

    // nodes = graph.adjacentNodes(8);
    // then(nodes).containsExactlyInAnyOrder(3, 4, 7, 9);

    // nodes = graph.adjacentNodes(9);
    // then(nodes).containsExactlyInAnyOrder(1, 8);
    // }

    // /**
    // * 求无向图指定节点的"度"
    // *
    // * <p>
    // * "度"分为"入度"和"出度", "入度"指该节点的所有前趋节点数量, "出度"指该节点的所有后续节点的数量, "度"即该阶段的"出度"和"入度"之和,
    // * 即该阶段的邻接节点数量
    // * </p>
    // *
    // * <p>
    // * 通过 {@link MutableGraph#inDegree(Object) MutableGraph.inDegree(T)} 方法可以获得指定节点的"入度"
    // * </p>
    // *
    // * <p>
    // * 通过 {@link MutableGraph#outDegree(Object) MutableGraph.outDegree(T)} 可以获得指定节点的"出度"
    // * </p>
    // *
    // * <p>
    // * 通过 {@link MutableGraph#degree(Object) MutableGraph.degree(T)} 可以获得指定节点的"度"
    // * </p>
    // *
    // * <p>
    // * 在无向图中, 一个节点的"度"和其"入度"以及"出度"的值相同
    // * </p>
    // */
    // @Test
    // void degree_shouldGetNodeDegreeOfUndirectedGraph() {
    // var graph = buildUndirected(ElementOrder.insertion());

    // // 获取无向图中各个节点的邻接节点, 确认由其前趋和后继节点组成
    // then(graph.degree(1))
    // .isEqualTo(graph.inDegree(1))
    // .isEqualTo(graph.outDegree(1))
    // .isEqualTo(graph.adjacentNodes(1).size())
    // .isEqualTo(graph.predecessors(1).size())
    // .isEqualTo(graph.successors(1).size())
    // .isEqualTo(2);

    // then(graph.degree(2))
    // .isEqualTo(graph.inDegree(2))
    // .isEqualTo(graph.outDegree(2))
    // .isEqualTo(graph.adjacentNodes(2).size())
    // .isEqualTo(graph.predecessors(2).size())
    // .isEqualTo(graph.successors(2).size())
    // .isEqualTo(5);

    // then(graph.degree(3))
    // .isEqualTo(graph.inDegree(3))
    // .isEqualTo(graph.outDegree(3))
    // .isEqualTo(graph.adjacentNodes(3).size())
    // .isEqualTo(graph.predecessors(3).size())
    // .isEqualTo(graph.successors(3).size())
    // .isEqualTo(2);

    // then(graph.degree(4))
    // .isEqualTo(graph.inDegree(4))
    // .isEqualTo(graph.outDegree(4))
    // .isEqualTo(graph.adjacentNodes(4).size())
    // .isEqualTo(graph.predecessors(4).size())
    // .isEqualTo(graph.successors(4).size())
    // .isEqualTo(4);
    // }
}
