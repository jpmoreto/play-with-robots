package jpm.lib.maps

import jpm.lib.math.DoubleVector2D
import jpm.lib.math.Vector2D

/**
 * Created by jm on 15/03/17.
 *
 */
interface Tree<T: Tree<T,V,N>, V: Vector2D<V,N>, N: Number> {

    fun size(): Int

    fun left(): N
    fun right(): N
    fun bottom(): N
    fun top(): N
    fun contains(p:V): Boolean

    //fun occupiedCircle(center: Vector2D<Int>, probability: Double): Int // return radius
    //fun freeCircle(center: Vector2D<Int>, probability: Double): Int // return radius

    //fun occupiedCircle(center: Vector2D<Int>): Int // return radius
    //fun freeCircle(center: Vector2D<Int>): Int // return radius

    /**
     * Visit all k-d tree nodes intersected by segment S = p + t * v, 0 <= t < tmax
     *
     * @p the start point of the ray
     * @v the vector with the direction
     * @tmax the distance to traverse relative to v size
     * @visit the visitor function to call for each node intersected by segment S = p + t * v, 0 <= t < tmax
     *        the visit returns true to continue visit, false to stop visit
     */
    fun intersectRay(p: DoubleVector2D, v: DoubleVector2D, tmax:Double,
                     visit: (node: T, p: DoubleVector2D, v: DoubleVector2D, tmax: Double) -> Boolean)

    fun visitAll(visit: (node: T) -> Unit)
    fun visitAllDepthFirst(visit: (node: T) -> Unit)

    fun occupied(p: V): Double

    fun free(p: V): Double

    fun getPoint(p: V): T

    fun occupied(): Double
    fun free(): Double
    //fun unknown(): Double

    fun setOccupied(p: V)
    //fun setOccupied(p1: V, p2: V)
    fun setFree(p: V)
    //fun setFree(p1: V, p2: V)

    var isLeaf: Boolean
    val center: V

    fun getDimVector(): V

    fun toStringLeaf(): String

    fun toStringNode(): String { return "" }
}
