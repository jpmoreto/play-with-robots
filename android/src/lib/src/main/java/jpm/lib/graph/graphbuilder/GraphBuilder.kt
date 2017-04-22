package jpm.lib.graph.graphbuilder

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet
import jpm.lib.graph.graphs.WeightedSpaceGraph
import jpm.lib.maps.KDTreeD
import jpm.lib.math.DoubleVector2D
import jpm.lib.math.RectangleContext
import jpm.lib.math.Side
import java.util.TreeSet

/**
 * Created by jm on 25/03/17.
 *
 */

object GraphBuilder {
    fun build(startPoint: DoubleVector2D, endPoint: DoubleVector2D, rectangles: Collection<RectangleContext>): WeightedSpaceGraph.Graph {

        val graph = WeightedSpaceGraph.Graph()

        val startNode = WeightedSpaceGraph.Node(startPoint,startPoint)
        graph.add(startNode)
        graph.startAdd(startNode)
        val endNode = WeightedSpaceGraph.Node(endPoint,endPoint)
        graph.add(endNode)
        graph.endAdd(endNode)

        fun addHorizontal(nodesToAdd: ObjectAVLTreeSet<WeightedSpaceGraph.Node>, r: RectangleContext, other: RectangleContext, y: Double) {

            val xMin = if(other.p1.x < r.p1.x) r.p1.x else other.p1.x
            val xMax = if(other.p2.x > r.p2.x) r.p2.x else other.p2.x

            val middlePoint = DoubleVector2D((xMin + xMax) / 2.0, y)

            var node = graph.nodes[middlePoint]

            if(node == null) {
                node =  WeightedSpaceGraph.Node(DoubleVector2D(xMin,y),DoubleVector2D(xMax,y))
                graph.add(node)
            }
            nodesToAdd.add(node)
        }

        fun addVertical(nodesToAdd: ObjectAVLTreeSet<WeightedSpaceGraph.Node>, r: RectangleContext, other: RectangleContext, x: Double) {

            val yMin = if(other.p1.y < r.p1.y) r.p1.y else other.p1.y
            val yMax = if(other.p2.y > r.p2.y) r.p2.y else other.p2.y

            val middlePoint = DoubleVector2D(x, (yMin + yMax) / 2.0)

            var node = graph.nodes[middlePoint]

            if(node == null) {
                node =  WeightedSpaceGraph.Node(DoubleVector2D(x,yMin),DoubleVector2D(x,yMax))
                graph.add(node!!)
            }
            nodesToAdd.add(node!!)
        }

        for(r in rectangles) {
            val rContainsStart = r.contains(startPoint)
            val rContainsEnd = r.contains(endPoint)

            val nodesToAdd = ObjectAVLTreeSet<WeightedSpaceGraph.Node>(WeightedSpaceGraph.NodeComparator)

            for (bottom in r[Side.BOTTOM]) addHorizontal(nodesToAdd,r,bottom,bottom.p2.y)
            for (top in r[Side.TOP])       addHorizontal(nodesToAdd,r,top,top.p1.y)
            for (left in r[Side.LEFT])     addVertical(nodesToAdd,r,left,left.p2.x)
            for (right in r[Side.RIGHT])   addVertical(nodesToAdd,r,right,right.p1.x)

            for(nodeA in nodesToAdd) {
                for(nodeB in nodesToAdd)
                    if(nodeA != nodeB)
                        WeightedSpaceGraph.Arc(nodeA,nodeB,(nodeA.middlePoint - nodeB.middlePoint).length())

                if(rContainsStart)
                    WeightedSpaceGraph.Arc(startNode,nodeA,(startNode.middlePoint - nodeA.middlePoint).length())

                if(rContainsEnd)
                    WeightedSpaceGraph.Arc(nodeA,endNode,(endNode.middlePoint - nodeA.middlePoint).length())
            }
        }

        return graph
    }

    fun getPath(graph: WeightedSpaceGraph.Graph): List<WeightedSpaceGraph.Node> {
        val path = mutableListOf<WeightedSpaceGraph.Node>()

        var node = graph.endNode
        val startNode = graph.startNode


        if(node != null && startNode != null) {
            while (node != null) {
                path.add(node)
                node = node.from()
            }
        }

        return if(path.size > 1) path.asReversed() else mutableListOf<WeightedSpaceGraph.Node>()
    }

    fun optimizePath(originalPath: List<WeightedSpaceGraph.Node>, tree: KDTreeD.Node, occupiedThreshold: Double): List<WeightedSpaceGraph.Node> {


        fun havePathFromTo(start: WeightedSpaceGraph.Node, end: WeightedSpaceGraph.Node): Boolean {
            var occupied = false

            fun visit(tree: KDTreeD.Node, p: DoubleVector2D, v: DoubleVector2D, t: Double): Boolean {

                if (tree.isLeaf && tree.occupied() > occupiedThreshold) {
                    occupied = true
                    return false
                }
                return true
            }
            tree.intersectRay(start.middlePoint,end.middlePoint - start.middlePoint, 1.0, ::visit)
            return !occupied
        }

        if(originalPath.isNotEmpty()) {
            val path = mutableListOf<WeightedSpaceGraph.Node>()

            var firstNode: WeightedSpaceGraph.Node? = null
            var lastNode: WeightedSpaceGraph.Node? = null

            for (node in originalPath) {
                if (firstNode == null) {
                    firstNode = node
                    path.add(firstNode)
                } else {
                    if (lastNode != null) {
                        if (!havePathFromTo(firstNode, node)) {
                            path.add(lastNode)
                            firstNode = lastNode
                        }
                    }
                    lastNode = node
                }
            }
            if (firstNode != originalPath.last()) {
                path.add(lastNode!!)
            }

            return path
        }
        return originalPath
    }
}