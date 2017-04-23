package jpm.lib.graph.graphs

import java.util.Comparator

/**
 * Created by jm on 22/03/17.
 *
 */

object WeightedGraphAbs {

    class CompareNodesByCost<N: WeightedGraphAbs.Node<N, A>,A: WeightedGraphAbs.Arc<N,A>>: Comparator<N> {
        override fun compare(p0: N, p1: N): Int {
            val costP0 = p0.getMinCost() + p0.getMinCostToEndNode()
            val costP1 = p1.getMinCost() + p1.getMinCostToEndNode()

            if(costP0 < costP1) return -1
            if(costP0 > costP1) return 1

            val hashP0 = p0.hashCode()
            val hashP1 = p1.hashCode()

            if(hashP0 < hashP1) return -1
            if(hashP0 > hashP1) return 1
            return 0
        }
    }

    interface Node<N: Node<N,A>,A: Arc<N,A>>: jpm.lib.graph.abs.Node {
        fun reset(endNode: N)

        fun add(arc: A): N

        fun setVisited(isVisited: Boolean)
        fun getVisited(): Boolean
        fun getMinCost(): Double
        fun setMinCost(cost: Double)
        fun setMinCostArc(arc: A)
        fun getMinCostToEndNode(): Double
    }

    abstract class Arc< N: Node<N,A>, A: Arc<N,A> >(override val startNode: N, override val endNode: N, val weight: Double) : jpm.lib.graph.abs.WeightedArc<N, Double> {
        override fun weight() = weight

        init {
            startNode.add(this as A) // FIXME:
            endNode.add(this)
        }

        override fun toString(): String {
            return "Arc(startNode=$startNode, endNode=$endNode, weight=$weight)"
        }
    }

    interface Graph<G: jpm.lib.graph.abs.Graph<G, N, A>, N: Node<N,A>, A: Arc<N,A> >: jpm.lib.graph.abs.Graph<G,N,A>
}