package alvin.study.guava.graphs;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

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
    private GraphPaths() {
    }

    /**
     * 获取 {@link Graph} 图对象中两点之间的路径
     *
     * <p>
     * 本例通过广度优先算法 (BFS) 来计算两点之间的路径, 即按"层" (即每个节点的"后继"节点集合) 推进, 计算每层节点的连通性, 直到找到目标节点
     * </p>
     *
     * <p>
     * 为了保证递归可以正常结束, 已经访问过的节点不会再次访问 (通过 {@code visited} 集合), 因为是 BFS 算法, 所以如果一个节点被重复访问,
     * 其路径长度会比前一次长, 所以最终结果不是全部路径, 而是不重复路径, 且每条路径为其重复路径中访问节点数最少的路径
     * </p>
     *
     * @param graph    "图"对象
     * @param fromNode 起始节点
     * @param toNode   终止节点
     * @return 包含路径的集合
     */
    public static <N> @NotNull List<List<N>> getPaths(Graph<N> graph, N fromNode, N toNode) {
        // 定义 BFS 算法所需的队列
        // 并将起始节点入队
        var que = new ArrayDeque<N>();
        que.offer(fromNode);

        // 定义存储访问路径的 Map 对象, 该对象保存从指定节点到某个节点的路径
        // Key 为图中的某个节点, Value 为从 fromNode 到该节点的路径集合
        var pathMap = MultimapBuilder.hashKeys().linkedListValues().<N, N>build();
        pathMap.put(fromNode, fromNode);

        // 存储已访问节点的集合, 避免访问过的路径被重复访问
        var visited = Sets.<N>newHashSet();

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
                    .filter(s -> !visited.contains(s))
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

            // 将该节点加入已访问节点中
            visited.add(node);
        }

        return results;
    }

    /**
     * 求 {@link ValueGraph} 类型图对象指定两个节点间的"最短路径"
     *
     * <p>
     * 所谓"最短路径", 指的是"有权图", 从节点 {@code A} 到节点 {@code B}, 如果节点连通, 则这些路径中, "边权重"之和最小的那个
     * </p>
     *
     * <p>
     * 本例演示了 "狄克斯特拉算法 (Dijkstra)" 求最短路径, 具体算法描述如下:
     * <ol>
     * <li>
     * 设置两个关键集合, 一个用于存储"未访问"的节点 (本例使用"优先队列"), 一个用于存储"已访问"的节点 (本例使用"哈希表"); 另有一个集合,
     * 用于存储每个节点到起始节点的路径和距离 (本例使用"字典")
     * </li>
     * <li>
     * 算法初始化阶段, 将图中的节点都存入字典中, 每个节点对应的到起始节点的路径为"空", 到起始节点的距离为"无穷大",
     * 表示各个节点的路径和距离尚未计算; 接下来, 将起始节点放入优先队列
     * </li>
     * <li>
     * 算法执行过程是一个迭代过程, 每次迭代从优先队列中取得到起始节点距离最小的节点, 获取其"后继节点"集合, 即从"起始节点"开始,
     * 沿着其"后继节点" (以及后继节点的"后继节点"), 逐步访问, 直到所有节点均被访问到
     * </li>
     * <li>
     * 对于每一个后继节点, 如果其未被访问过, 则计算"当前迭代的节点的距离 + 该节点到该后继节点的距离 (边权重)",
     * 该距离相当于起始节点到该后继节点的距离, 如果此距离小于"该后继节点已有的距离" (无穷大或该后继节点从另一条路径到起始节点的距离),
     * 则认为此次计算结果为该后继节点的"最短距离", 并将此结果和该后继节点关联
     * </li>
     * <li>
     * 不断迭代, 直到图中所有节点都被访问一次后, 每个节点的"最短路径"就都已经被计算完毕
     * </li>
     * </ol>
     * </p>
     *
     * @param <N>        图节点类型
     * @param <E>        图中"边"权值类型
     * @param graph      {@link ValueGraph} 类型图对象, 具有"边"权重值
     * @param fromNode   起始节点
     * @param toNode     终止节点
     * @param distanceFn 将"边"权重值转换为"距离值"的函数对象
     * @return {@link Optional} 类型对象, 如果为空, 表示给定的两个节点间不连通; 否则包含一个 {@link PathValue} 类型对象,
     * 记录了路径和路径距离
     */
    public static <N, E> Optional<PathValue<N>> getShortestPath(
            @NotNull ValueGraph<N, E> graph,
            N fromNode,
            N toNode,
            ToIntFunction<E> distanceFn) {
        // 记录节点和路径值关系的 Map 对象
        var nodeMap = Maps.<N, PathValue<N>>newHashMap();

        // 将图中的所有节点放入 Map 对象中, 并对 fromNode 对应的 Value 设置初始值
        graph.nodes().forEach(n -> nodeMap.put(n, new PathValue<>()));
        nodeMap.get(fromNode).reset(null, 0);

        // 定义优先队列, 用于从一系列候选节点中获取距离较小的那个
        // 将起始节点入队
        var queue = new PriorityQueue<N>((n1, n2) -> {
            // 获取待比较的两个节点路径值
            var v1 = nodeMap.get(n1);
            var v2 = nodeMap.get(n2);

            // 比较两个节点到起始节点的距离值
            return Integer.compare(v1.getDistance(), v2.getDistance());
        });
        queue.offer(fromNode);

        // 保存已访问节点的集合, 用于避免路径的重复查找
        var visited = Sets.<N>newHashSet();

        // 持续跌倒, 直到优先队列为空, 表示所有的节点都被访问过
        while (!queue.isEmpty()) {
            // 从优先队列中获取距离最小的节点作为当前节点
            var topNode = queue.poll();

            // 从 Map 对象中获取当前节点的路径值
            var topNodeValue = nodeMap.get(topNode);

            // 遍历当前节点的后继节点
            graph.successors(topNode)
                    .stream()
                    // 过滤掉已经访问过的节点
                    .filter(s -> !visited.contains(s))
                    // 获取该后继节点的边权重值, 并对该节点进行处理
                    .forEach(s -> graph.edgeValue(topNode, s).ifPresent(v -> {
                        // 将边权重值转化为距离值
                        var edgeDistance = distanceFn.applyAsInt(v);
                        // 获取后继节点的路径值
                        var successorValue = nodeMap.get(s);

                        // 判断当前节点到后继节点形成的路径距离是否小于该后继节点已经获得的路径距离
                        if (topNodeValue.getDistance() + edgeDistance < successorValue.getDistance()) {
                            // 形成新的路径, 及到当前节点的路径加上当前节点, 为到该后继节点的路径
                            var path = Lists.newLinkedList(topNodeValue.getPath());
                            path.add(topNode);

                            // 修改后继节点的路径值, 包括路径和距离
                            successorValue.reset(path, topNodeValue.getDistance() + edgeDistance);
                        }

                        // 将该后继节点入队
                        queue.offer(s);
                    }));

            // 将当前节点存入已访问集合中
            visited.add(topNode);
        }

        // 获取终止节点上到起始节点的最短路径和距离
        var result = nodeMap.get(toNode);
        if (result.getPath().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.copy(toNode));
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
        public List<N> getPath() {
            return path == null ? List.of() : path;
        }

        /**
         * 获取路径距离
         *
         * @return 路径的距离
         */
        public int getDistance() {
            return distance;
        }

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
}
