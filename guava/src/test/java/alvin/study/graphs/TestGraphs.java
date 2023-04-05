package alvin.study.graphs;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableGraph;

import alvin.study.graphs.GraphsDatasource.Edge;

class TestGraphs {
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

    @Nested
    class TestDirected {
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
            var graph = datasource.buildGraph(true, ElementOrder.insertion());

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
         * 获取有向图中任意两个节点是否连通
         *
         * <p>
         * 通过 {@link MutableGraph#hasEdgeConnecting(Object, Object) MutableGraph.hasEdgeConnecting(T, T)}
         * 方法可以判断两个节点是否连通, 如果连通则返回 {@code true}
         * </p>
         *
         * <p>
         * 对于有向图的任意两个节点 {@code A} 和 {@code B}, 如果有 {@code A → B} 的连通成立, 则 {@code B → A} 的连通必然不成立
         * </p>
         */
        @Test
        void hasEdgeConnecting_shouldCheckedIfHasConnectedEdgeBetweenTwoDirectedNodes() {
            // 构建有向图
            var graph = datasource.buildGraph(true, ElementOrder.insertion());

            // 确认具有边的两个节点是连通的
            var connected = graph.hasEdgeConnecting(2, 5);
            then(connected).isTrue();

            // 确认在有向图中, 连通的两个节点反过来不能连通
            connected = graph.hasEdgeConnecting(5, 2);
            then(connected).isFalse();

            // 确认跨两个边的节点不具备连通性
            connected = graph.hasEdgeConnecting(2, 8);
            then(connected).isFalse();
        }

        /**
         * 获取有向图中某个节点的前趋节点列表
         *
         * <p>
         * 通过 {@link MutableGraph#predecessors(Object) MutableGraph.predecessors(T)} 方法可以获取由指定某个节点的前趋节点组成的集合
         * </p>
         *
         * <p>
         * 对于有向图来说, 以指定节点为后继节点组成边的节点为该节点的前趋节点
         * </p>
         */
        @Test
        void predecessors_shouldGetPredecessorsNodesOfDirectedGraph() {
            // 构建有向图
            var graph = datasource.buildGraph(true, ElementOrder.insertion());

            // 确认有向图中各个节点的前趋节点
            for (var node : graph.nodes()) {
                var predecessors = graph.predecessors(node);
                then(predecessors).containsExactlyInAnyOrderElementsOf(datasource.preNeighbors(node));
            }
        }

        /**
         * 获取有向图中某个节点的后继节点列表
         *
         * <p>
         * 通过 {@link MutableGraph#successors(Object) MutableGraph.successors(T)} 方法可以获取由指定某个节点的后继节点组成的集合
         * </p>
         *
         * <p>
         * 对于有向图来说, 以指定节点为前趋节点组成边的节点为该节点的后继节点
         * </p>
         */
        @Test
        void successors_shouldGetSuccessorsNodesOfDirectedGraph() {
            // 构建有向图
            var graph = datasource.buildGraph(true, ElementOrder.insertion());

            // 确认有向图中各个节点的后继节点
            for (var node : graph.nodes()) {
                var successors = graph.successors(node);
                then(successors).containsExactlyInAnyOrderElementsOf(datasource.postNeighbors(node));
            }
        }

        /**
         * 获取有向图中指定节点的邻接节点
         *
         * <p>
         * 通过 {@link MutableGraph#adjacentNodes(Object) MutableGraph.adjacentNodes(T)} 方法可以获取指定某个节点的邻接节点
         * </p>
         *
         * <p>
         * 一个图中, 某个节点的邻接节点即其前趋和后继节点
         * </p>
         */
        @Test
        void adjacentNodes_shouldGetAdjacentNodesOfDirectedGraph() {
            // 构建有向图
            var graph = datasource.buildGraph(true, ElementOrder.insertion());

            // 获取有向图中各个节点的邻接节点, 确认由其前趋和后继节点组成
            for (var node : graph.nodes()) {
                var adjacentNodes = graph.adjacentNodes(node);
                then(adjacentNodes).containsExactlyInAnyOrderElementsOf(datasource.neighbors(node));
            }
        }

        /**
         * 求有向图指定节点的"度"
         *
         * <p>
         * "度"分为"入度"和"出度", "入度"指该节点的所有前趋节点数量, "出度"指该节点的所有后续节点的数量, "度"即该阶段的"出度"和"入度"之和,
         * 即该阶段的邻接节点数量
         * </p>
         *
         * <p>
         * 通过 {@link MutableGraph#inDegree(Object) MutableGraph.inDegree(T)} 方法可以获得指定节点的"入度"
         * </p>
         *
         * <p>
         * 通过 {@link MutableGraph#outDegree(Object) MutableGraph.outDegree(T)} 可以获得指定节点的"出度"
         * </p>
         *
         * <p>
         * 通过 {@link MutableGraph#degree(Object) MutableGraph.degree(T)} 可以获得指定节点的"度"
         * </p>
         */
        @Test
        void degree_shouldGetNodeDegreesOfDirectedGraph() {
            // 构建有向图
            var graph = datasource.buildGraph(true, ElementOrder.insertion());

            // 获取各个节点的"度" (包括"入度", "出度")
            for (var node : graph.nodes()) {
                then(graph.inDegree(node)).isEqualTo(datasource.preNeighbors(node).size());
                then(graph.outDegree(node)).isEqualTo(datasource.postNeighbors(node).size());
                then(graph.degree(node)).isEqualTo(datasource.neighbors(node).size());
            }
        }

        /**
         * 删除指定的节点
         *
         * <p>
         * 通过 {@link MutableGraph#removeNode(Object) MutableGraph.removeNode(T)} 方法可以从有向图中删除一个节点
         * </p>
         *
         * <p>
         * 当删除节点后, 由该节点组成的边也将不复存在
         * </p>
         */
        @Test
        void removeNode_shouldRemoveNodeFromDirectedGraph() {
            // 构建有向图
            var graph = datasource.buildGraph(true, ElementOrder.insertion());

            // 删除节点
            then(graph.removeNode(8)).isTrue();

            // 确认有向图中该节点已经被删除
            then(graph.nodes()).doesNotContain(8);

            // 确认有向图中不包含包含已删除节点的边
            for (var edge : graph.edges()) {
                then(edge.nodeU()).isNotEqualTo(8);
                then(edge.nodeV()).isNotEqualTo(8);
            }
        }

        /**
         * 获取有向图中任意两节点之间的路径
         *
         * <p>
         * 通过 {@link GraphPath#getPaths(Object, Object) GraphPath.getPaths(T, T)} 方法可以计算所给的两个节点之间可连通的路径
         * </p>
         *
         * <p>
         * 对于有向图来说, 路径只能向一个方向寻找, 不存在反向路径
         * </p>
         */
        @Test
        void getPaths_shouldGetPathsBetweenTwoNodesOfDirectedGraph() {
            // 构建有向图
            var graph = datasource.buildGraph(true, ElementOrder.insertion());

            var graphPath = new GraphPath<>(graph);
            var paths = graphPath.getPaths(1, 8);
            then(paths).containsExactlyInAnyOrder(
                List.of(1, 2, 3, 8),
                List.of(1, 2, 4, 8),
                List.of(1, 2, 7, 8));
        }
    }

    @Nested
    class TestUndirected {
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
            var graph = datasource.buildGraph(false, ElementOrder.insertion());

            // 确认无向图包含的节点
            then(graph.nodes()).containsExactlyInAnyOrderElementsOf(datasource.nodes());

            // 确认无向图包含的边, 无向图的边由 EndpointPair.Unordered 类型对象表示
            // 对于无向图来说, 边的节点顺序对调之后, 仍和之前相等
            then(graph.edges()).containsExactlyInAnyOrderElementsOf(datasource.unOrderedEdges(false));
            then(graph.edges()).containsExactlyInAnyOrderElementsOf(datasource.unOrderedEdges(true));
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
            // 构建无向图
            var graph = datasource.buildGraph(false, ElementOrder.insertion());

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

        /**
         * 获取无向图中某个节点的前趋节点列表
         *
         * <p>
         * 通过 {@link MutableGraph#predecessors(Object) MutableGraph.predecessors(T)} 方法可以获取由指定某个节点的前趋节点组成的集合
         * </p>
         *
         * <p>
         * 对于无向图来说, 凡是能和指定节点连接的节点, 都属于该节点的前趋节点, 即无向图某个节点的前趋节点同时也是其后继节点
         * </p>
         */
        @Test
        void predecessors_shouldGetPredecessorsNodesOfUndirectedGraph() {
            // 构建无向图
            var graph = datasource.buildGraph(false, ElementOrder.insertion());

            // 确认有向图中各个节点的前趋节点
            for (var node : graph.nodes()) {
                var predecessors = graph.predecessors(node);
                then(predecessors).containsExactlyInAnyOrderElementsOf(datasource.neighbors(node));
            }
        }

        /**
         * 获取无向图中某个节点的后继节点列表
         *
         * <p>
         * 通过 {@link MutableGraph#successors(Object) MutableGraph.successors(T)} 方法可以获取由指定某个节点的后继节点组成的集合
         * </p>
         *
         * <p>
         * 对于无向图来说, 凡是能和指定节点连接的节点, 都属于该节点的后继节点, 即无向图某个节点的后继节点同时也是其前趋节点
         * </p>
         */
        @Test
        void successors_shouldGetSuccessorsNodesOfUndirectedGraph() {
            // 构建无向图
            var graph = datasource.buildGraph(false, ElementOrder.insertion());

            // 确认有向图中各个节点的前趋节点
            for (var node : graph.nodes()) {
                var predecessors = graph.successors(node);
                then(predecessors).containsExactlyInAnyOrderElementsOf(datasource.neighbors(node));
            }
        }

        /**
         * 获取无向图中指定节点的邻接节点
         *
         * <p>
         * 通过 {@link MutableGraph#adjacentNodes(Object) MutableGraph.adjacentNodes(T)} 方法可以获取指定某个节点的邻接节点
         * </p>
         *
         * <p>
         * 一个图中, 某个节点的邻接节点即其前趋和后继节点
         * </p>
         */
        @Test
        void adjacentNodes_shouldGetAdjacentNodesOfUndirectedGraph() {
            // 构建无向图
            var graph = datasource.buildGraph(false, ElementOrder.insertion());

            // 确认有向图中各个节点的前趋节点
            for (var node : graph.nodes()) {
                var predecessors = graph.adjacentNodes(node);
                then(predecessors).containsExactlyInAnyOrderElementsOf(datasource.neighbors(node));
            }
        }

        /**
         * 求无向图指定节点的"度"
         *
         * <p>
         * "度"分为"入度"和"出度", "入度"指该节点的所有前趋节点数量, "出度"指该节点的所有后续节点的数量, "度"即该阶段的"出度"和"入度"之和,
         * 即该阶段的邻接节点数量
         * </p>
         *
         * <p>
         * 通过 {@link MutableGraph#inDegree(Object) MutableGraph.inDegree(T)} 方法可以获得指定节点的"入度"
         * </p>
         *
         * <p>
         * 通过 {@link MutableGraph#outDegree(Object) MutableGraph.outDegree(T)} 可以获得指定节点的"出度"
         * </p>
         *
         * <p>
         * 通过 {@link MutableGraph#degree(Object) MutableGraph.degree(T)} 可以获得指定节点的"度"
         * </p>
         *
         * <p>
         * 在无向图中, 一个节点的"度"和其"入度"以及"出度"的值相同
         * </p>
         */
        @Test
        void degree_shouldGetNodeDegreeOfUndirectedGraph() {
            // 构建无向图
            var graph = datasource.buildGraph(false, ElementOrder.insertion());

            // 获取各个节点的"度" (包括"入度", "出度")
            for (var node : graph.nodes()) {
                then(graph.inDegree(node)).isEqualTo(datasource.neighbors(node).size());
                then(graph.outDegree(node)).isEqualTo(datasource.neighbors(node).size());
                then(graph.degree(node)).isEqualTo(datasource.neighbors(node).size());
            }
        }

        /**
         * 删除指定的节点
         *
         * <p>
         * 通过 {@link MutableGraph#removeNode(Object) MutableGraph.removeNode(T)} 方法可以从有向图中删除一个节点
         * </p>
         *
         * <p>
         * 当删除节点后, 由该节点组成的边也将不复存在
         * </p>
         */
        @Test
        void removeNode_shouldRemoveNodeFromDirectedGraph() {
            // 构建无向图
            var graph = datasource.buildGraph(false, ElementOrder.insertion());

            // 删除节点
            then(graph.removeNode(8)).isTrue();

            // 确认有向图中该节点已经被删除
            then(graph.nodes()).doesNotContain(8);

            // 确认有向图中不包含包含已删除节点的边
            for (var edge : graph.edges()) {
                then(edge.nodeU()).isNotEqualTo(8);
                then(edge.nodeV()).isNotEqualTo(8);
            }
        }

        /**
         * 获取无向图中任意两节点之间的路径
         *
         * <p>
         * 通过 {@link GraphPath#getPaths(Object, Object) GraphPath.getPaths(T, T)} 方法可以计算所给的两个节点之间可连通的路径
         * </p>
         *
         * <p>
         * 对于无向图来说, 路径可以向两个方向查询, 即一个节点的正反两边都可以形成联通的路径
         * </p>
         */
        @Test
        void getPaths_shouldGetPathsBetweenTwoNodesOfUndirectedGraph() {
            // 构建无向图
            var graph = datasource.buildGraph(false, ElementOrder.insertion());

            var graphPath = new GraphPath<>(graph);
            var paths = graphPath.getPaths(1, 8);
            then(paths).containsExactlyInAnyOrder(
                List.of(1, 9, 8),
                List.of(1, 2, 3, 8),
                List.of(1, 2, 4, 8),
                List.of(1, 2, 7, 8));
        }
    }
}
