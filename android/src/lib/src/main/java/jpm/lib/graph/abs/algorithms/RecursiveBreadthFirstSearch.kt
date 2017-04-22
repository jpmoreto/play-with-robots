package jpm.lib.graph.abs.algorithms

import jpm.lib.graph.abs.Arc
import jpm.lib.graph.abs.Graph
import jpm.lib.graph.abs.Node

/**
 * Created by jm on 22/03/17.
 *
 */

interface RecursiveBreadthFirstSearch<G : Graph<G, N, A>, N : Node, A : Arc<N>>: SearchAlgorithm<G,N,A> {

     override fun search(graph: G, startNode: N, visit: (arc: A?, node: N) -> Boolean, stopCond: (node: N) -> Boolean) {
        fun searchOutArcs(graph: G, arc: A?, startNode: N, visit: (arc: A?, node: N) -> Boolean) {

            var continueSearch = false

            val arcs = graph.outArcs(startNode).filter { arc ->
                if(continueSearch) {
                    if (!wasVisited(arc.endNode)) {
                        setVisited(arc.endNode)
                        continueSearch = visit(arc, startNode)
                        continueSearch
                    } else
                        false
                } else
                    false
            }

            for (a in arcs) searchOutArcs(graph, a, a.endNode, visit)
        }

        if(visit(null,startNode)) {
            setVisited(startNode)

            searchOutArcs(graph, null, startNode, visit)
        }
    }
}