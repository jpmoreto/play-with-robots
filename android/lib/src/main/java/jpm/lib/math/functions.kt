package jpm.lib.math

import java.util.*

/**
 * Created by jm on 19/02/17.
 *
 * http://geomalgorithms.com/a12-_hull-3.html
 * http://www.algomation.com/algorithm/graham-scan-convex-hull
 *
 * http://www.sosmath.com/trig/Trig5/trig5/trig5.html
 * https://en.wikipedia.org/wiki/List_of_trigonometric_identities
 *
 * http://algs4.cs.princeton.edu/99hull/GrahamScan.java.html
 * http://algs4.cs.princeton.edu/99hull/Point2D.java.html
 *
 * http://stackoverflow.com/questions/1585459/whats-the-most-efficient-way-to-detect-triangle-triangle-intersections
 * http://blackpawn.com/texts/pointinpoly/default.html
 *
 * http://realtimecollisiondetection.net/
 * https://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm
 * http://www.java-gaming.org/index.php?topic=30375.0
 * http://seb.ly/2009/05/super-fast-trianglerectangle-intersection-test/
 * https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
 *
 * https://github.com/libgdx/libgdx *************************************
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Intersector.java
 *
 * sin(-teta) = -sin(teta)
 * cos(-teta) =  cos(teta)
 */

data class LineSegment<T: Vector<T,N>,N:Number>(val p1: T, val p2: T)

data class Rectangle<T: Vector2D<T,N>, N:Number>(val p1: T, val p2: T) {

    init {
        assert(p2 strictGt p1)
    }
}

fun <R: Rectangle<T,N>, T: Vector2D<T,N>, N:Number> intersect(r1: R, r2: R): Boolean =
    r1.p1 le_x r2.p2 && r1.p2 ge_x r2.p1 && r2.p2 le_y r2.p2 && r1.p2 ge_y r2.p1

fun <T: Vector2D<T,N>, N:Number> intersection(r1: Rectangle<T,N>, r2: Rectangle<T,N>): Rectangle<T,N>? {
    /*
      r1.p1.x < r2.p1.x && r1.p1.y < r2.p1.y
      r1.p1.x < r2.p1.x && r1.p1.y >= r2.p1.y

      r1.p1.x >= r2.p1.x && r1.p1.y < r2.p1.y
      r1.p1.x >= r2.p1.x && r1.p1.y >= r2.p1.y
    */
    if (r1.p1 lt_x r2.p1) {
        if (r1.p1 lt_y r2.p1) {
            val p1 = r2.p1
            val p2 = r1.p2
            return if(p2 ge_x p1 && p2 ge_y p1) Rectangle(p1,p2) else null
        }
        else {
            val p1 = Vector2D.new<T,N>(r2.p1.x,r1.p1.y)
            val p2 = Vector2D.new<T,N>(r1.p2.x,r2.p2.y)
            return if(p2 ge_x p1 && p2 ge_y p1) Rectangle(p1,p2) else null
        }
    } else {
        if (r1.p1 lt_y r2.p1) {
            val p1 = Vector2D.new<T,N>(r1.p1.x,r2.p1.y)
            val p2 = Vector2D.new<T,N>(r2.p2.x,r1.p2.y)
            return if(p2 ge_x p1 && p2 ge_y p1) Rectangle(p1,p2) else null
        } else {
            val p1 = r1.p1
            val p2 = r2.p2
            return if(p2 ge_x p1 && p2 ge_y p1) Rectangle(p1,p2) else null
        }
    }
}

fun <T: Vector2D<T,N>, N: Number> lineIntersection(a1: T, a2: T, b1: T, b2: T): Pair<T,T>? {
    /*
      The line segments are collinear and overlapping, meaning that they share more than one point.
      The line segments are collinear but not overlapping, sort of "chunks" of the same line.
      The line segments are parallel and non-intersecting.
      The line segments have a single point of intersection.
      The line segments do not intersect.
    */
    val r = a2 - a1
    val s = b2 - b1
    val rxs = r crossProduct s
    val rxsDouble = rxs.toDouble()

    val b1_minus_a1 = b1 - a1

    val qpxr = (b1_minus_a1 crossProduct r).toDouble()

    // If r x s = 0 and (q - p) x r = 0, then the two lines are collinear.
    if (nearZero(rxsDouble, 1E-20) && nearZero(qpxr, 1E-20)) {
        // 1. If either  0 <= (q - p) * r <= r * r or 0 <= (p - q) * s <= * s
        // then the two lines are overlapping,
        val a1_minus_b1 = a1 - b1
        val b1_minus_a1_prod_r = (b1_minus_a1 product r).toDouble()
        val a1_minus_b1_prod_s = (a1_minus_b1 product s).toDouble()

        if (0.0 <= b1_minus_a1_prod_r && b1_minus_a1_prod_r <= (r product r).toDouble() ||
            0.0 <= a1_minus_b1_prod_s && a1_minus_b1_prod_s <= (s product s).toDouble()) {

            if(a1.x == a2.x || b1.x == b2.x) { // vertical line
                val a1_y = a1.y.toDouble()
                val a2_y = a2.y.toDouble()
                val b1_y = b1.y.toDouble()
                val b2_y = b2.y.toDouble()

                val p1 = if(Math.min(b1_y,b2_y) <= a1_y && a1_y <= Math.max(b1_y,b2_y)) a1 else a2
                val p2 = if(Math.min(a1_y,a2_y) <= b1_y && b1_y <= Math.max(a1_y,a2_y)) b1 else b2

                return Pair(p1,p2)
            } else {
                val a1_x = a1.x.toDouble()
                val a2_x = a2.x.toDouble()
                val b1_x = b1.x.toDouble()
                val b2_x = b2.x.toDouble()

                val p1 = if(Math.min(b1_x,b2_x) <= a1_x && a1_x <= Math.max(b1_x,b2_x)) a1 else a2
                val p2 = if(Math.min(a1_x,a2_x) <= b1_x && b1_x <= Math.max(a1_x,a2_x)) b1 else b2

                return Pair(p1,p2)
            }
        }

        // 2. If neither 0 <= (q - p) * r = r * r nor 0 <= (p - q) * s <= s * s
        // then the two lines are collinear but disjoint.
        // No need to implement this expression, as it follows from the expression above.
        return null
    }

    // 3. If r x s = 0 and (q - p) x r != 0, then the two lines are parallel and non-intersecting.
    if (nearZero(rxsDouble,1E-30))
        return null

    // t = (q - p) x s / (r x s)
    val cp = b1_minus_a1 crossProduct s
    val t = cp.toDouble() / rxsDouble

    // u = (q - p) x r / (r x s)
    val u = qpxr / rxsDouble

    // 4. If rxs != 0 and 0 <= t <= 1 and 0 <= u <= 1
    // the two line segments meet at the point p + t r = q + u s.
    if (0 <= t && t <= 1.0 && 0.0 <= u && u <= 1.0) {
        // We can calculate the intersection point using either t or u.
        val intersection = a1 + (r * cp) / rxs

        // An intersection was found.
        return Pair(intersection,intersection)
    }

    // 5. Otherwise, the two line segments are not parallel but do not intersect.
    return null
}

fun intersection(l1: LineSegment<DoubleVector2D,Double>, l2: LineSegment<DoubleVector2D,Double>): LineSegment<DoubleVector2D,Double>? {
    /*
      The line segments are collinear and overlapping, meaning that they share more than one point.
      The line segments are collinear but not overlapping, sort of "chunks" of the same line.
      The line segments are parallel and non-intersecting.
      The line segments have a single point of intersection.
      The line segments do not intersect.
    */

    val r = l1.p2 - l1.p1
    val s = l2.p2 - l2.p1
    val rxs = r crossProduct s
    val minusL2P1_L1P1 = l2.p1 - l1.p1

    val qpxr = minusL2P1_L1P1 crossProduct r

    // If r x s = 0 and (q - p) x r = 0, then the two lines are collinear.
    if (nearZero(rxs,1E-30) && nearZero(qpxr,1E-30)) {
        // 1. If either  0 <= (q - p) * r <= r * r or 0 <= (p - q) * s <= * s
        // then the two lines are overlapping,
        val minusL1P1_L2P1 = l1.p1 - l2.p1

        if (0.0 <= minusL2P1_L1P1 product r && minusL2P1_L1P1 product r <= r product r || 0.0 <= minusL1P1_L2P1 product s && minusL1P1_L2P1 product s <= s product s) {

            if(l1.p1.x == l1.p2.x || l2.p1.x == l2.p2.x) { // vertical line
                val p1 = if(Math.min(l2.p1.y,l2.p2.y) <= l1.p1.y && l1.p1.y <= Math.max(l2.p1.y,l2.p2.y)) l1.p1 else l1.p2
                val p2 = if(Math.min(l1.p1.y,l1.p2.y) <= l2.p1.y && l2.p1.y <= Math.max(l1.p1.y,l1.p2.y)) l2.p1 else l2.p2

                return LineSegment(p1,p2)
            } else {
                val p1 = if(Math.min(l2.p1.x,l2.p2.x) <= l1.p1.x && l1.p1.x <= Math.max(l2.p1.x,l2.p2.x)) l1.p1 else l1.p2
                val p2 = if(Math.min(l1.p1.x,l1.p2.x) <= l2.p1.x && l2.p1.x <= Math.max(l1.p1.x,l1.p2.x)) l2.p1 else l2.p2

                return LineSegment(p1,p2)
            }
        }

        // 2. If neither 0 <= (q - p) * r = r * r nor 0 <= (p - q) * s <= s * s
        // then the two lines are collinear but disjoint.
        // No need to implement this expression, as it follows from the expression above.
        return null
    }

    // 3. If r x s = 0 and (q - p) x r != 0, then the two lines are parallel and non-intersecting.
    if (nearZero(rxs,1E-30))
        return null

    // t = (q - p) x s / (r x s)
    val t = (minusL2P1_L1P1 crossProduct s) / rxs

    // u = (q - p) x r / (r x s)

    val u = qpxr / rxs

    // 4. If rxs != 0 and 0 <= t <= 1 and 0 <= u <= 1
    // the two line segments meet at the point p + t r = q + u s.
    if (0 <= t && t <= 1.0 && 0.0 <= u && u <= 1.0) {
        // We can calculate the intersection point using either t or u.
        val intersection = l1.p1 + r * t

        // An intersection was found.
        return LineSegment(intersection,intersection)
    }

    // 5. Otherwise, the two line segments are not parallel but do not intersect.
    return null

}

/**
 * Returns true if a→b→c is a counterclockwise turn.
 * @param a first point
 * @param b second point
 * @param c third point
 * @return { -1, 0, +1 } if a→b→c is a { clockwise, collinear; counterclocwise } turn.
 */

fun ccw(a: DoubleVector2D, b: DoubleVector2D, c: DoubleVector2D): Int {
    val area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
    if (area2 < 0)
        return -1
    else if (area2 > 0)
        return +1
    else
        return 0
}

fun polarOrder(p1: DoubleVector2D, p2: DoubleVector2D): Boolean {
    if (p1.y == 0.0 && p1.x > 0.0)
        return true //angle of p1 is 0, thus p2 > p1

    if (p2.y == 0.0 && p2.x > 0.0)
        return false //angle of p2 is 0 , thus p1 > p2

    if (p1.y > 0.0 && p2.y < 0.0)
        return true //p1 is between 0 and 180, p2 between 180 and 360

    if (p1.y < 0.0 && p2.y > 0.0)
        return false

    return p1 crossProduct p2 > 0 //return true if p1 is clockwise from p2
}

class PolarOrderComparator(val basePoint: DoubleVector2D): Comparator<DoubleVector2D> {
    override fun compare(lhs: DoubleVector2D, rhs: DoubleVector2D): Int {
        val dx1 = lhs.x - basePoint.x
        val dy1 = lhs.y - basePoint.y
        val dx2 = rhs.x - basePoint.x
        val dy2 = rhs.y - basePoint.y

        if (dy1 >= 0 && dy2 < 0)
            return -1    // q1 above; q2 below
        else if (dy2 >= 0 && dy1 < 0)
            return +1    // q1 below; q2 above
        else if (dy1 == 0.0 && dy2 == 0.0) {            // 3-collinear and horizontal
            if (dx1 >= 0 && dx2 < 0)
                return -1
            else if (dx2 >= 0 && dx1 < 0)
                return +1
            else
                return 0
        } else
            return -ccw(basePoint, lhs, rhs)     // both above or below

        // Note: ccw() recomputes dx1, dy1, dx2, and dy2
    }
}

fun power2(v: Double) = v * v

/**
 * @l coordinate in local frame
 * @angle in radian - angle between Global and Local frame.
 *        On star we can assume that the robot is oriented with global frame,
 *        or if we have a compass we can assume that global frame Y points to north and X to west.
 *        Global Frame orientation = Local frame orientation + angle
 *        (angle positive in anticlockwise - like trigonometric circle)
 * @return coordinate in global frame
*/
fun <T: Vector2D<T,N>, N: Number> toGlobalCoordinates(l: T, angle: Double): T =  l rotate (-angle)

/**
 * @g coordinate in global frame
 * @angle in radian - angle between Global and Local frame.
 *        On star we can assume that the robot is oriented with global frame,
 *        or if we have a compass we can assume that global frame Y points to north and X to west.
 *        Global Frame orientation = Local frame orientation + angle
 *        (angle positive in anticlockwise - like trigonometric circle)
 * @return coordinate in local frame
 */
fun <T: Vector2D<T,N>, N: Number> toLocalCoordinates(g: T, angle: Double): T  = g rotate angle

fun rotation(startAngle: Double, endAngle: Double) = endAngle - startAngle

fun nearZero(value: Double, epsilon: Double) = -epsilon <= value && value <= epsilon

fun nearEqualsRel(valueA: Double, valueB: Double, epsilon: Double)
        = Math.abs(valueA - valueB) <= epsilon * Math.max(Math.abs(valueA), Math.abs(valueB))

/**
 * Calculates the convex hull of a set of points
 * @points array of points, must pass a copy because it will be changed
 * @return a ordered list of points that defines the polign that represents the convex hull
 */
fun convexHull(points: Array<DoubleVector2D>): Array<DoubleVector2D> {
    fun toArray(hull: Stack<DoubleVector2D>): Array<DoubleVector2D> {
        val res = Array<DoubleVector2D>(hull.size) { i ->
            hull.pop()
        }
        res.reverse()
        return res
    }

    val n = points.size

    if (points.isEmpty()) return arrayOf()

    // preprocess so that points[0] has lowest y-coordinate; break ties by x-coordinate
    // points[0] is an extreme point of the convex hull
    // (alternatively, could do easily in linear time)
    Arrays.sort(points)

    // sort by polar angle with respect to base point a[0],
    // breaking ties by distance to a[0]
    Arrays.sort(points, 1, n, PolarOrderComparator(points[0]))

    val hull = Stack<DoubleVector2D>()

    hull.push(points[0])

    // find index k1 of first point not equal to a[0]
    var k1 = 1

    while (k1 < n) {
        if (points[0] != points[k1]) break
        k1++
    }
    if (k1 < n) {

        // find index k2 of first point not collinear with a[0] and a[k1]
        var k2 = k1 + 1

        while (k2 < n) {
            if (ccw(points[0], points[k1], points[k2]) != 0) break
            k2++
        }
        hull.push(points[k2 - 1])    // a[k2-1] is second extreme point

        // Graham scan; note that a[n-1] is extreme point different from a[0]
        for (i in k2..n - 1) {
            var top = hull.pop()
            while (ccw(hull.peek(), top, points[i]) <= 0) {
                top = hull.pop()
            }
            hull.push(top)
            hull.push(points[i])
        }
    }
    return toArray(hull)
}

fun isConvex(points: Array<DoubleVector2D>): Boolean {
   val n = points.size
    if (n <= 2) return true

    for (i in 0..n -1) {
        if (ccw(points[i], points[(i+1) % n], points[(i+2) % n]) <= 0) {
            return false
        }
    }
    return true
}

/**
 * @x point
 * @l: line segment
 * @return > 0 if p is to the left of the line, < 0 if p is to the right of the line, 0 if p is a point of the line
 */
fun pointRelativePositionToLine(p: DoubleVector2D, l: LineSegment<DoubleVector2D,Double>): Double =
        ((l.p2.x - l.p1.x) * (p.y - l.p1.y) - (l.p2.y - l.p1.y) * (p.x - l.p1.x))
