package jpm.lib.graph.abs.algorithms

import jpm.lib.graph.abs.Arc
import jpm.lib.graph.abs.Graph
import jpm.lib.graph.abs.Node

/**
 * Created by jm on 22/03/17.
 *
 */

interface RecursiveDepthFirstSearch<G : Graph<G, N, A>, N : Node, A : Arc<N>>: SearchAlgorithm<G,N,A> {

    override fun search(graph: G, startNode: N, visit: (arc: A?, node: N) -> Boolean, stopCond: (node: N) -> Boolean) {

        setVisited(startNode)

        if(visit(null,startNode))
            for (arc in graph.outArcs(startNode))
                if (!wasVisited(arc.endNode))
                    search(graph, arc.endNode, visit)
    }
}