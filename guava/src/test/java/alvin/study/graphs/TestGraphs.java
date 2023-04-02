package alvin.study.graphs;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;

class TestGraphs {
    /**
     *
     * <p>
     * <img src="assets/undirected_graph.png"/>
     * </p>
     */
    @Test
    void undirected_shouldBuildUndirectedGraphs() {
        var graph = GraphBuilder.undirected()
                .nodeOrder(ElementOrder.<Integer>insertion())
                .<Integer>build();

        then(graph.nodeOrder()).isEqualTo(ElementOrder.<Integer>insertion());

        graph.putEdge(1, 2);
        graph.putEdge(2, 3);
        graph.putEdge(2, 4);
        graph.putEdge(2, 5);
        graph.putEdge(2, 7);
        graph.putEdge(3, 8);
        graph.putEdge(4, 5);
        graph.putEdge(4, 6);
        graph.putEdge(4, 8);
        graph.putEdge(5, 6);
        graph.putEdge(6, 7);
        graph.putEdge(7, 8);

        graph.addNode(9);
        graph.putEdge(8, 9);

        then(graph.nodes()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 7, 8, 6, 9);

        then(graph.edges()).containsExactlyInAnyOrder(
            EndpointPair.unordered(1, 2),
            EndpointPair.unordered(2, 3),
            EndpointPair.unordered(2, 4),
            EndpointPair.unordered(2, 5),
            EndpointPair.unordered(2, 7),
            EndpointPair.unordered(3, 8),
            EndpointPair.unordered(4, 8),
            EndpointPair.unordered(4, 5),
            EndpointPair.unordered(4, 6),
            EndpointPair.unordered(5, 6),
            EndpointPair.unordered(7, 8),
            EndpointPair.unordered(6, 7),
            EndpointPair.unordered(8, 9));

        then(graph.edges()).containsExactlyInAnyOrder(
            EndpointPair.unordered(2, 1),
            EndpointPair.unordered(3, 2),
            EndpointPair.unordered(4, 2),
            EndpointPair.unordered(2, 5),
            EndpointPair.unordered(7, 2),
            EndpointPair.unordered(8, 3),
            EndpointPair.unordered(8, 4),
            EndpointPair.unordered(5, 4),
            EndpointPair.unordered(6, 4),
            EndpointPair.unordered(6, 5),
            EndpointPair.unordered(8, 7),
            EndpointPair.unordered(7, 6),
            EndpointPair.unordered(9, 8));
    }
}
