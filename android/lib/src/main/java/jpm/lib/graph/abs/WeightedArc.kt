package jpm.lib.graph.abs

/**
 * Created by jm on 22/03/17.
 *
 */

interface WeightedArc<out NO: Node, out N: Number>: Arc<NO> {
    fun weight(): N
}