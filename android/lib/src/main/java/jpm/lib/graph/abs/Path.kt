package jpm.lib.graph.abs

/**
 * Created by jm on 22/03/17.
 *
 */

interface Path<N: Node, A: Arc<N>> {
    infix fun add(node: N): Path<N,A>
    infix fun add(arc: A): Path<N,A>

    fun path(): List<Pair<N,A?>>
}
