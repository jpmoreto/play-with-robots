package jpm.lib.maps

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet
import jpm.lib.math.DoubleVector2D
import jpm.lib.math.nearZero
import jpm.lib.maps.KDTreeAbs.SplitAxis
import jpm.lib.maps.KDTreeAbs.TOP_OR_RIGHT
import jpm.lib.maps.KDTreeAbs.BOTTOM_OR_LEFT
import jpm.lib.maps.KDTreeAbs.otherChild
import jpm.lib.math.RectangleContext
import jpm.lib.math.lineIntersection
import java.util.Comparator
import java.util.TreeSet

/**
 * http://robowiki.net/wiki/Kd-tree
 *
 * to have a stack with a decent size to support the recursive nature of the implementation, use:
 * ThreadGroup group = new ThreadGroup("threadGroup");
 * new Thread(group, runnableObject, "YourThreadName", 2000000).start(); - max 256k
 */

object KDTreeD {

    var dimMin: Double = 4.0

    private var horizontalUnit = DoubleVector2D(1.0,0.0)
    private var verticalUnit   = DoubleVector2D(0.0,1.0)

    object CompareNodesX : Comparator<Node> {
        override fun compare(n0: KDTreeD.Node, n1: KDTreeD.Node): Int {

            if(n0.center.x < n1.center.x) return -1
            if(n0.center.x > n1.center.x) return 1
            if(n0.center.y < n1.center.y) return -1
            if(n0.center.y > n1.center.y) return 1
            return 0
        }
    }

    fun fromNodeToRectangle(nodes: ObjectAVLTreeSet<Node>): Collection<RectangleContext> {
        val rectangles = mutableListOf<RectangleContext>()

        for(node in nodes) {
            val x = node.center.x
            val y = node.center.y
            val halfXDim = node.xDim / 2.0
            val halfYDim = node.yDim / 2.0

            rectangles.add(RectangleContext(
                    DoubleVector2D(x - halfXDim, y - halfYDim),
                    DoubleVector2D(x + halfXDim, y + halfYDim)))
        }
        return rectangles
    }

    // The min size of a Node is 2 x 2
    class Node(center: DoubleVector2D, splitAxis: SplitAxis, xDim: Double, yDim: Double = xDim):
            KDTreeAbs.Node<Node,DoubleVector2D,Double>(center,splitAxis,xDim,yDim)  {

        override fun getDimVector(): DoubleVector2D = DoubleVector2D(xDim / 2.0, yDim / 2.0)

        override fun left()
                = center.x - xDim / 2.0

        override fun right()
                = center.x + xDim / 2.0

        override fun bottom()
                = center.y - yDim / 2.0

        override fun top()
                = center.y + yDim / 2.0

        override fun contains(p: DoubleVector2D)
                = left() <= p.x && p.x < right() && bottom() <= p.y && p.y < top()

        fun clone(enlargeFactor: Double, occupiedThreshold: Double): Node {
            val enlargedTree = KDTreeD.Node(center, splitAxis, xDim,yDim)

            visitAll { t ->
                if (t.isLeaf && t.occupied() > occupiedThreshold) {

                    val dimX = t.xDim / 2.0 + enlargeFactor
                    val dimY = t.yDim / 2.0 + enlargeFactor

                    enlargedTree.setOccupied(t.center.x - dimX,t.center.y - dimY,t.center.x + dimX,t.center.y + dimY)
                }
            }
            return enlargedTree
        }

        fun clone(x1: Double, y1: Double, x2: Double, y2: Double,enlargeFactor: Double, occupiedThreshold: Double): Node {
            val enlargedTree = KDTreeD.Node(center, splitAxis, xDim,yDim)

            visitAll(x1,y1,x2,y2) { t ->
                if (t.isLeaf && t.occupied() > occupiedThreshold) {

                    val dimX = t.xDim / 2.0
                    val dimY = t.yDim / 2.0

                    enlargedTree.setOccupied(
                            t.center.x - dimX - enlargeFactor,
                            t.center.y - dimY - enlargeFactor,
                            t.center.x + dimX + enlargeFactor,
                            t.center.y + dimY + enlargeFactor)
                }
            }
            return enlargedTree
        }

        fun visitAll(x1: Double, y1: Double, x2: Double, y2: Double, visit: (node: Node) -> Unit) {
            if(x2 > left() && x1 < right() && y2 > bottom() && y1 < top()) {
                visit(this)
                if(!isLeaf)
                    for (child in children) child?.visitAll(x1, y1, x2, y2, visit)
            }
        }

        fun getNodes(minOccup: Double, maxOccup: Double, x1: Double, y1: Double, x2: Double, y2: Double): Collection<KDTreeD.Node> {
            val result = mutableListOf<KDTreeD.Node>()

            visitAll(x1,y1,x2,y2) { t ->
                if(t.isLeaf) {
                    val occup = t.occupied()
                    if(minOccup <= occup && occup <= maxOccup)
                       result.add(t)
                }
            }
            return result
        }

        fun getRectangles(minOccup: Double, maxOccup: Double, x1: Double, y1: Double, x2: Double, y2: Double): Collection<RectangleContext> {
            val rectangles = mutableListOf<RectangleContext>()

            visitAll(x1,y1,x2,y2) { t ->
                if(t.isLeaf) {
                    val occup = t.occupied()
                    if(minOccup <= occup && occup <= maxOccup) {
                        val x = t.center.x
                        val y = t.center.y
                        val halfXDim = t.xDim / 2.0
                        val halfYDim = t.yDim / 2.0

                        rectangles.add(RectangleContext(
                            DoubleVector2D(x - halfXDim, y - halfYDim),
                            DoubleVector2D(x + halfXDim, y + halfYDim)))
                    }
                }
            }
            return rectangles
        }

        override fun intersectRay(p: DoubleVector2D, v: DoubleVector2D, tmax: Double,
                                  visit: (node: Node, p: DoubleVector2D, v: DoubleVector2D, tmax: Double) -> Boolean) {

            if(visit(this,p,v,tmax)) {

                // Figure out which child to recurse into first
                val axis = splitAxis.i
                val toVisitFirst = if (p[axis] < center[axis]) children[BOTTOM_OR_LEFT] else children[TOP_OR_RIGHT]

                if (nearZero(v[axis], 1E-30)) {
                    // Segment parallel to splitting plane, visit near side only
                    toVisitFirst?.intersectRay(p, v, tmax, visit)
                } else {
                    // Find t value for intersection between segment and split plane
                    val t = (center[axis] - p[axis]) / v[axis]

                    // Test if line segment straddles splitting plane
                    if (v[axis] > 0.0 && (0.0 < t && t < tmax) || v[axis] < 0.0 && (0.0 <= t && t < tmax)) {
                        // Yes, traverse near side first, then far side
                        toVisitFirst?.intersectRay(p, v, t, visit)

                        val toVisitLast = if (p[axis] < center[axis]) children[TOP_OR_RIGHT] else children[BOTTOM_OR_LEFT]

                        toVisitLast?.intersectRay(p + v * t, v, tmax - t, visit)

                    } else {
                        toVisitFirst?.intersectRay(p, v, tmax, visit)
                    }
                }
            }
        }

        fun intersectionPoint(p: DoubleVector2D, trayEnd: DoubleVector2D): DoubleVector2D? {
            val halfXDim = xDim / 2.0
            val halfYDim = yDim / 2.0

            val leftBottom  = DoubleVector2D(center.x - halfXDim, center.y - halfYDim)
            val leftTop     = DoubleVector2D(center.x - halfXDim, center.y + halfYDim)
            val rightBottom = DoubleVector2D(center.x + halfXDim, center.y - halfYDim)
            val rightTop    = DoubleVector2D(center.x + halfXDim, center.y + halfYDim)

            val intersectionPointLeft   = lineIntersection(leftBottom,leftTop,p,trayEnd)
            val intersectionPointRight  = lineIntersection(rightBottom,rightTop,p,trayEnd)
            val intersectionPointBottom = lineIntersection(leftBottom,rightBottom,p,trayEnd)
            val intersectionPointTop    = lineIntersection(leftTop,rightTop,p,trayEnd)

            val possibleIntersectionPoints = arrayOf(intersectionPointLeft,intersectionPointRight,intersectionPointBottom,intersectionPointTop)

            // we can have 1 or 2 intersection points
            val intersectionPoints = possibleIntersectionPoints.filter { it != null }.toTypedArray()

            if(intersectionPoints.isNotEmpty()) { // only to be safe, is possible if the ray is small enough
                if (intersectionPoints.size == 1) {
                    if (intersectionPoints[0]?.first == intersectionPoints[0]?.second) {
                        return intersectionPoints[0]?.first
                    }
                    return DoubleVector2D(
                        (intersectionPoints[0]!!.first.x + intersectionPoints[0]!!.second.x) / 2.0,
                        (intersectionPoints[0]!!.first.y + intersectionPoints[0]!!.second.y) / 2.0)
                }
                return DoubleVector2D(
                    (intersectionPoints[0]!!.first.x + intersectionPoints[1]!!.first.x) / 2.0,
                    (intersectionPoints[0]!!.first.y + intersectionPoints[1]!!.first.y) / 2.0)
            }
            return null
        }

        fun intersectRayMinPoint(pd: DoubleVector2D, v: DoubleVector2D, tmax: Double, occupiedThreshold: Double): DoubleVector2D? {
            val trayEnd = pd + v * tmax
            var nearPointFromPd:DoubleVector2D? = null
            var nearDistance = (trayEnd - pd).length() + 100.0

            fun visit(tree: KDTreeD.Node, p: DoubleVector2D, v: DoubleVector2D, t: Double): Boolean {

                if((pd - p).length() > nearDistance) return false

                if (tree.isLeaf && tree.occupied() > occupiedThreshold) {

                    val intersectionPoint = tree.intersectionPoint(p,trayEnd)

                    if(intersectionPoint != null) { // only to be safe, is possible if the ray is small enough
                        val distance = (pd - intersectionPoint).length()
                        if (distance < nearDistance) {
                            nearDistance = distance
                            nearPointFromPd = intersectionPoint
                        }
                    }
                }
                return true
            }
            intersectRay(pd, v, tmax, ::visit)
            return nearPointFromPd
        }

        // return a collection of nodes ordered by CompareNodesX
        fun adjacentSpace(startPoint: DoubleVector2D, minOccup: Double, maxOccup: Double): ObjectAVLTreeSet<Node> {

            val queue = TreeSet<Node>(CompareNodesX)
            var node: KDTreeD.Node? = this.getPoint(startPoint)

            val result = ObjectAVLTreeSet<Node>(CompareNodesX)

            fun validNode(node: KDTreeD.Node): Boolean {
                val occup = node.occupied()
                return minOccup <= occup && occup <= maxOccup
            }

            fun visit(tree: KDTreeD.Node, p: DoubleVector2D, v: DoubleVector2D, t: Double): Boolean {

                if(tree.isLeaf && validNode(tree)) {
                    if(!result.contains(tree)) {
                        queue.add(tree)
                        result.add(tree)
                    }
                }
                return true
            }

            if(validNode(node!!)) {

                val halfDimMin = dimMin / 2.0

                while(node != null) {

                    val left   = node.left()
                    val right  = node.right()
                    val bottom = node.bottom()
                    val top    = node.top()

                    this.intersectRay(DoubleVector2D(left, top    + halfDimMin),  horizontalUnit, right - left, ::visit) // top ray
                    this.intersectRay(DoubleVector2D(left, bottom - halfDimMin),  horizontalUnit, right - left, ::visit) // bottom ray
                    this.intersectRay(DoubleVector2D(left  - halfDimMin, bottom), verticalUnit,   top - bottom, ::visit) // left ray
                    this.intersectRay(DoubleVector2D(right + halfDimMin, bottom), verticalUnit,   top - bottom, ::visit) // right ray
                    node = queue.pollFirst()
                }
            }
            return result
        }

        override fun getPoint(p: DoubleVector2D): Node {
            if(isLeaf) return this

            fun getChild(childPos: Int, axis: SplitAxis, xDiff: Double, yDiff: Double, xDim: Double, yDim: Double): Node {
                if (children[childPos] == null)
                    return Node(DoubleVector2D(center.x + xDiff, center.y + yDiff), axis, xDim,yDim)

                return children[childPos]!!.getPoint(p)
            }

            val quarterDim = halfDim / 2.0

            if(SplitAxis.XAxis == splitAxis) {
                if (p.x < center.x)  // left
                    return getChild(BOTTOM_OR_LEFT, SplitAxis.YAxis, -quarterDim, 0.0, halfDim, yDim)
                else  // right
                    return getChild(TOP_OR_RIGHT, SplitAxis.YAxis,    quarterDim, 0.0, halfDim, yDim)
            }
            else {
                if (p.y < center.y)  // bottom
                    return getChild(BOTTOM_OR_LEFT, SplitAxis.XAxis, 0.0, -quarterDim, xDim, halfDim)
                else  // top
                    return getChild(TOP_OR_RIGHT,   SplitAxis.XAxis, 0.0,  quarterDim, xDim, halfDim)
            }
        }

        override fun getOrCreateChild(x: Double, y: Double): Node {

            fun getChild(childPos: Int, axis: SplitAxis, xDiff: Double, yDiff: Double, xDim: Double, yDim: Double): Node {
                if (children[childPos] == null) {
                    children[childPos] = Node(DoubleVector2D(center.x + xDiff, center.y + yDiff), axis, xDim, yDim)

                    children[otherChild[childPos]] = Node(DoubleVector2D(center.x - xDiff, center.y - yDiff), axis, xDim, yDim)
                }

                return children[childPos]!!
            }

            isLeaf = false

            if(SplitAxis.XAxis == splitAxis) {
                if (x < center.x) // left
                    return getChild(BOTTOM_OR_LEFT, SplitAxis.YAxis, -halfDim / 2.0, 0.0, halfDim, yDim)
                else  // right
                    return getChild(TOP_OR_RIGHT, SplitAxis.YAxis, halfDim / 2.0, 0.0, halfDim, yDim)
            }
            else {
                if (y < center.y) // bottom
                    return getChild(BOTTOM_OR_LEFT, SplitAxis.XAxis, 0.0, -halfDim / 2.0, xDim, halfDim)
                else // top
                    return getChild(TOP_OR_RIGHT, SplitAxis.XAxis, 0.0, halfDim / 2.0, xDim, halfDim)
            }
        }

        fun getOrCreateChild() {

            fun createChildren(axis: SplitAxis, xDiff: Double, yDiff: Double, xDim: Double, yDim: Double) {
                children[TOP_OR_RIGHT]   = Node(DoubleVector2D(center.x + xDiff, center.y + yDiff), axis, xDim, yDim)
                children[BOTTOM_OR_LEFT] = Node(DoubleVector2D(center.x - xDiff, center.y - yDiff), axis, xDim, yDim)
            }

            isLeaf = false
            if (children[TOP_OR_RIGHT] == null) {
                if (SplitAxis.XAxis == splitAxis)
                    createChildren(SplitAxis.YAxis, halfDim / 2.0, 0.0, halfDim, yDim)
                else
                    createChildren(SplitAxis.XAxis, 0.0, halfDim / 2.0, xDim, halfDim)
            }
        }

        override fun getOrCreateChild(p: DoubleVector2D): Node
                = getOrCreateChild(p.x,p.y)

        override fun split() {

            val childrenSamplesPositive = samplesPositive shr 1
            val childrenSamplesNegative = samplesNegative shr 1

            fun setSamples(childPos: Int) {

                val child = children[childPos]

                child!!.samplesPositive += childrenSamplesPositive
                child!!.samplesNegative += childrenSamplesNegative
            }

            setSamples(TOP_OR_RIGHT)
            setSamples(BOTTOM_OR_LEFT)

            isLeaf = false
            samplesPositive = 0
            samplesNegative = 0
        }

        override val children = arrayOf<Node?>(null, null)
        override val halfDim = if(SplitAxis.XAxis == splitAxis) xDim / 2.0 else yDim / 2.0
        override fun canSplit() = SplitAxis.XAxis == splitAxis && xDim > dimMin || SplitAxis.YAxis == splitAxis && yDim > dimMin

        fun setOccupied(x1: Double, y1: Double, x2: Double, y2: Double) {

            if(x2 <= left() || x1 >= right() || y2 <= bottom() || y1 >= top()) return

            if (canSplit()) {
                if (samplesPositive > 0 || samplesNegative > 0) {
                    if (samplesPositive > samplesNegative) {
                        setOccupied()
                    } else {
                        getOrCreateChild()
                        children[0]!!.setOccupied(x1,y1,x2,y2)
                        children[1]!!.setOccupied(x1,y1,x2,y2)
                        split()
                    }
                } else {
                    if(x1 <= left() && x2 >= right() && y1 <= bottom() && y2 >= top()) {
                        setOccupied()
                        children[0] = null
                        children[1] = null
                        isLeaf = true
                    } else {
                        getOrCreateChild()
                        children[0]!!.setOccupied(x1,y1,x2,y2)
                        children[1]!!.setOccupied(x1,y1,x2,y2)

                        if (children[0]!!.isLeaf && children[1]!!.isLeaf) {
                            tryMergeChildren()
                            if(children[0] != null)
                               tryDeleteChild()
                        }
                    }
                }
            } else
                setOccupied()
        }
    }
}
