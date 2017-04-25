package jpm.lib.graph

import jpm.lib.graph.algorithms.AStarAlgorithm
import jpm.lib.graph.algorithms.DijkstraAlgorithm
import jpm.lib.graph.WeightedGraph
import jpm.lib.graph.WeightedGraph.Node
import jpm.lib.graph.WeightedGraph.Arc
import jpm.lib.math.DoubleVector2D
import org.junit.Test

/**
 * Created by jm on 22/03/17.
 *
 */
class DijkstraAlgorithmTest {

    @Test
    fun test() {
        // does not make sense all the points have the same coordinate, but for this algorithm is the same
        val node0 = Node("0", DoubleVector2D(0.0,5.5))
        val node1 = Node("1", DoubleVector2D(0.0,5.5))
        val node2 = Node("2", DoubleVector2D(0.0,5.5))
        val node3 = Node("3", DoubleVector2D(0.0,5.5))
        val node4 = Node("4", DoubleVector2D(0.0,5.5))
        val node5 = Node("5", DoubleVector2D(0.0,5.5))
        val node6 = Node("6", DoubleVector2D(0.0,5.5))
        val node7 = Node("7", DoubleVector2D(0.0,5.5))
        val node8 = Node("8", DoubleVector2D(0.0,5.5))

        val graph = WeightedGraph.Graph()

        graph
                .add(Arc(node0,node1,4.0))
                .add(Arc(node0,node7,8.0))

                .add(Arc(node1,node2,8.0))
                .add(Arc(node1,node7,11.0))

                .add(Arc(node2,node3,7.0))
                .add(Arc(node2,node5,4.0))
                .add(Arc(node2,node8,2.0))

                .add(Arc(node3,node4,9.0))
                .add(Arc(node3,node5,14.0))

                .add(Arc(node4,node5,10.0))

                .add(Arc(node5,node6,2.0))

                .add(Arc(node6,node7,1.0))
                .add(Arc(node6,node8,6.0))

                .add(Arc(node7,node8,7.0))


        val alg = DijkstraAlgorithm<WeightedGraph.Graph, WeightedGraph.Node, WeightedGraph.Arc>()
        alg.apply(graph,node0)

        println("DijkstraAlgorithm\n$graph")

        val alg1 = AStarAlgorithm<WeightedGraph.Graph, WeightedGraph.Node, WeightedGraph.Arc>()
        alg1.apply(graph,node0, node6)

        println("AStarAlgorithm\n$graph")

    }
}