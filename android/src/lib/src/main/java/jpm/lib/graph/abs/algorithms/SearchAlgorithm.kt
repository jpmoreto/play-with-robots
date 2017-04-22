package jpm.lib.graph.abs.algorithms

import jpm.lib.graph.abs.Arc
import jpm.lib.graph.abs.Graph
import jpm.lib.graph.abs.Node

/**
 * Created by jm on 22/03/17.
 *
 * http://algs4.cs.princeton.edu/41graph/
 */

interface SearchAlgorithm<G : Graph<G, N, A>, N : Node, A : Arc<N>> {


    fun search(graph: G, startNode: N, visit: (arc: A?, node: N) -> Boolean, stopCond: (node: N) -> Boolean = {false})

    fun setVisited(node: N)
    fun wasVisited(node: N): Boolean
}