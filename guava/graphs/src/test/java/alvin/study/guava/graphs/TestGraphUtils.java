package alvin.study.guava.graphs;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;

import org.junit.jupiter.api.Test;

import alvin.study.guava.graphs.GraphsDatasource.Edge;

/**
 * 测试 {@link Graphs} 工具类, 用于对"图"对象进行对应操作
 *
 * <p>
 * 本例通过 <img src="../../../../../../../assets/directed.png" /> 这个有向图来演示
 * </p>
 */
class TestGraphUtils {
    // 边集合列表, 图的每个边由相连的两个节点组成
    private final GraphsDatasource<Integer, ?> datasource = new GraphsDatasource<>(
        Edge.of(1, 2),
        Edge.of(2, 3),
        Edge.of(2, 4),
        Edge.of(2, 5),
        Edge.of(2, 7),
        Edge.of(3, 8),
        Edge.of(4, 5),
        Edge.of(4, 6),
        Edge.of(4, 8),
        Edge.of(5, 6),
        Edge.of(6, 7),
        Edge.of(7, 8),
        Edge.of(8, 9),
        Edge.of(9, 1));

    /**
     * 判断当前"图"对象是否包含"环"
     *
     * <p>
     * 通过 {@link Graphs#hasCycle(com.google.common.graph.Graph)
     * Graphs.hasCycle(Graph)} 方法可以判断一个图对象中是否包含"环"
     * </p>
     *
     * <p>
     * 所谓的 "环", 即从图中的某个节点出发, 经过若干条 "边" 后, 仍能回到该节点,
     * 所以这条路径可以无限循环下去, 也就是说该图中有一个环
     * </p>
     *
     * <p>
     * 特殊的, 如果图中的一条边, 起始节点和终止节点都是它自身, 也称为 "自环",
     * 这种情况默认是不允许的, 除非在构建图对象时, 通过
     * {@link com.google.common.graph.GraphBuilder#allowsSelfLoops(boolean)
     * GraphBuilder.allowsSelfLoops(true)} 方法允许图中可以发生此情况
     * </p>
     *
     * <p>
     * 如果一个"有向图", 且不具备任何"环", 该图也被称为"有向无环图"
     * </p>
     *
     * <p>
     * 在本例中, 原始数据中的 {@code 9 → 1} 这条边使得整个图具有一个 "环",
     * 即从节点 {@code 1} 开始可以回到自身. 删除 {@code 9 → 1}
     * 这条边后, 整个图就不存在任何 "环", 成为一个 "有向无环图"
     * </p>
     */
    @Test
    void hasCycle_shouldCheckIfGraphHasCycle() {
        // 构建有向图, 且允许产生节点自环
        var graph = datasource.buildGraph(
            true, ElementOrder.insertion(), ElementOrder.stable(), true);
        // 确认图对象允许节点自环
        then(graph.allowsSelfLoops()).isTrue();

        // 确认由于 9 → 1 这条边的存在, 所以该图具有一个环
        then(Graphs.hasCycle(graph)).isTrue();

        // 删除 9 → 1 这条边, 确认图中不再有环
        graph.removeEdge(9, 1);
        then(Graphs.hasCycle(graph)).isFalse();

        // 增加 1 → 1 这条边, 产生一个节点自环, 所以此时图又具备一个环
        graph.putEdge(1, 1);
        then(Graphs.hasCycle(graph)).isTrue();
    }

    /**
     * 根据所给节点, 获取指定图的子图
     *
     * <p>
     * 通过 {@link Graphs#inducedSubgraph(com.google.common.graph.Graph, Iterable)
     * Graphs.inducedSubgraph(Graph, Iterable)} 方法可以通过一个图中的某几个节点,
     * 获取该图的一个子图
     * </p>
     *
     * <p>
     * 子图中包含的点会保持和原图相同的连接
     * </p>
     */
    @Test
    void inducedSubgraph_shouldGetSubgraphByGivenNodes() {
        // 构建一个有向图
        var graph = datasource.buildGraph(
            true, ElementOrder.insertion(), ElementOrder.stable());

        // 获取指定节点的子图
        var subgraph = Graphs.inducedSubgraph(graph, List.of(2, 3, 4, 6, 8));

        // 确认子图中包含全部指定节点
        then(subgraph.nodes()).containsExactlyInAnyOrder(2, 3, 4, 6, 8);
        // 确认子图中包含指定的边
        then(subgraph.edges()).containsExactlyInAnyOrder(
            EndpointPair.ordered(2, 3),
            EndpointPair.ordered(2, 4),
            EndpointPair.ordered(3, 8),
            EndpointPair.ordered(4, 6),
            EndpointPair.ordered(4, 8));

        // 确认子图中的所有节点都在原图中包含
        then(graph.nodes()).containsAll(subgraph.nodes());
        // 确认子图中的所有边都在原图中包含
        then(graph.edges()).containsAll(subgraph.edges());
    }

    /**
     * 根据所给节点, 获取到图中此节点可以到达的所有节点
     *
     * <p>
     * 通过 {@link Graphs#reachableNodes(com.google.common.graph.Graph, Object)
     * Graphs.reachableNodes(Graph, T)} 方法获取图中与给定节点最终可以连通的所有节点集合
     * </p>
     *
     * <p>
     * 所谓可到达节点, 即在图中, 无论经过多少条边, 可以最终连通 {@code A} 和 {@code B}
     * 这两个点, 即表示 {@code A} 节点可以到达
     * {@code B} 节点
     * </p>
     *
     * <p>
     * {@code reachableNodes} 方法是通过 BFS (广度优先) 算法寻找连通节点的,
     * 所以结果的顺序是按照与所给节点的距离排序的, 且 "所给节点本身" 总是排在结果的第一个
     * (自己总是可以到达自己)
     * </p>
     *
     * <p>
     * 因为本例是一个 "全连通图", 所以一个节点可以到达任意其它节点
     * </p>
     */
    @Test
    void reachableNodes_shouldFindReachableNodesOfGivenNode() {
        // 构建一个有向图
        var graph = datasource.buildGraph(
            true, ElementOrder.insertion(), ElementOrder.stable());

        // 查询给定节点可以到达的所有其它节点
        var nodes = Graphs.reachableNodes(graph, 6);
        // 确认可到达节点集合
        then(nodes).containsExactly(6, 7, 8, 9, 1, 2, 3, 4, 5);
    }

    /**
     * 获取图的 "传递闭包"
     *
     * <p>
     * 通过 {@link Graphs#transitiveClosure(com.google.common.graph.Graph)
     * Graphs.transitiveClosure(Graph)} 方法可以获取指定图的传递闭包结果
     * </p>
     *
     * <p>
     * 在一个图中, 如果两个点最终可以连通, 则建立一条边直接将这两点连通,
     * 最终结果形成一个包含额外边的新图, 称为原图的 "传递闭包"
     * </p>
     */
    @Test
    void transitiveClosure_shouldGetTheTransitiveClosureOfGraph() {
        // 构建一个有向图
        var graph = datasource.buildGraph(true, ElementOrder.insertion(), ElementOrder.stable());

        // 获取指定图的"传递闭包"结果
        var closureGraph = Graphs.transitiveClosure(graph);

        // 确认传递闭包结果中的节点和原图一致
        then(closureGraph.nodes()).containsExactlyElementsOf(graph.nodes());
        // 确认传递闭包结果中包含原图中的所有边
        then(closureGraph.edges()).containsAll(graph.edges());

        // 计算传递闭包结果比原图多处的边
        var edges = new LinkedHashSet<>(closureGraph.edges());
        edges.removeAll(graph.edges());

        // 确认传递闭包中具有所有sud可连通节点的直接连接边
        then(edges).containsExactlyInAnyOrder(
            EndpointPair.ordered(1, 1),
            EndpointPair.ordered(1, 3),
            EndpointPair.ordered(1, 4),
            EndpointPair.ordered(1, 5),
            EndpointPair.ordered(1, 7),
            EndpointPair.ordered(1, 8),
            EndpointPair.ordered(1, 6),
            EndpointPair.ordered(1, 9),
            EndpointPair.ordered(2, 2),
            EndpointPair.ordered(2, 8),
            EndpointPair.ordered(2, 6),
            EndpointPair.ordered(2, 9),
            EndpointPair.ordered(2, 1),
            EndpointPair.ordered(3, 3),
            EndpointPair.ordered(3, 9),
            EndpointPair.ordered(3, 1),
            EndpointPair.ordered(3, 2),
            EndpointPair.ordered(3, 4),
            EndpointPair.ordered(3, 5),
            EndpointPair.ordered(3, 7),
            EndpointPair.ordered(3, 6),
            EndpointPair.ordered(4, 4),
            EndpointPair.ordered(4, 7),
            EndpointPair.ordered(4, 9),
            EndpointPair.ordered(4, 1),
            EndpointPair.ordered(4, 2),
            EndpointPair.ordered(4, 3),
            EndpointPair.ordered(5, 5),
            EndpointPair.ordered(5, 7),
            EndpointPair.ordered(5, 8),
            EndpointPair.ordered(5, 9),
            EndpointPair.ordered(5, 1),
            EndpointPair.ordered(5, 2),
            EndpointPair.ordered(5, 3),
            EndpointPair.ordered(5, 4),
            EndpointPair.ordered(7, 7),
            EndpointPair.ordered(7, 9),
            EndpointPair.ordered(7, 1),
            EndpointPair.ordered(7, 2),
            EndpointPair.ordered(7, 3),
            EndpointPair.ordered(7, 4),
            EndpointPair.ordered(7, 5),
            EndpointPair.ordered(7, 6),
            EndpointPair.ordered(8, 8),
            EndpointPair.ordered(8, 1),
            EndpointPair.ordered(8, 2),
            EndpointPair.ordered(8, 3),
            EndpointPair.ordered(8, 4),
            EndpointPair.ordered(8, 5),
            EndpointPair.ordered(8, 7),
            EndpointPair.ordered(8, 6),
            EndpointPair.ordered(6, 6),
            EndpointPair.ordered(6, 8),
            EndpointPair.ordered(6, 9),
            EndpointPair.ordered(6, 1),
            EndpointPair.ordered(6, 2),
            EndpointPair.ordered(6, 3),
            EndpointPair.ordered(6, 4),
            EndpointPair.ordered(6, 5),
            EndpointPair.ordered(9, 9),
            EndpointPair.ordered(9, 2),
            EndpointPair.ordered(9, 3),
            EndpointPair.ordered(9, 4),
            EndpointPair.ordered(9, 5),
            EndpointPair.ordered(9, 7),
            EndpointPair.ordered(9, 8),
            EndpointPair.ordered(9, 6));
    }

    /**
     * 获取有向图的"反转图"
     *
     * <p>
     * 通过 {@link Graphs#transpose(com.google.common.graph.Graph)
     * Graphs.transpose(Graph)} 方法可以获取指定图的反转图结果
     * </p>
     *
     * <p>
     * 所谓"反转图", 即和原图具有相同的节点和边, 只是每个边的顶点进行了对调
     * </p>
     */
    @Test
    void transpose_shouldTransposeAGraph() {
        // 构建一个有向图
        var graph = datasource.buildGraph(true, ElementOrder.insertion(), ElementOrder.stable());

        // 对图中的边进行反转
        var transGraph = Graphs.transpose(graph);

        // 确认转换得到的图节点和原图一致
        then(transGraph.nodes()).containsExactlyInAnyOrderElementsOf(graph.nodes());
        // 确认转换得到的图边和原图相反
        then(transGraph.edges()).containsExactlyInAnyOrderElementsOf(datasource.orderedEdges(true));
    }
}
