package alvin.study.guava.graphs;

import alvin.study.guava.graphs.GraphsDatasource.Edge;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

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
    private final GraphsDatasource<Integer, String> datasource = new GraphsDatasource<>(
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
            Edge.of(9, 1, "9-1")
    );

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
        void adjacentEdges_shouldGetAdjacentEdgesBetweenTwoNodesInDirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(true, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取指定边的"邻接边"对象集合
            var adjacentEdges = network.adjacentEdges("2-3");

            // 确认指定边的邻接边集合
            then(adjacentEdges).containsExactly("1-2", "2-7", "2-4", "2-5", "3-8");

            // 在 2, 3 节点间在加入一条平行边
            network.addEdge(2, 3, "2-3a");

            // 获取指定边的"邻接边"对象集合
            adjacentEdges = network.adjacentEdges("2-3");

            // 确认之前添加的平行边也被查询到
            then(adjacentEdges).containsExactly("1-2", "2-4", "2-5", "2-7", "2-3a", "3-8");
        }

        /**
         * 获取两个节点之间的连接边对象集合
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#edgeConnecting(Object, Object) Network.edgeConnecting(N, N)}
         * 方法可以获取网络中两个节点之间的边对象
         * </p>
         */
        @Test
        void edgesConnecting_shouldGetConnectingEdgesBetweenTwoNodesInDirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(true, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取两个节点间的连接边, 此时能获取一个边对象
            var edges = network.edgesConnecting(2, 3);
            then(edges).containsExactly("2-3");

            // 在相同的两个节点间再增加一个连接边对象
            network.addEdge(2, 3, "2-3a");

            // 重新获取两个节点间的连接边, 此时可以获取两个边对象
            edges = network.edgesConnecting(2, 3);
            then(edges).containsExactly("2-3", "2-3a");
        }

        /**
         * 获取两个节点见的"唯一"的边对象
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#edgeConnectingOrNull(Object, Object)
         * Network.edgeConnectingOrNull(N, N)} 方法可以获取两个节点间的"唯一边对象"
         * </p>
         *
         * <p>
         * 所谓"唯一边对象", 即作为参数的两个节点间只能有一个"边对象", 如果有多个平行边对象, 则调用该方法会抛出异常
         * </p>
         */
        @Test
        void edgeConnectingOrNull_shouldGetSingleConnectingEdgeBetweenTwoNodesInDirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(true, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取两个节点间的边对象
            var edge = network.edgeConnectingOrNull(2, 3);
            then(edge).isEqualTo("2-3");

            // 在两个节点间增加新的边对象, 确认再次获取边对象会抛出异常
            network.addEdge(2, 3, "2-3a");
            thenThrownBy(() -> network.edgeConnectingOrNull(2, 3)).isInstanceOf(IllegalArgumentException.class);
        }

        /**
         * 获取网络中指定节点的邻接边
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#incidentEdges(Object) Network.incidentEdges(N)}
         * 方法可以获取网络中和指定节点相关的"邻接边"对象集合
         * </p>
         *
         * <p>
         * 所谓"邻接边", 即以指定节点为起点 (或终点) 的点之间的边对象
         * </p>
         */
        @Test
        void incidentEdges_shouldGetIncidentEdgesByNodeInDirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(true, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取指定节点的邻接边
            var edges = network.incidentEdges(2);
            then(edges).containsExactly("1-2", "2-3", "2-7", "2-4", "2-5");

            // 添加一条平行边
            network.addEdge(2, 3, "2-3a");

            // 再次获取相同节点的邻接边, 确认新加入的边对象被查询到
            edges = network.incidentEdges(2);
            then(edges).containsExactly("1-2", "2-3", "2-4", "2-5", "2-7", "2-3a");
        }

        /**
         * 获取网络中指定边的邻接节点
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#incidentNodes(Object) Network.incidentNodes(E)}
         * 方法可以根据所给的边对象, 获取该边的两个连接节点
         * </p>
         */
        @Test
        void incidentNodes_shouldGetIncidentNodesByEdgeInDirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(true, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取指定边的邻接节点
            var nodes = network.incidentNodes("2-3");
            then(nodes).isEqualTo(EndpointPair.ordered(2, 3));

            // 在节点间增加一个新的平行边对象
            network.addEdge(2, 3, "2-3a");

            // 通过新加入的边对象, 获取其邻接节点
            nodes = network.incidentNodes("2-3a");
            then(nodes).isEqualTo(EndpointPair.ordered(2, 3));
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
        void adjacentEdges_shouldGetAdjacentEdgesBetweenTwoNodesInUndirectedNetwork() {
            // 构建无向网络
            var network = datasource.buildNetwork(false, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取指定边的"邻接边"对象集合
            var adjacentEdges = network.adjacentEdges("2-3");

            // 确认指定边的邻接边集合
            then(adjacentEdges).containsExactly("3-8", "2-4", "2-5", "2-7", "1-2");

            // 在 2, 3 节点间在加入一条平行边
            network.addEdge(2, 3, "2-3a");

            // 获取指定边的"邻接边"对象集合
            adjacentEdges = network.adjacentEdges("2-3");

            // 确认之前添加的平行边也被查询到
            then(adjacentEdges).containsExactly("3-8", "2-3a", "2-4", "2-5", "2-7", "1-2");
        }

        /**
         * 获取两个节点之间的连接边对象集合
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#edgeConnecting(Object, Object) Network.edgeConnecting(N, N)}
         * 方法可以获取网络中两个节点之间的边对象
         * </p>
         */
        @Test
        void edgesConnecting_shouldGetConnectingEdgesBetweenTwoNodesInUndirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(false, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取两个节点间的连接边, 此时能获取一个边对象
            var edges = network.edgesConnecting(2, 3);
            then(edges).containsExactly("2-3");

            // 在相同的两个节点间再增加一个连接边对象
            network.addEdge(2, 3, "2-3a");

            // 重新获取两个节点间的连接边, 此时可以获取两个边对象
            edges = network.edgesConnecting(2, 3);
            then(edges).containsExactly("2-3", "2-3a");
        }

        /**
         * 获取两个节点见的"唯一"的边对象
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#edgeConnectingOrNull(Object, Object)
         * Network.edgeConnectingOrNull(N, N)} 方法可以获取两个节点间的"唯一边对象"
         * </p>
         *
         * <p>
         * 所谓"唯一边对象", 即作为参数的两个节点间只能有一个"边对象", 如果有多个平行边对象, 则调用该方法会抛出异常
         * </p>
         */
        @Test
        void edgeConnectingOrNull_shouldGetSingleConnectingEdgeBetweenTwoNodesInUndirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(false, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取两个节点间的边对象
            var edge = network.edgeConnectingOrNull(2, 3);
            then(edge).isEqualTo("2-3");

            // 在两个节点间增加新的边对象, 确认再次获取边对象会抛出异常
            network.addEdge(2, 3, "2-3a");
            thenThrownBy(() -> network.edgeConnectingOrNull(2, 3)).isInstanceOf(IllegalArgumentException.class);
        }

        /**
         * 获取网络中指定节点的邻接边
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#incidentEdges(Object) Network.incidentEdges(N)}
         * 方法可以获取网络中和指定节点相关的"邻接边"对象集合
         * </p>
         *
         * <p>
         * 所谓"邻接边", 即以指定节点为起点 (或终点) 的点之间的边对象
         * </p>
         */
        @Test
        void incidentEdges_shouldGetIncidentEdgesByNodeInUndirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(false, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取指定节点的邻接边
            var edges = network.incidentEdges(2);
            then(edges).containsExactly("2-3", "2-4", "2-5", "2-7", "1-2");

            // 添加一条平行边
            network.addEdge(2, 3, "2-3a");

            // 再次获取相同节点的邻接边, 确认新加入的边对象被查询到
            edges = network.incidentEdges(2);
            then(edges).containsExactly("2-3", "2-4", "2-5", "2-7", "1-2", "2-3a");
        }

        /**
         * 获取网络中指定边的邻接节点
         *
         * <p>
         * 通过 {@link com.google.common.graph.Network#incidentNodes(Object) Network.incidentNodes(E)}
         * 方法可以根据所给的边对象, 获取该边的两个连接节点
         * </p>
         */
        @Test
        void incidentNodes_shouldGetIncidentNodesByEdgeInUndirectedNetwork() {
            // 构建有向网络
            var network = datasource.buildNetwork(false, ElementOrder.insertion(), ElementOrder.insertion());

            // 获取指定边的邻接节点
            var nodes = network.incidentNodes("2-3");
            then(nodes).isEqualTo(EndpointPair.unordered(2, 3));

            // 在节点间增加一个新的平行边对象
            network.addEdge(2, 3, "2-3a");

            // 通过新加入的边对象, 获取其邻接节点
            nodes = network.incidentNodes("2-3a");
            then(nodes).isEqualTo(EndpointPair.unordered(2, 3));
        }
    }
}
