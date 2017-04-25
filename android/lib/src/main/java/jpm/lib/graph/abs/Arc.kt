package jpm.lib.graph.abs

/**
 * Created by jm on 21/03/17.
 *
 */

interface   Arc<out N: Node> {
    val startNode: N
    val endNode: N
}
