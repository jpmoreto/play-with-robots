package jpm.lib.maps

import jpm.lib.math.IntVector2D
import jpm.lib.maps.KDTreeAbs.SplitAxis
import jpm.lib.maps.KDTreeAbs.TOP_OR_RIGHT
import jpm.lib.maps.KDTreeAbs.BOTTOM_OR_LEFT
import jpm.lib.math.DoubleVector2D
import jpm.lib.math.nearZero

/**
 * http://robowiki.net/wiki/Kd-tree
 */
object KDTree {

    var dimMin = 4

    // The min size of a Node is 2 x 2  Node<T: Node<T,V,N>, V: Vector2D<V,N>, N: Number>: Tree<T,V,N>
    class Node(center: IntVector2D, splitAxis: SplitAxis, xDim: Int, yDim: Int = xDim):
            KDTreeAbs.Node<Node,IntVector2D,Int>(center,splitAxis,xDim,yDim) {

        override fun left()
                = center.x - xDim / 2

        override fun right()
                = center.x + xDim / 2

        override fun bottom()
                = center.y - yDim / 2

        override fun top()
                = center.y + yDim / 2

        override fun contains(p: IntVector2D)
                = left() <= p.x && p.x < right() && bottom() <= p.y && p.y < top()

        override fun getDimVector(): IntVector2D = IntVector2D(xDim / 2, yDim / 2)

        override fun intersectRay(p: DoubleVector2D, v: DoubleVector2D, tmax: Double,
                                  visit: (node: Node, p: DoubleVector2D, v: DoubleVector2D, tmax: Double) -> Boolean) {

            if(visit(this,p,v,tmax)) {

                // Figure out which child to recurse into first
                val axis = splitAxis.i
                val toVisitFirst = if (p[axis] < center[axis]) children[BOTTOM_OR_LEFT] else children[TOP_OR_RIGHT]

                if (nearZero(v[axis], 1E-20)) {
                    // Segment parallel to splitting plane, visit near side only
                    toVisitFirst?.intersectRay(p, v, tmax, visit)
                } else {
                    // Find t value for intersection between segment and split plane
                    val t = (center[axis] - p[axis]) / v[axis]

                    // Test if line segment straddles splitting plane
                    if (0.0 < t && t < tmax) {
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


        override fun getPoint(p: IntVector2D): Node {
            if(isLeaf) return this

            fun getChild(childPos: Int, axis: SplitAxis, xDiff: Int, yDiff: Int, xDim: Int, yDim: Int): Node {
                if (children[childPos] == null)
                    return Node(IntVector2D(center.x + xDiff, center.y + yDiff), axis, xDim,yDim)

                return children[childPos]!!.getPoint(p)
            }

            val quarterDim = halfDim shr 1

            if(SplitAxis.XAxis == splitAxis) {
                if (p.x < center.x)  // left
                    return getChild(BOTTOM_OR_LEFT, SplitAxis.YAxis, -quarterDim, 0, halfDim, yDim)
                else  // right
                    return getChild(TOP_OR_RIGHT, SplitAxis.YAxis,    quarterDim, 0, halfDim, yDim)
            }
            else {
                if (p.y < center.y)  // bottom
                    return getChild(BOTTOM_OR_LEFT, SplitAxis.XAxis, 0, -quarterDim, xDim, halfDim)
                else  // top
                    return getChild(TOP_OR_RIGHT,   SplitAxis.XAxis, 0,  quarterDim, xDim, halfDim)
            }
        }

        override fun getOrCreateChild(x: Int, y: Int): Node {
            return Node(center,splitAxis,xDim,yDim)
        }

        override fun getOrCreateChild(p: IntVector2D): Node {

            fun getChild(childPos: Int, axis: SplitAxis, xDiff: Int, yDiff: Int, xDim: Int, yDim: Int): Node {
                if (children[childPos] == null) {
                    children[childPos] = Node(IntVector2D(center.x + xDiff, center.y + yDiff), axis, xDim, yDim)

                    if(childPos == 0)
                        children[1] = Node(IntVector2D(center.x - xDiff, center.y - yDiff), axis, xDim, yDim)
                    else
                        children[0] = Node(IntVector2D(center.x - xDiff, center.y - yDiff), axis, xDim, yDim)
                }

                return children[childPos]!!
            }

            val quarterDim = halfDim shr 1

            isLeaf = false

            if(SplitAxis.XAxis == splitAxis) {
                if (p.x < center.x)  // left
                    return getChild(BOTTOM_OR_LEFT, SplitAxis.YAxis, -quarterDim, 0, halfDim, yDim)
                else  // right
                    return getChild(TOP_OR_RIGHT,  SplitAxis.YAxis,   quarterDim, 0, halfDim, yDim)
            }
            else {
                if (p.y < center.y)  // bottom
                    return getChild(BOTTOM_OR_LEFT, SplitAxis.XAxis, 0, -quarterDim, xDim, halfDim)
                else  // top
                    return getChild(TOP_OR_RIGHT,   SplitAxis.XAxis, 0,  quarterDim, xDim, halfDim)
            }
        }

        override fun split() {

            val childrenSamplesPositive = samplesPositive shr 1
            val childrenSamplesNegative = samplesNegative shr 1

            fun setSamples(childPos: Int, axis: SplitAxis, xDiff: Int, yDiff: Int,  xDim: Int, yDim: Int) {
                if (children[childPos] == null) {
                    val child = Node(IntVector2D(center.x + xDiff, center.y + yDiff), axis, xDim, yDim)
                    children[childPos] = child

                    child.samplesPositive = childrenSamplesPositive
                    child.samplesNegative = childrenSamplesNegative
                }
            }

            val quarterDim = halfDim shr 1

            if(SplitAxis.XAxis == splitAxis) {
                setSamples(TOP_OR_RIGHT,   SplitAxis.YAxis,  quarterDim, 0, halfDim, yDim)
                setSamples(BOTTOM_OR_LEFT, SplitAxis.YAxis, -quarterDim, 0, halfDim, yDim)
            }
            else {
                setSamples(TOP_OR_RIGHT,   SplitAxis.XAxis, 0,  quarterDim, xDim, halfDim)
                setSamples(BOTTOM_OR_LEFT, SplitAxis.XAxis, 0, -quarterDim, xDim, halfDim)
            }

            isLeaf = false
            samplesPositive = 0
            samplesNegative = 0
        }

        override val children = arrayOf<Node?>(null, null)
        override val halfDim = if(SplitAxis.XAxis == splitAxis) xDim shr 1 else yDim shr 1
        override fun canSplit()         = SplitAxis.XAxis == splitAxis && xDim > dimMin || SplitAxis.YAxis == splitAxis && yDim > dimMin
    }
}
