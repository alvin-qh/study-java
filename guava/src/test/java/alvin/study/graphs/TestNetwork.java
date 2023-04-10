package alvin.study.graphs;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.graph.ElementOrder;

import alvin.study.graphs.GraphsDatasource.Edge;

/**
 * 测试网络类型
 *
 * <p>
 * 网络类型 {@link com.google.common.graph.Network Network} 是一种特殊类型的"图", 该类型允许两个节点之间存在多条"边",
 * 每条边均有一个"唯一对象"表示
 * </p>
 *
 * <p>
 * 如同两地间可以有多条航线, 每条航线的距离和费用 (边权重值) 都可以不同
 * </p>
 */
class TestNetwork {
    // 边集合列表, 图的每个边由相连的两个节点组成, 注意三元组的最后一个值是表示边的对象, 而不是边权重值
    // 本例中, 节点由整数值表示, 边由字符串类型表示
    private GraphsDatasource<Integer, String> datasource = new GraphsDatasource<>(
        Edge.of(1, 2, "1-2"),
        Edge.of(2, 3, "2-3"),
        Edge.of(2, 4, "2-4"),
        Edge.of(2, 5, "2-5"),
        Edge.of(2, 7, "2-7"),
        Edge.of(3, 8, "3-8"),
        Edge.of(4, 5, "4-5"),
        Edge.of(4, 6, "4-6"),
        Edge.of(4, 8, "4-8"),
        Edge.of(5, 6, "5-6"),
        Edge.of(6, 7, "6-7"),
        Edge.of(7, 8, "7-8"),
        Edge.of(8, 9, "8-9"),
        Edge.of(9, 1, "9-1"));

    @Nested
    class TestDirectedNetwork {
        /**
         * 测试构建有向网络
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#nodes() Network.nodes()} 方法可以获取网络中的所有节点对象集合
         * </p>
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#edges() Network.edges()} 方法可以获取网络中所有边对象集合
         * </p>
         */
        @Test
        void build_shouldBuildDirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(true, ElementOrder.insertion(), ElementOrder.insertion());

            // 确认网络中包含的节点
            then(network.nodes()).containsExactlyInAnyOrderElementsOf(datasource.nodes());

            // 确认网络中包含的边对象
            then(network.edges()).containsExactlyInAnyOrder(
                "1-2",
                "2-3",
                "2-4",
                "2-5",
                "2-7",
                "3-8",
                "4-5",
                "4-6",
                "4-8",
                "5-6",
                "6-7",
                "7-8",
                "8-9",
                "9-1");
        }

        /**
         * 测试获取一个边的所有"邻接边"
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#adjacentEdges(Object) Network.adjacentEdges(E)}
         * 方法可以获取网络中指定边的邻接边对象集合
         * </p>
         *
         * <p>
         * 所谓"邻接边", 即和指定边两个节点任意一个重合的其它边, 例如: 节点 {@code 2, 3} 组成的边 {@code 2-3}, 其"邻接边"可能为
         * {@code 1-2, 2-4, 2-5, 3-8, 3-6} 等
         * </p>
         */
        @Test
        void adjacentEdges_shouldGetAdjacentEdgesBetweenTwoNodes() {
            // 构建有向网络
            var network = datasource.buildNetwork(true, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取指定边的"邻接边"对象集合
            var adjacentEdges = network.adjacentEdges("2-3");

            // 确认指定边的邻接边集合
            then(adjacentEdges).containsExactly("1-2", "2-7", "2-4", "2-5", "3-8");
        }
    }

    @Nested
    class TestUndirectedNetwork {
        /**
         * 测试构建无向网络
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#nodes() Network.nodes()} 方法可以获取网络中的所有节点对象集合
         * </p>
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#edges() Network.edges()} 方法可以获取网络中所有边对象集合
         * </p>
         */
        @Test
        void build_shouldBuildDirectedNetwork() {
            // 构建无向网络
            var network = datasource.buildNetwork(false, ElementOrder.insertion(), ElementOrder.insertion());

            // 确认网络中包含的节点
            then(network.nodes()).containsExactlyInAnyOrderElementsOf(datasource.nodes());

            // 确认网络中包含的边对象
            then(network.edges()).containsExactlyInAnyOrder(
                "1-2",
                "2-3",
                "2-4",
                "2-5",
                "2-7",
                "3-8",
                "4-5",
                "4-6",
                "4-8",
                "5-6",
                "6-7",
                "7-8",
                "8-9",
                "9-1");
        }

        /**
         * 测试获取一个边的所有"邻接边"
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#adjacentEdges(Object) Network.adjacentEdges(E)}
         * 方法可以获取网络中指定边的邻接边对象集合
         * </p>
         *
         * <p>
         * 所谓"邻接边", 即和指定边两个节点任意一个重合的其它边, 例如: 节点 {@code 2, 3} 组成的边 {@code 2-3}, 其"邻接边"可能为
         * {@code 1-2, 2-4, 2-5, 3-8, 3-6} 等
         * </p>
         */
        @Test
        void adjacentEdges_shouldGetAdjacentEdgesBetweenTwoNodes() {
            // 构建无向网络
            var network = datasource.buildNetwork(false, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取指定边的"邻接边"对象集合
            var adjacentEdges = network.adjacentEdges("2-3");

            // 确认指定边的邻接边集合
            then(adjacentEdges).containsExactly("3-8", "2-4", "2-5", "2-7", "1-2");
        }
    }
}
