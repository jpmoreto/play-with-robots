package jpm.lib.graph.algorithms

import jpm.lib.graph.graphs.WeightedGraphAbs

/**
 * Created by jm on 22/03/17.
 *
 */
class DijkstraAlgorithm<
        G: WeightedGraphAbs.Graph<G,N,A>,
        N: WeightedGraphAbs.Node<N, A>,
        A: WeightedGraphAbs.Arc<N,A>>: AlgorithmAbs<G, N, A>() {

    fun reset(graph: G, startNode: N, endNode: N) {
        for(node in graph.nodes()) { node.reset(endNode) }
        startNode.setMinCost(0.0)
    }

    fun apply(graph: G, startNode: N) {
        fun visit(arc: A?, node: N): Boolean {

            if(arc != null) {
                val newCost = arc.weight + if(node == arc.startNode) arc.endNode.getMinCost() else arc.startNode.getMinCost()

                if(newCost < node.getMinCost()) {
                    node.setMinCost(newCost)
                    node.setMinCostArc(arc)
                }
            }
            return true
        }
        reset(graph,startNode,startNode)

        search(graph,startNode, ::visit)
    }
}
