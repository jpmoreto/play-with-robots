package jpm.lib.math

import it.unimi.dsi.fastutil.doubles.Double2ReferenceAVLTreeMap
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet

/**
 * Created by jm on 25/03/17.
 *
 */

val epsilon = 1E-30

/**
 * @p1 left bottom
 * @p2 right top
 */
open class DoubleRectangle2D(val p1: DoubleVector2D, val p2: DoubleVector2D) {
    override fun toString() = "(p1=$p1, p2=$p2)"

    fun height() = p2.y - p1.y
    fun width()  = p2.x - p1.x
}
/*
object CompareDoubleRectangle2DXY : Comparator<DoubleRectangle2D> {
    override fun compare(r0: DoubleRectangle2D, r1: DoubleRectangle2D): Int {

        if(r0.p1.x < r1.p1.x) return -1
        if(r0.p1.x > r1.p1.x) return 1
        if(r0.p2.x < r1.p2.x) return -1
        if(r0.p2.x > r1.p2.x) return 1

        if(r0.p1.y < r1.p1.y) return -1
        if(r0.p1.y > r1.p1.y) return 1
        if(r0.p2.y < r1.p2.y) return -1
        if(r0.p2.y > r1.p2.y) return 1

        return 0
    }
}

object CompareDoubleRectangle2DYX : Comparator<DoubleRectangle2D> {
    override fun compare(r0: DoubleRectangle2D, r1: DoubleRectangle2D): Int {

        if(r0.p1.y < r1.p1.y) return -1
        if(r0.p1.y > r1.p1.y) return 1
        if(r0.p2.y < r1.p2.y) return -1
        if(r0.p2.y > r1.p2.y) return 1

        if(r0.p1.x < r1.p1.x) return -1
        if(r0.p1.x > r1.p1.x) return 1
        if(r0.p2.x < r1.p2.x) return -1
        if(r0.p2.x > r1.p2.x) return 1

        return 0
    }
}
*/

object SimpleCompareDoubleRectangle2DXY : Comparator<DoubleRectangle2D> {
    override fun compare(r0: DoubleRectangle2D, r1: DoubleRectangle2D): Int {

        if(r0.p1.x < r1.p1.x) return -1
        if(r0.p1.x > r1.p1.x) return 1
        if(r0.p2.x < r1.p2.x) return -1
        if(r0.p2.x > r1.p2.x) return 1

        return 0
    }
}

object SimpleCompareDoubleRectangle2DYX : Comparator<DoubleRectangle2D> {
    override fun compare(r0: DoubleRectangle2D, r1: DoubleRectangle2D): Int {

        if(r0.p1.y < r1.p1.y) return -1
        if(r0.p1.y > r1.p1.y) return 1
        if(r0.p2.y < r1.p2.y) return -1
        if(r0.p2.y > r1.p2.y) return 1

        return 0
    }
}

fun intersectsY(b: DoubleRectangle2D, a: DoubleRectangle2D)
        = a.p1.y - epsilon <= b.p1.y && b.p2.y <= a.p2.y - epsilon ||
        b.p1.y - epsilon <= a.p1.y && a.p1.y < b.p2.y - epsilon ||
        b.p1.y + epsilon < a.p2.y && a.p2.y <= b.p2.y + epsilon

fun intersectsX(b: DoubleRectangle2D, a: DoubleRectangle2D)
        = a.p1.x - epsilon <= b.p1.x && b.p2.x <= a.p2.x - epsilon ||
        b.p1.x - epsilon <= a.p1.x && a.p1.x < b.p2.x - epsilon ||
        b.p1.x + epsilon < a.p2.x && a.p2.x <= b.p2.x + epsilon

enum class Side(val dir: Int, val oppositeDir: Int) {
    TOP(0,1), BOTTOM(1,0), RIGHT(2,3), LEFT  (3,2)
}

class RectangleContext(p1: DoubleVector2D, p2: DoubleVector2D): DoubleRectangle2D(p1,p2) {

    val dirs = arrayOf(
        ObjectAVLTreeSet<RectangleContext>(SimpleCompareDoubleRectangle2DXY),
        ObjectAVLTreeSet<RectangleContext>(SimpleCompareDoubleRectangle2DXY),
        ObjectAVLTreeSet<RectangleContext>(SimpleCompareDoubleRectangle2DYX),
        ObjectAVLTreeSet<RectangleContext>(SimpleCompareDoubleRectangle2DYX)
    )

    operator fun get(side: Side) = dirs[side.dir]

    fun contains(p: DoubleVector2D): Boolean
            = p1.x <= p.x && p.x < p2.x && p1.y <= p.y && p.y < p2.y

    fun add(other: RectangleContext, toDir: Side) {
        //println("${toStringLess()} add ${other.toStringLess()} toDir $toDir")
        dirs[toDir.dir].add(other)
        other.dirs[toDir.oppositeDir].add(this)
    }

    override fun toString()
            = "RC(${super.toString()}\n   top=${dirs[Side.TOP.dir].map {it.toStringLess()}}\n   bottom=${dirs[Side.BOTTOM.dir].map {it.toStringLess()}}\n   left=${dirs[Side.LEFT.dir].map {it.toStringLess()}}\n   right=${dirs[Side.RIGHT.dir].map {it.toStringLess()}})"

    fun toStringLess()
            = super.toString()
}

fun compactRectangles(rectContexts: Collection<RectangleContext>, minDiff: Int = 30, maxIterations: Int= 5): Collection<RectangleContext> {
    var size = 0

    val rectContextsByBottom = Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>()
    val rectContextsByTop = Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>()
    val rectContextsByLeft = Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>()
    val rectContextsByRight = Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>()

    fun addTo(xOrY: Double, context: RectangleContext, map: Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>, cmp: Comparator<DoubleRectangle2D>) {
        val nodes = map.get(xOrY)
        if(nodes == null) {
            val newTree = ObjectAVLTreeSet<RectangleContext>(cmp)
            newTree.add(context)
            map.put(xOrY,newTree)
        } else
            nodes.add(context)
    }

    fun  mergeRectangles(leftMatchRectangles: MutableList<RectangleContext>, rightMatchRectangles: MutableList<RectangleContext>): RectangleContext {
        // left rectangles are to the right of the merge
        // right rectangles are to the left of the merge
        //
        return RectangleContext(rightMatchRectangles.first().p1, leftMatchRectangles.last().p2)
    }

    fun mergeNeighborsLeftAndRight(
        leftRectangles: ObjectAVLTreeSet<RectangleContext>,
        rightRectangles: ObjectAVLTreeSet<RectangleContext>,
        rectangleContextsToAdd: MutableList<RectangleContext>,
        rectangleContextsToDelete: MutableList<RectangleContext>) {

        fun  normalizeLeftRectangles(matchRectangles: MutableList<RectangleContext>,contextMin: Double, contextMax: Double): MutableList<RectangleContext> {
            // left rectangles are to the right of the merge
            //
            if(nearZero(contextMin - contextMax,epsilon)) // all contexts with same width
                return matchRectangles

            val newMatchRectangles = mutableListOf<RectangleContext>() // stores the central rectangles

            for(rectangle in matchRectangles) {
                if(nearZero(rectangle.p2.x - contextMin,epsilon)) {
                    newMatchRectangles.add(rectangle)
                } else { // split in 2
                    newMatchRectangles.add(RectangleContext(rectangle.p1, DoubleVector2D(contextMin,rectangle.p2.y)))
                    rectangleContextsToAdd.add(RectangleContext(DoubleVector2D(contextMin,rectangle.p1.y), rectangle.p2))
                }
            }
            return newMatchRectangles
        }

        fun  normalizeRightRectangles(rightMatchRectangles: MutableList<RectangleContext>,contextMin: Double, contextMax: Double): MutableList<RectangleContext> {
            // right rectangles are to the left of the merge
            //
            if(nearZero(contextMin - contextMax,epsilon)) // all contexts with same width
                return rightMatchRectangles

            val newMatchRectangles = mutableListOf<RectangleContext>() // stores the central rectangles

            for(rectangle in rightMatchRectangles) {
                if(nearZero(rectangle.p1.x - contextMin,epsilon)) {
                    newMatchRectangles.add(rectangle)
                } else { // split in 2
                    newMatchRectangles.add(RectangleContext(DoubleVector2D(contextMin,rectangle.p1.y), rectangle.p2))
                    rectangleContextsToAdd.add(RectangleContext(rectangle.p1, DoubleVector2D(contextMin,rectangle.p2.y)))
                }
            }
            return newMatchRectangles
        }

        val iterLeft = leftRectangles.iterator()
        val iterRight = rightRectangles.iterator()

        var left: RectangleContext = iterLeft.next()
        var right: RectangleContext = iterRight.next()

        val leftMatchRectangles = mutableListOf<RectangleContext>()
        val rightMatchRectangles = mutableListOf<RectangleContext>()

        while (true) {

            while (left.p1.y  < right.p1.y - epsilon && iterLeft.hasNext())  left = iterLeft.next()
            while (right.p1.y < left.p1.y  - epsilon && iterRight.hasNext()) right = iterRight.next()

            if(nearZero(left.p1.y - right.p1.y,epsilon)) {
                leftMatchRectangles.add(left)
                rightMatchRectangles.add(right)

                while (left.p2.y  < right.p2.y - epsilon && iterLeft.hasNext())  {
                    val leftPrevious = left
                    left = iterLeft.next()
                    if (!nearZero(leftPrevious.p2.y - left.p1.y, epsilon)) break
                    leftMatchRectangles.add(left)
                }
                while (right.p2.y < left.p2.y - epsilon && iterRight.hasNext()) {
                    val rightPrevious = right
                    right = iterRight.next()
                    if (!nearZero(rightPrevious.p2.y - right.p1.y, epsilon)) break
                    rightMatchRectangles.add(right)
                }
                if(nearZero(leftMatchRectangles.last().p2.y - rightMatchRectangles.last().p2.y,epsilon)) {

                    var contextMinLeft = Double.MIN_VALUE
                    var contextMaxLeft = Double.MAX_VALUE

                    for(leftRectangle in rightMatchRectangles) {
                        if (leftRectangle.p1.x > contextMinLeft) contextMinLeft = leftRectangle.p1.x
                        if (leftRectangle.p1.x < contextMaxLeft) contextMaxLeft = leftRectangle.p1.x
                    }

                    var contextMinRight = Double.MAX_VALUE
                    var contextMaxRight = Double.MIN_VALUE

                    for(rightRectangle in leftMatchRectangles) {
                        if (rightRectangle.p2.x < contextMinRight) contextMinRight = rightRectangle.p2.x
                        if (rightRectangle.p2.x > contextMaxRight) contextMaxRight = rightRectangle.p2.x
                    }

                    rectangleContextsToDelete.addAll(leftMatchRectangles)
                    rectangleContextsToDelete.addAll(rightMatchRectangles)

                    rectangleContextsToAdd.add(mergeRectangles(
                        normalizeLeftRectangles(leftMatchRectangles, contextMinRight, contextMaxRight),  // leftRectangles are in the right side of the merge
                        normalizeRightRectangles(rightMatchRectangles, contextMinLeft, contextMaxLeft))) // rightRectangles are in the left side of the merge
                }

                if(iterLeft.hasNext()) {
                    if(left == leftMatchRectangles.last()) left = iterLeft.next()
                } else
                    break

                if(iterRight.hasNext()) {
                    if(right == rightMatchRectangles.last()) right = iterRight.next()
                } else
                    break
            } else if(left.p1.y < right.p1.y) {
                if(iterLeft.hasNext()) left = iterLeft.next() else break
            } else {
                if(iterRight.hasNext()) right = iterRight.next() else break
            }
            leftMatchRectangles.clear()
            rightMatchRectangles.clear()
        }
    }

    fun mergeNeighborsBottomAndTop(
        bottomRectangles: ObjectAVLTreeSet<RectangleContext>,
        topRectangles: ObjectAVLTreeSet<RectangleContext>,
        rectangleContextsToAdd: MutableList<RectangleContext>,
        rectangleContextsToDelete: MutableList<RectangleContext>) {

        fun  normalizeBottomRectangles(matchRectangles: MutableList<RectangleContext>,contextMin: Double, contextMax: Double): MutableList<RectangleContext> {
            // bottom rectangles are to the top of the merge
            //
            if(nearZero(contextMin - contextMax,epsilon)) // all contexts with same width
                return matchRectangles

            val newMatchRectangles = mutableListOf<RectangleContext>() // stores the central rectangles

            for(rectangle in matchRectangles) {
                if(nearZero(rectangle.p2.y - contextMin,epsilon)) {
                    newMatchRectangles.add(rectangle)
                } else { // split in 2
                    newMatchRectangles.add(RectangleContext(rectangle.p1, DoubleVector2D(rectangle.p2.x, contextMin)))
                    rectangleContextsToAdd.add(RectangleContext(DoubleVector2D(rectangle.p1.x, contextMin), rectangle.p2))
                }
            }
            return newMatchRectangles
        }

        fun  normalizeTopRectangles(topMatchRectangles: MutableList<RectangleContext>,contextMin: Double, contextMax: Double): MutableList<RectangleContext> {
            // top rectangles are to the bottom of the merge
            //
            if(nearZero(contextMin - contextMax,epsilon)) // all contexts with same width
                return topMatchRectangles

            val newMatchRectangles = mutableListOf<RectangleContext>() // stores the central rectangles

            for(rectangle in topMatchRectangles) {
                if(nearZero(rectangle.p1.y - contextMin,epsilon)) {
                    newMatchRectangles.add(rectangle)
                } else { // split in 2
                    newMatchRectangles.add(RectangleContext(DoubleVector2D(rectangle.p1.x,contextMin), rectangle.p2))
                    rectangleContextsToAdd.add(RectangleContext(rectangle.p1, DoubleVector2D(rectangle.p2.x,contextMin)))
                }
            }
            return newMatchRectangles
        }

        val iterBottom = bottomRectangles.iterator()
        val iterTop = topRectangles.iterator()

        var bottom: RectangleContext = iterBottom.next()
        var top: RectangleContext = iterTop.next()

        val bottomMatchRectangles = mutableListOf<RectangleContext>()
        val topMatchRectangles = mutableListOf<RectangleContext>()

        while (true) {

            while (bottom.p1.x  < top.p1.x - epsilon && iterBottom.hasNext())  bottom = iterBottom.next()
            while (top.p1.x < bottom.p1.x  - epsilon && iterTop.hasNext()) top = iterTop.next()

            if(nearZero(bottom.p1.x - top.p1.x,epsilon)) {
                bottomMatchRectangles.add(bottom)
                topMatchRectangles.add(top)

                while (bottom.p2.x  < top.p2.x - epsilon && iterBottom.hasNext())  {
                    val bottomPrevious = bottom
                    bottom = iterBottom.next()
                    if (!nearZero(bottomPrevious.p2.x - bottom.p1.x, epsilon)) break
                    bottomMatchRectangles.add(bottom)
                }
                while (top.p2.x < bottom.p2.x  - epsilon && iterTop.hasNext()) {
                    val topPrevious = top
                    top = iterTop.next()
                    if (!nearZero(topPrevious.p2.x - top.p1.x, epsilon)) break
                    topMatchRectangles.add(top)
                }
                if(nearZero(bottomMatchRectangles.last().p2.x - topMatchRectangles.last().p2.x,epsilon)) {

                    var contextMinBottom = Double.MIN_VALUE
                    var contextMaxBottom = Double.MAX_VALUE

                    for(bottomRectangle in topMatchRectangles) {
                        if (bottomRectangle.p1.y > contextMinBottom) contextMinBottom = bottomRectangle.p1.y
                        if (bottomRectangle.p1.y < contextMaxBottom) contextMaxBottom = bottomRectangle.p1.y
                    }

                    var contextMinTop = Double.MAX_VALUE
                    var contextMaxTop = Double.MIN_VALUE

                    for(topRectangle in bottomMatchRectangles) {
                        if (topRectangle.p2.y < contextMinTop) contextMinTop = topRectangle.p2.y
                        if (topRectangle.p2.y > contextMaxTop) contextMaxTop = topRectangle.p2.y
                    }

                    rectangleContextsToDelete.addAll(bottomMatchRectangles)
                    rectangleContextsToDelete.addAll(topMatchRectangles)

                    rectangleContextsToAdd.add(mergeRectangles(
                        normalizeBottomRectangles(bottomMatchRectangles, contextMinTop, contextMaxTop), // bottomRectangles are in the top side of the merge
                        normalizeTopRectangles(topMatchRectangles, contextMinBottom, contextMaxBottom))) // topRectangles are in the bottom side of the merge
                }
                if(iterBottom.hasNext()) {
                    if(bottom == bottomMatchRectangles.last()) bottom = iterBottom.next()
                } else
                    break

                if(iterTop.hasNext()) {
                    if(top == topMatchRectangles.last()) top = iterTop.next()
                } else
                    break
            } else if(bottom.p1.x < top.p1.x) {
                if(iterBottom.hasNext()) bottom = iterBottom.next() else break
            } else {
                if(iterTop.hasNext()) top = iterTop.next() else break
            }
            bottomMatchRectangles.clear()
            topMatchRectangles.clear()
        }
    }

    fun mergeNeighbors(rectContextsByLow: Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>,
                       rectContextsByHigh: Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>,
                       mergeNeighborsLowHigh: (lowRectangles: ObjectAVLTreeSet<RectangleContext>,
                                               highRectangles: ObjectAVLTreeSet<RectangleContext>,
                                               rectangleContextsToAdd: MutableList<RectangleContext>,
                                               rectangleContextsToDelete: MutableList<RectangleContext>) -> Unit) {

        val iterLow = rectContextsByLow.iterator()
        val iterHigh = rectContextsByHigh.iterator()

        var low: MutableMap.MutableEntry<Double,ObjectAVLTreeSet<RectangleContext>>
        var high: MutableMap.MutableEntry<Double,ObjectAVLTreeSet<RectangleContext>>
        val rectangleContextsToAdd = mutableListOf<RectangleContext>()
        val rectangleContextsToDelete = mutableListOf<RectangleContext>()

        while (iterLow.hasNext() && iterHigh.hasNext()) {
            low = iterLow.next()
            high = iterHigh.next()

            while ((low.value.isEmpty() || low.key < high.key - epsilon) && iterLow.hasNext()) low = iterLow.next()
            while ((high.value.isEmpty() || high.key < low.key - epsilon) && iterHigh.hasNext()) high = iterHigh.next()

            if (!low.value.isEmpty() && !high.value.isEmpty() && nearZero(low.key - high.key, epsilon))
                mergeNeighborsLowHigh(low.value, high.value, rectangleContextsToAdd, rectangleContextsToDelete)

            for (rect in rectangleContextsToDelete) {
                rectContextsByBottom.get(rect.p1.y)?.remove(rect)
                rectContextsByTop.get(rect.p2.y)?.remove(rect)
                rectContextsByLeft.get(rect.p1.x)?.remove(rect)
                rectContextsByRight.get(rect.p2.x)?.remove(rect)
            }
            size -= rectangleContextsToDelete.size
            rectangleContextsToDelete.clear()
        }

        size += rectangleContextsToAdd.size

        for (rect in rectangleContextsToAdd) {
            addTo(rect.p1.y, rect, rectContextsByBottom, SimpleCompareDoubleRectangle2DXY)
            addTo(rect.p2.y, rect, rectContextsByTop, SimpleCompareDoubleRectangle2DXY)
            addTo(rect.p1.x, rect, rectContextsByLeft, SimpleCompareDoubleRectangle2DYX)
            addTo(rect.p2.x, rect, rectContextsByRight, SimpleCompareDoubleRectangle2DYX)
        }
    }

    fun setNeighbors(
        rectContextsByLow:  Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>,
        rectContextsByHigh: Double2ReferenceAVLTreeMap<ObjectAVLTreeSet<RectangleContext>>,
        xOrY: Int,
        side: Side,
        intersects: (b: RectangleContext, a: RectangleContext) -> Boolean) {

        fun setNeighbors(
            lowRectangles: ObjectAVLTreeSet<RectangleContext>,
            highRectangles: ObjectAVLTreeSet<RectangleContext>) {

            val iterLow = lowRectangles.iterator()
            val iterHigh = highRectangles.iterator()

            var low: RectangleContext = iterLow.next()
            var high: RectangleContext = iterHigh.next()

            while (true) {
                //println("setNeighbors $side inner low = ${low.toStringLess()} high = ${high.toStringLess()}")

                while (low.p2[xOrY] < high.p1[xOrY] - epsilon && iterLow.hasNext())  {
                    low = iterLow.next()
                    //println("setNeighbors $side inner (low.p2[$xOrY] < high.p1[$xOrY]) low = $${low.toStringLess()} high = ${high.toStringLess()}")
                }
                while (high.p2[xOrY] < low.p1[xOrY] - epsilon && iterHigh.hasNext()) {
                    high = iterHigh.next()
                    //println("setNeighbors $side inner (high.p2[$xOrY] < low.p1[$xOrY]) low = $${low.toStringLess()} high = ${high.toStringLess()}")
                }

                if(intersects(high,low)) low.add(high,side)

                if(low.p2[xOrY] < high.p2[xOrY] - epsilon) {
                    if(iterLow.hasNext()) {
                        low = iterLow.next()
                        //println("setNeighbors $side inner (low.p2[$xOrY] < high.p2[$xOrY]) low = $${low.toStringLess()} high = ${high.toStringLess()}")
                    } else break
                }
                else if(high.p2[xOrY] < low.p2[xOrY] - epsilon){
                    if(iterHigh.hasNext()) {
                        high = iterHigh.next()
                        //println("setNeighbors $side inner (high.p2[$xOrY] < low.p2[$xOrY]) low = $${low.toStringLess()} high = ${high.toStringLess()}")
                    } else break
                } else {
                    if(iterLow.hasNext()) {
                        low = iterLow.next()
                        //println("setNeighbors $side inner (else) low = $${low.toStringLess()} high = ${high.toStringLess()}")
                    } else break
                    if(iterHigh.hasNext()) {
                        high = iterHigh.next()
                        //println("setNeighbors $side inner (else) low = $${low.toStringLess()} high = ${high.toStringLess()}")
                    } else break
                }
            }
        }

        val iterLow = rectContextsByLow.iterator()
        val iterHigh = rectContextsByHigh.iterator()

        var low = iterLow.next()
        var high =  iterHigh.next()
        //println("setNeighbors $side low.first.key = ${rectContextsByLow.keys.firstDouble()} low.last.key = ${rectContextsByLow.keys.lastDouble()}")
        //println("setNeighbors $side high.first.key = ${rectContextsByHigh.keys.firstDouble()} high.last.key = ${rectContextsByHigh.keys.lastDouble()}")

        while (true) {
            //println("setNeighbors $side low.key = ${low.key} high.key = ${high.key}")
            while (low.key  < high.key - epsilon)  {
                if(iterLow.hasNext()) low = iterLow.next() else break
                //println("setNeighbors $side (low.key  < high.key) low.key = ${low.key} high.key = ${high.key}")
            }
            while (high.key < low.key  - epsilon) {
                if(iterHigh.hasNext()) high = iterHigh.next() else break
                //println("setNeighbors $side (high.key < low.key) low.key = ${low.key} high.key = ${high.key}")
            }
            if(nearZero(low.key - high.key, epsilon)) {
                if (!low.value.isEmpty() && !high.value.isEmpty()) setNeighbors(low.value, high.value)
                if(iterLow.hasNext()) low = iterLow.next() else break
                if(iterHigh.hasNext()) high = iterHigh.next() else break
            } else if(low.key  < high.key) {
                if(iterLow.hasNext()) low = iterLow.next() else break
            } else {
                if(iterHigh.hasNext()) high = iterHigh.next() else break
            }
        }
    }

    for(rect in rectContexts) {
        addTo(rect.p1.y, rect,rectContextsByBottom,SimpleCompareDoubleRectangle2DXY)
        addTo(rect.p2.y, rect,rectContextsByTop,SimpleCompareDoubleRectangle2DXY)
        addTo(rect.p1.x, rect,rectContextsByLeft,SimpleCompareDoubleRectangle2DYX)
        addTo(rect.p2.x, rect,rectContextsByRight,SimpleCompareDoubleRectangle2DYX)
        size += 1
    }

    println("result begin size = $size")

    var previousSize: Int
    var iterations = 0
    do {

        //println("result size = ${rectContextsByLeft.flatMap { it.value }.size} iterations = $iterations")
        previousSize = size

        mergeNeighbors(rectContextsByLeft,rectContextsByRight,::mergeNeighborsLeftAndRight)
        mergeNeighbors(rectContextsByBottom,rectContextsByTop,::mergeNeighborsBottomAndTop)

        //println("result   end size = ${result.size}\n  ${result.map { it.toStringLess() }}")
        //println("result   end size = $size")

        iterations += 1
    } while (previousSize > size + minDiff && iterations < maxIterations)
    println("result end size = $size iterations = $iterations")

    setNeighbors(rectContextsByLeft,rectContextsByRight,1,Side.LEFT,::intersectsY)
    setNeighbors(rectContextsByBottom,rectContextsByTop,0,Side.BOTTOM,::intersectsX)

    return rectContextsByLeft.flatMap { it.value }
}
