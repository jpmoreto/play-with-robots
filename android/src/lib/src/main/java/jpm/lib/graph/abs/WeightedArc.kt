package jpm.lib.graph.abs

/**
 * Created by jm on 22/03/17.
 *
 */

interface WeightedArc<NO: Node, N: Number>: Arc<NO> {
    fun weight(): N
}