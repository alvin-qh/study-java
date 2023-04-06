package alvin.study.graphs;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.Graphs;

import alvin.study.graphs.GraphsDatasource.Edge;

/**
 * 测试 {@link Graphs} 工具类, 用于对"图"对象进行对应操作
 *
 * <p>
 * 本例通过 <img src="assets/directed.png" /> 这个有向图来演示
 * </p>
 */
class TestGraphUtils {
    // 边集合列表, 图的每个边由相连的两个节点组成
    private GraphsDatasource<Integer, ?> datasource = new GraphsDatasource<>(
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
     * 通过 {@link Graphs#hasCycle(com.google.common.graph.Graph) Graphs.hasCycle(Graph)} 方法可以判断一个图对象中是否包含"环"
     * </p>
     *
     * <p>
     * 所谓的"环", 即从图中的某个节点出发, 经过若干条"边"后, 仍能回到该节点, 所以这条路径可以无限循环下去, 也就是说该图中有一个环
     * </p>
     *
     * <p>
     * 特殊的, 如果图中的一条边, 起始节点和终止节点都是它自身, 也称为"自环", 这种情况默认是不允许的, 除非在构建图对象时, 通过
     * {@link com.google.common.graph.GraphBuilder#allowsSelfLoops(boolean) GraphBuilder.allowsSelfLoops(true)}
     * 方法允许图中可以发生此情况
     * </p>
     *
     * <p>
     * 如果一个"有向图", 且不具备任何"环", 该图也被称为"有向无环图"
     * </p>
     *
     * <p>
     * 在本例中, 原始数据中的 {@code 9 → 1} 这条边使得整个图具有一个"环", 即从节点 {@code 1} 开始可以回到自身. 删除 {@code 9 → 1}
     * 这条边后, 整个图就不存在任何"环", 成为一个"有向无环图"
     * </p>
     */
    @Test
    void cycle_shouldCheckIfGraphHasCycle() {
        // 构建有向图, 且允许产生节点自环
        var graph = datasource.buildGraph(true, ElementOrder.insertion(), ElementOrder.stable(), true);
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
}
