package jpm.lib.graph.abs

/**
 * Created by jm on 21/03/17.
 *
 * see: http://btechsmartclass.com/DS/U3_T11.html
 */

interface Graph<G: Graph<G, N, A>, N: Node,A: Arc<N>> {
    fun nodes(): Set<N>
    fun startNodes(): Set<N>
    fun endNodes(): Set<N>

    infix fun add(node: N): G
    infix fun add(arc: A): G
    infix fun startAdd(node: N): G
    infix fun endAdd(node: N): G

    infix fun outArcs(node: N): Set<A>
    infix fun inArcs(node: N): Set<A>
}