package alvin.study.graphs;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.google.common.graph.Graph;
import com.google.common.graph.ValueGraph;

/**
 * 计算图路径的类型
 *
 * <p>
 * 本类型用于计算"图"类型对象中任意两个节点之间的路径
 * </p>
 */
public final class GraphPaths {
    private GraphPaths() {}

    /**
     * 获取 {@link Graph} 图对象中两点之间的路径
     *
     * <p>
     * 本例通过广度优先算法 (BFS) 来计算两点之间的路径, 即按"层" (即每个节点的"后继"节点集合) 推进, 计算每层节点的连通性, 直到找到目标节点
     * </p>
     *
     * <p>
     * 为了保证递归可以正常结束, 已经访问过的节点不会再次访问, 因为是 BFS 算法, 所以如果一个节点被重复访问, 其路径长度会比前一次长,
     * 所以最终结果不是全部路径, 而是不重复路径, 且每条路径为其重复路径中访问节点数最少的路径
     * </p>
     *
     * @param graph    "图"对象
     * @param fromNode 起始节点
     * @param toNode   终止节点
     * @return 包含路径的集合
     */
    public static <N> List<List<N>> getPaths(Graph<N> graph, N fromNode, N toNode) {
        // 定义 BFS 算法所需的队列
        // 并将起始节点入队
        var que = new ArrayDeque<N>();
        que.offer(fromNode);

        // 定义存储访问路径的 Map 对象, 该对象保存从指定节点到某个节点的路径
        // Key 为图中的某个节点, Value 为从 fromNode 到该节点的路径集合
        var pathMap = MultimapBuilder.hashKeys().linkedListValues().<N, N>build();
        pathMap.put(fromNode, fromNode);

        // 记录最终结果的集合
        var results = Lists.<List<N>>newArrayList();

        // 借助队列进行深度优先访问
        while (!que.isEmpty()) {
            // 获取队列头节点
            var node = que.poll();

            // 获取到该节点的路径
            var path = pathMap.get(node);
            if (path.isEmpty()) {
                continue;
            }

            // 遍历该节点的后继节点
            graph.successors(node)
                    .stream()
                    // 过滤掉已经访问过的节点
                    .filter(s -> !pathMap.containsKey(s))
                    // 对后继节点进行处理
                    .forEach(s -> {
                        if (s.equals(toNode)) {
                            // 如果该节点已经为最终节点, 则记录一条路径
                            results.add(Stream.concat(path.stream(), Stream.of(s)).toList());
                        } else {
                            // 记录从起始节点到该后继节点的路径
                            // 记录到当前节点的路径
                            pathMap.putAll(s, path);
                            // 添加到后继节点的路径
                            pathMap.put(s, s);

                            // 将该后继节点入队, 以便后续继续访问其后继节点
                            que.offer(s);
                        }
                    });
        }
        return results;
    }

    /**
     * 用于记录路径距离的类型
     *
     * <p>
     * 对于求"最短路径", 需要记录路径和该条路径的距离
     * </p>
     */
    public static class PathValue<N> implements Comparable<PathValue<N>> {
        // 路径集合, 记录路径上经过的节点
        private List<N> path;

        // 该路径的距离, 默认为无穷大, 表示该路径距尚未连通
        private int distance = Integer.MAX_VALUE;

        @Override
        public int compareTo(PathValue<N> o) {
            return Integer.compare(distance, o.distance);
        }

        /**
         * 获取路径
         *
         * @return 保存路径的集合, 如果为空, 表示路径不存在
         */
        public List<N> getPath() { return path == null ? List.of() : path; }

        /**
         * 获取路径距离
         *
         * @return 路径的距离
         */
        public int getDistance() { return distance; }

        /**
         * 设置路径
         *
         * <p>
         * 在计算过程中, 如果形成路径, 或者找到距离更短的路径, 则需要重新设置路径
         * </p>
         *
         * @param path 新路径
         */
        void reset(List<N> path, int distance) {
            this.path = path;
            this.distance = distance;
        }

        /**
         * 复制当前对象, 并在路径中增加终止节点
         *
         * @param lastNode 终止节点
         * @return 当前对象的拷贝, 且路径中增加了终结节点
         */
        PathValue<N> copy(N lastNode) {
            var val = new PathValue<N>();
            val.distance = this.distance;
            val.path = Stream.concat(getPath().stream(), Stream.of(lastNode)).toList();
            return val;
        }
    }

    /**
     * 求 {@link ValueGraph} 类型图对象指定两个节点间的"最短路径"
     *
     * @param <N>        图节点类型
     * @param <E>        图中"边"权值类型
     * @param graph      {@link ValueGraph} 类型图对象, 具有"边"权重值
     * @param fromNode   起始节点
     * @param toNode     终止节点
     * @param distanceFn 将"边"权重值转换为"距离值"的函数对象
     * @return {@link Optional} 类型对象, 如果为空, 表示给定的两个节点间不连通; 否则包含一个 {@link PathValue} 类型对象,
     *         记录了路径和路径距离
     */
    public static <N, E> Optional<PathValue<N>> getShortestPath(
            ValueGraph<N, E> graph, N fromNode, N toNode, ToIntFunction<E> distanceFn) {
        // 记录节点和路径值关系的 Map 对象
        var nodeMap = Maps.<N, PathValue<N>>newHashMap();

        // 将图中的所有节点放入 Map 对象中, 并对 fromNode 对应的 Value 设置初始值
        graph.nodes().forEach(n -> nodeMap.put(n, new PathValue<>()));
        nodeMap.get(fromNode).reset(null, 0);

        // 定义优先队列, 用于从一系列候选节点中获取距离较小的那个
        var queue = new PriorityQueue<N>((n1, n2) -> {
            var v1 = nodeMap.get(n1);
            var v2 = nodeMap.get(n2);
            return Integer.compare(v1.getDistance(), v2.getDistance());
        });
        queue.offer(fromNode);

        var visited = Sets.<N>newHashSet();

        while (!queue.isEmpty()) {
            var topNode = queue.poll();
            var topNodeValue = nodeMap.get(topNode);

            graph.successors(topNode)
                    .stream()
                    .filter(s -> !visited.contains(s))
                    .forEach(s -> graph.edgeValue(topNode, s).ifPresent(v -> {
                        var edgeDistance = distanceFn.applyAsInt(v);
                        var successorValue = nodeMap.get(s);

                        if (topNodeValue.getDistance() + edgeDistance < successorValue.getDistance()) {
                            var path = Lists.newLinkedList(topNodeValue.getPath());
                            path.add(topNode);

                            successorValue.reset(path, topNodeValue.getDistance() + edgeDistance);
                        }

                        queue.offer(s);
                    }));

            visited.add(topNode);
        }

        var result = nodeMap.get(toNode);
        if (result.getPath().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.copy(toNode));
    }
}
