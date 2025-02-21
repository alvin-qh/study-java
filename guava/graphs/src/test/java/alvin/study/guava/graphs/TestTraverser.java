package alvin.study.guava.graphs;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.Traverser;

import alvin.study.guava.graphs.GraphsDatasource.Edge;

/**
 * 测试图的遍历
 */
class TestTraverser {
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
     * 演示无向图的遍历, 有向图的遍历何其类似
     *
     * <p>
     * 通过 {@link Traverser#forGraph(com.google.common.graph.SuccessorsFunction) Traverser.forGraph(SuccessorsFunction)}
     * 方法可以构建一个遍历类型 ({@link Traverser}) 对象
     * </p>
     *
     * <p>
     * {@link Traverser#breadthFirst(Object)} 方法对图进行"广度优先遍历" (BFS), 遍历结果为: 按照节点的后继分层遍历.
     * {@link Traverser#breadthFirst(Iterable)} 方法可以设置一组遍历的起始节点
     * </p>
     *
     * <p>
     * {@link Traverser#depthFirstPreOrder(Object)} 方法对图进行"正序深度优先遍历" (DFS), 遍历结果为: 先沿着一条路径遍历其上所有节点,
     * 之后以同样方法访问其它路径上的节点. {@link Traverser#depthFirstPreOrder(Iterable)} 方法可以设置一组遍历的起始节点
     * </p>
     *
     * <p>
     * {@link Traverser#depthFirstPostOrder(Object)} 方法对图进行"逆序深度优先遍历" (DFS), 遍历结果为:
     * 先沿着一条路径到达该路径上最后一个节点, 逆序访问直到返回起始节点, 之后以同样方法访问其它路径上的节点.
     * {@link Traverser#depthFirstPostOrder(Iterable)} 方法可以设置一组遍历的起始节点
     * </p>
     *
     * <p>
     * 本例中使用如下无向图演示图的遍历
     * <img src="../../../../../../../assets/undirected.png" />
     * </p>
     */
    @Test
    void traverser_shouldTraversalFromGivenNodeInGraph() {
        // 创建一个无向图
        var graph = datasource.buildGraph(false, ElementOrder.insertion(), ElementOrder.stable());

        // 实例化图遍历器对象
        var traverser = Traverser.forGraph(graph);

        // 测试广度优先遍历
        var bfs = traverser.breadthFirst(1);
        then(bfs).containsExactly(1, 2, 9, 3, 4, 5, 7, 8, 6);

        bfs = traverser.breadthFirst(List.of(1, 9));
        then(bfs).containsExactly(1, 9, 2, 8, 3, 4, 5, 7, 6);

        // 测试正序深度优先遍历
        var dfs = traverser.depthFirstPreOrder(1);
        then(dfs).containsExactly(1, 2, 3, 8, 4, 5, 6, 7, 9);

        dfs = traverser.depthFirstPreOrder(List.of(1, 9));
        then(dfs).containsExactly(1, 2, 3, 8, 4, 5, 6, 7, 9);

        // 测试逆序深度优先遍历
        dfs = traverser.depthFirstPostOrder(1);
        then(dfs).containsExactly(7, 6, 5, 4, 9, 8, 3, 2, 1);

        dfs = traverser.depthFirstPostOrder(List.of(1, 9));
        then(dfs).containsExactly(7, 6, 5, 4, 9, 8, 3, 2, 1);
    }
}
