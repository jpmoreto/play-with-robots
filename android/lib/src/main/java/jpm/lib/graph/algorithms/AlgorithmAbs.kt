package jpm.lib.graph.algorithms

import jpm.lib.graph.abs.algorithms.GenericSearchAlgorithm
import jpm.lib.graph.graphs.WeightedGraphAbs
import java.util.TreeSet

/**
 * Created by jm on 02/04/17.
 *
 */

abstract class AlgorithmAbs<
        G: WeightedGraphAbs.Graph<G,N,A>,
        N: WeightedGraphAbs.Node<N, A>,
        A: WeightedGraphAbs.Arc<N,A>>: GenericSearchAlgorithm<G, N, A> {

    override val queue = TreeSet<N>(WeightedGraphAbs.CompareNodesByCost())

    override fun add(node: N) {
        //println("add($node)")
        queue.add(node)
    }

    override fun remove(node: N) {
       queue.remove(node)
    }

    override fun removeNext(): N? {
        return queue.pollFirst()
    }

    override fun setVisited(node: N) {
        node.setVisited(true)
    }

    override fun wasVisited(node: N): Boolean {
        return node.getVisited()
    }
}