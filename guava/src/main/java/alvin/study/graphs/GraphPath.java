package alvin.study.graphs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Queues;
import com.google.common.graph.Graph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;

/**
 * 计算图路径的类型
 *
 * <p>
 * 本类型用于计算 {@link Graph} 类型中任意两个节点之间的路径
 * </p>
 */
public class GraphPath<N> {
    //
    private final Graph<N> graph;

    /**
     * 构造器, 设置 {@link Graph} 对象参数
     *
     * @param graph {@link Graph} 对象参数
     */
    public GraphPath(Graph<N> graph) {
        this.graph = graph;
    }

    /**
     * 获取图中两点之间的路径
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
     * @param fromNode 起始节点
     * @param toNode   终止节点
     * @return 包含路径的集合
     */
    public List<List<N>> getPaths(N fromNode, N toNode) {
        // 定义 BFS 算法所需的队列
        var que = new ArrayDeque<N>();
        // 将起始节点入队
        que.offer(fromNode);

        // 定义记录路径计算过程的 Map 对象, 其中 Key 为所存路径的最后一个节点, Value 为存储的路径
        var pathMap = new HashMap<N, List<N>>();
        // 存储第路径的第一个节点
        pathMap.put(fromNode, List.of(fromNode));

        // 保存路径结果的集合
        var results = new ArrayList<List<N>>();

        // 保存已访问节点, 已访问的节点将不再访问, 防止进入循环访问
        var visited = new HashSet<N>();
        // 向已访问节点列表中增加起始节点, 表示该节点已被访问
        visited.add(fromNode);

        // 循环, 直到队列为空, 表示所有节点均已被访问
        while (!que.isEmpty()) {
            // 获取队列头节点
            var top = que.poll();

            // 通过头节点获取最后一个访问节点为该节点的路径集合
            var paths = pathMap.get(top);
            if (paths == null) {
                continue;
            }

            // 逐个遍历该节点的后继节点
            for (var node : graph.successors(top)) {
                // 检查该节点是否未被访问过
                if (!visited.contains(node)) {
                    // 如果该后继节点已经为最后一个节点, 则保存该路径未一个结果
                    if (node.equals(toNode)) {
                        results.add(Stream.concat(paths.stream(), Stream.of(node)).toList());
                    } else {
                        // 将该后继节点加入路径, 该后继节点为 Key, 存储到路径 Map 中
                        pathMap.put(node, Stream.concat(paths.stream(), Stream.of(node)).toList());

                        // 将该后继节点入队, 以便后续继续访问其后继节点
                        que.offer(node);
                        // 将该后继节点加入已访问节点集合
                        visited.add(node);
                    }
                }
            }
        }

        return Collections.unmodifiableList(results);
    }

    @SuppressWarnings("unchecked")
    public List<N> getShortestPath(N fromNode, N toNode) {
        if (!(graph instanceof ValueGraph)) {
            throw new IllegalStateException();
        }

        var valueGraph = (ValueGraph<N, ?>) graph;

        var que = Queues.<N>newArrayDeque();
        que.offer(fromNode);

        while (!que.isEmpty()) {
            var node = que.poll();
            for (var subNode : valueGraph.successors(node)) {
                
            }
        }
    }
}
