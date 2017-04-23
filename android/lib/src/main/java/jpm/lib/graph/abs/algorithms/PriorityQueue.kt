package jpm.lib.graph.abs.algorithms

import jpm.lib.graph.abs.Arc
import jpm.lib.graph.abs.Graph
import jpm.lib.graph.abs.Node

/**
 * Created by jm on 22/03/17.
 *
 */
interface PriorityQueue<G : Graph<G, N, A>, N : Node, A : Arc<N>>: SearchAlgorithm<G,N,A> {
    val queue: Collection<N>

    fun add(node: N)
    fun remove(node: N)
    fun removeNext(): N?
}