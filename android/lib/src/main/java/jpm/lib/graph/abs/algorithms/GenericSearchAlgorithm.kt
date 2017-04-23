package jpm.lib.graph.abs.algorithms

import jpm.lib.graph.abs.Arc
import jpm.lib.graph.abs.Graph
import jpm.lib.graph.abs.Node

/**
 * Created by jm on 22/03/17.
 *
 */

interface GenericSearchAlgorithm<G : Graph<G, N, A>, N : Node, A : Arc<N>>:
        SearchAlgorithm<G,N,A>, PriorityQueue<G,N,A> {

    override fun search(graph: G, startNode: N, visit: (arc: A?, node: N) -> Boolean, stopCond: (node: N) -> Boolean) {

        var nextNode = if(visit(null, startNode)) startNode else null

        while(nextNode != null) {
            //println("nextNode($nextNode)")
            if(stopCond(nextNode)) return

            setVisited(nextNode)

            for (arc in graph.outArcs(nextNode)) {

                val node = arc.endNode

                if (!wasVisited(node)) {
                    remove(node)

                    if(visit(arc, node)) {
                        add(node)
                    }
                }
            }

            nextNode = removeNext()
        }
    }
}
