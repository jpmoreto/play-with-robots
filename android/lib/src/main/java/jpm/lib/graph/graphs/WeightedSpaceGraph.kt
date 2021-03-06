package jpm.lib.graph.graphs

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import jpm.lib.math.DoubleVector2D
import java.util.*

/**
 * Created by jm on 22/03/17.
 *
 */

object WeightedSpaceGraph {

    class Node(val p1: DoubleVector2D, val p2: DoubleVector2D, val middlePoint: DoubleVector2D): WeightedGraphAbs.Node<Node,Arc> {

        override fun setVisited(isVisited: Boolean) {
             visited_ = isVisited
        }

        override fun getVisited(): Boolean = visited_

        override fun getMinCost(): Double = minCost_

        override fun setMinCost(cost: Double) {
            minCost_ = cost
        }

        override fun setMinCostArc(arc: Arc) {
            minCostArc_ = arc
        }

        override fun getMinCostToEndNode(): Double = minCostToEndNode_

        val arcs = ObjectOpenHashSet<Arc>(53)
        var visited_ = false
        var minCost_: Double = Double.POSITIVE_INFINITY
        var minCostArc_: Arc? = null
        var minCostToEndNode_: Double = 0.0

        override fun reset(endNode: Node) {
            minCost_ = Double.POSITIVE_INFINITY
            minCostArc_ = null
            visited_ = false

            minCostToEndNode_ = (middlePoint - endNode.middlePoint).length()
        }

        override fun add(arc: Arc): Node {
            arcs.add(arc)
            return this
        }

        fun from(): Node? {
            if(minCostArc_ == null) return null
            return minCostArc_!!.startNode
        }

        override fun toString(): String {
            val from = if(minCostArc_ != null) minCostArc_!!.startNode.middlePoint else ""
            return "Node(name='$middlePoint', visited=$visited_, from=$from, minCost=$minCost_, totalCost=${minCost_ + minCostToEndNode_})"
        }
    }

    class Arc(startNode: Node, endNode: Node, weight: Double) : WeightedGraphAbs.Arc<Node,Arc>(startNode,endNode,weight) {
        init {
            startNode.add(this)
        }

        override fun weight() = weight

        override fun equals(other: Any?): Boolean {
            if(other is Arc) {
                return startNode === other.startNode && endNode == other.endNode
            }
            return false
        }

        override fun hashCode(): Int
            = startNode.hashCode().xor(endNode.hashCode())

        override fun toString(): String
            = "Arc(startNode=$startNode, endNode=$endNode, weight=$weight)"
    }
    /*
    object ArcComparator: Comparator<Arc> {
        override fun compare(a0: Arc, a1: Arc): Int {
            return NodeComparator.compare(a0.endNode,a1.endNode)
        }
    }
    */

    object NodeComparator: Comparator<Node> {
        override fun compare(a0: Node, a1: Node): Int
            = DoubleVector2DComparator.compare(a0.middlePoint,a1.middlePoint)
    }

    private object DoubleVector2DComparator : Comparator<DoubleVector2D> {
        override fun compare(p0: DoubleVector2D, p1: DoubleVector2D): Int {

            if(p0.x < p1.x - 1E-20) return -1
            if(p0.x > p1.x + 1E-20) return 1
            if(p0.y < p1.y - 1E-20) return -1
            if(p0.y > p1.y + 1E-20) return 1
            return 0
        }
    }

    class Graph: WeightedGraphAbs.Graph<Graph,Node,Arc> {
        val nodes = TreeMap<DoubleVector2D,Node>(DoubleVector2DComparator)

        var startNode: Node? = null
        var endNode: Node? = null

        operator fun get(nodeName: DoubleVector2D): Node? = nodes[nodeName]

        override fun nodes(): Set<Node> {
            return nodes.map { it.value }.toSet()
        }

        override fun startNodes(): Set<Node> {
            return if(startNode != null) setOf(startNode!!) else setOf<Node>()
        }

        override fun endNodes(): Set<Node> {
            return if(endNode != null) setOf(endNode!!) else setOf<Node>()
        }

        override fun add(node: Node): Graph {
            nodes.put(node.middlePoint,node)
            return this
        }

        override fun add(arc: Arc): Graph {
            add(arc.startNode)
            add(arc.endNode)
            return this
        }

        override fun startAdd(node: Node): Graph {
            startNode = node
            return this
        }

        override fun endAdd(node: Node): Graph {
            endNode = node
            return this
        }

        override fun outArcs(node: Node): Set<Arc> = node.arcs

        override fun inArcs(node: Node): Set<Arc> = node.arcs

        override fun toString(): String {
            return "Graph(nodes=${nodes().sortedBy { it.middlePoint }})"
        }
    }
}