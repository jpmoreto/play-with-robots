package jpm.lib.graph.algorithms

import jpm.lib.graph.graphs.WeightedGraphAbs

/**
 * Created by jm on 22/03/17.
 *
 */
class AStarAlgorithm<
        G: WeightedGraphAbs.Graph<G,N,A>,
        N: WeightedGraphAbs.Node<N, A>,
        A: WeightedGraphAbs.Arc<N,A>>: AlgorithmAbs<G, N, A>() {


    fun reset(graph: G, startNode: N, endNode: N) {
        for(node in graph.nodes()) { node.reset(endNode) }
        startNode.setMinCost(0.0)
    }

    fun apply(graph: G, startNode: N, endNode: N) {

        fun stopCond(node: N) = node.getMinCost() + node.getMinCostToEndNode() >= endNode.getMinCost()

        fun visit(arc: A?, node: N): Boolean {

            if(arc != null) {
                val newCost = arc.startNode.getMinCost() + arc.weight

                if(newCost < node.getMinCost()) {
                    if(newCost + node.getMinCostToEndNode() >= endNode.getMinCost()) return false

                    node.setMinCost(newCost)
                    node.setMinCostArc(arc)
                }
            }
            return true
        }
        reset(graph,startNode,endNode)

        search(graph,startNode, ::visit,::stopCond)
    }
}
