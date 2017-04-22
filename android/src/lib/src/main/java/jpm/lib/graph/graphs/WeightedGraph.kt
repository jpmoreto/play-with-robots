package jpm.lib.graph.graphs

import jpm.lib.math.DoubleVector2D

/**
 * Created by jm on 22/03/17.
 *
 */

object WeightedGraph {

    class Node(val name: String, val p: DoubleVector2D): WeightedGraphAbs.Node<Node, Arc> {

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

        val arcs = mutableSetOf<Arc>()
        var visited_ = false
        var minCost_: Double = Double.POSITIVE_INFINITY
        var minCostArc_: Arc? = null
        var minCostToEndNode_: Double = 0.0

        override fun reset(endNode: Node) {
            minCost_ = Double.POSITIVE_INFINITY
            minCostArc_ = null
            visited_ = false

            minCostToEndNode_ = (p - endNode.p).length()
        }

        override fun add(arc: Arc): Node {
            arcs.add(arc)
            return this
        }

        override fun toString(): String {
            val from =
                    if(minCostArc_ == null) ""
                    else if(minCostArc_!!.startNode == this) minCostArc_!!.endNode.name
                    else minCostArc_!!.startNode.name

            return "Node(name='$name', visited=$visited_, minCost=$minCost_, from=$from)"
        }
    }

    class Arc(startNode: Node, endNode: Node, weight: Double) : WeightedGraphAbs.Arc<Node,Arc>(startNode,endNode,weight) {
        override fun weight() = weight

        init {
            startNode.add(this)
            endNode.add(this)
        }
        override fun toString(): String {
            return "Arc(startNode=$startNode, endNode=$endNode, weight=$weight)"
        }
    }

    class Graph: WeightedGraphAbs.Graph<Graph,Node,Arc> {
        val nodes = mutableMapOf<String,Node>()

        operator fun get(nodeName: String): Node? = nodes[nodeName]

        override fun nodes(): Set<Node> {
            return nodes.map { it.value }.toSet()
        }

        override fun arcs(): Set<Arc> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startNodes(): Set<Node> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun endNodes(): Set<Node> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun add(node: Node): Graph {
            nodes.put(node.name,node)
            return this
        }

        override fun add(arc: Arc): Graph {
            add(arc.startNode)
            add(arc.endNode)
            return this
        }

        override fun startAdd(node: Node): Graph {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun endAdd(node: Node): Graph {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deg(node: Node): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun outDeg(node: Node): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun inDeg(node: Node): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun outArcs(node: Node): Set<Arc> = node.arcs

        override fun inArcs(node: Node): Set<Arc> = node.arcs

        override fun arcs(node: Node): Set<Arc> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun adjacent(a: Node, b: Node): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun toString(): String {
            return "Graph(nodes=${nodes().sortedBy { it.name }})"
        }
    }
}