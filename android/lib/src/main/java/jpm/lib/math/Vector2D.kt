package jpm.lib.math

import java.lang.Math.sqrt
import java.lang.Math.round
import java.lang.Math.acos

/**
 * Created by jm on 12/03/17.
 *
 */
abstract class Vector2D<T: Vector2D<T,N>, N:Number>(val x: N, val y: N): Vector<T,N> {

    abstract infix fun lt_x(b: T): Boolean
    abstract infix fun gt_x(b: T): Boolean
    abstract infix fun eq_x(b: T): Boolean
    abstract infix fun le_x(b: T): Boolean
    abstract infix fun ge_x(b: T): Boolean

    abstract infix fun lt_y(b: T): Boolean
    abstract infix fun gt_y(b: T): Boolean
    abstract infix fun eq_y(b: T): Boolean
    abstract infix fun le_y(b: T): Boolean
    abstract infix fun ge_y(b: T): Boolean

    /**
     * get(0) == x
     * get(1) == y
     */
    override operator fun get(pos: Int): N = if(pos == 0) x else y

    abstract infix fun crossProduct(b: T): N
    abstract fun perpendicular() : Vector2D<T,N>

    abstract infix fun rotate(angle: Double): T
    abstract fun rotate90(): T
    abstract fun rotateMinus90(): T
    abstract fun rotate180(): T

    companion object {
        fun <T: Vector2D<T,N>, N:Number> new(x: N, y: N): T
                = if(x is Double) DoubleVector2D(x, y as Double) as T
                  else IntVector2D(x as Int, y as Int) as T
    }
}

class DoubleVector2D(x:Double, y:Double): Vector2D<DoubleVector2D,Double>(x,y) {

    override fun lt_x(b: DoubleVector2D): Boolean = x < b.x
    override fun gt_x(b: DoubleVector2D): Boolean = x > b.x
    override fun eq_x(b: DoubleVector2D): Boolean = x == b.x
    override fun le_x(b: DoubleVector2D): Boolean = x <= b.x
    override fun ge_x(b: DoubleVector2D): Boolean = x >= b.x

    override fun lt_y(b: DoubleVector2D): Boolean = y < b.y
    override fun gt_y(b: DoubleVector2D): Boolean = y > b.y
    override fun eq_y(b: DoubleVector2D): Boolean = y == b.y
    override fun le_y(b: DoubleVector2D): Boolean = y <= b.y
    override fun ge_y(b: DoubleVector2D): Boolean = y >= b.y

    override fun strictGt(b: DoubleVector2D): Boolean = x > b.x && y > b.y

    override fun compareTo(other: DoubleVector2D): Int {
        if(y < other.y) return -1
        if(y > other.y) return 1
        if(x < other.x) return -1
        if(x > other.x) return 1
        return 0
    }

    override fun equals(other: Any?) = compareTo(other as DoubleVector2D) == 0

    override fun hashCode(): Int
            = 31 * x.hashCode() + y.hashCode()

    override operator fun plus(b: DoubleVector2D): DoubleVector2D
            = DoubleVector2D(x + b.x, y + b.y)

    override operator fun minus(b: DoubleVector2D): DoubleVector2D
            = DoubleVector2D(x - b.x, y - b.y)

    override operator fun times(k: Double): DoubleVector2D
            = DoubleVector2D(k * x, k * y)

    override operator fun div(k: Double): DoubleVector2D
            = DoubleVector2D(x / k, y / k)

    override operator fun unaryMinus(): DoubleVector2D
            = DoubleVector2D(-x, -y)

    override fun inv()
            = DoubleVector2D(1.0/x, 1.0/y)

    override fun product(b: DoubleVector2D): Double
            = x * b.x + y * b.y

    override fun crossProduct(b: DoubleVector2D): Double
            = x * b.y - y * b.x // = perpendicular().product(b)

    override fun perpendicular() : DoubleVector2D
            = DoubleVector2D(-y,x)

    override fun length(): Double
            = sqrt(x * x + y * y)

    override fun normalize(): DoubleVector2D
            = this / length()

    override fun angle(b: DoubleVector2D): Double
            = acos(product(b) /(this.length() * b.length()))

    override fun rotate(angle: Double): DoubleVector2D  {
        val cos = Math.cos(angle)
        val sin = Math.sin(angle)

        return DoubleVector2D(x * cos - y * sin, x * sin + y * cos)
    }

    override fun rotate90(): DoubleVector2D
            = perpendicular()

    override fun rotateMinus90(): DoubleVector2D
            = DoubleVector2D(y,-x)

    override fun rotate180(): DoubleVector2D
            = DoubleVector2D(-x,-y)

    override fun equals(b: DoubleVector2D): Boolean
            = x == b.x && y == b.y

    override fun nearEquals(b: DoubleVector2D, epsilon: Double): Boolean
            = nearZero(x-b.x,epsilon) && nearZero(y-b.y,epsilon)

    override fun nearEqualsRel(b: DoubleVector2D, epsilon: Double): Boolean
            = nearEqualsRel(x,b.x,epsilon) && nearEqualsRel(y,b.y,epsilon)

    override fun toString(): String = "($x,$y)"
}

class IntVector2D(x:Int, y:Int): Vector2D<IntVector2D,Int>(x,y) {

    override fun lt_x(b: IntVector2D): Boolean = x < b.x
    override fun gt_x(b: IntVector2D): Boolean = x > b.x
    override fun eq_x(b: IntVector2D): Boolean = x == b.x
    override fun le_x(b: IntVector2D): Boolean = x <= b.x
    override fun ge_x(b: IntVector2D): Boolean = x >= b.x

    override fun lt_y(b: IntVector2D): Boolean = y < b.y
    override fun gt_y(b: IntVector2D): Boolean = y > b.y
    override fun eq_y(b: IntVector2D): Boolean = y == b.y
    override fun le_y(b: IntVector2D): Boolean = y <= b.y
    override fun ge_y(b: IntVector2D): Boolean = y >= b.y

    override fun strictGt(b: IntVector2D): Boolean = x > b.x && y > b.y

    constructor(v: DoubleVector2D): this(round(v.x.toFloat()),round(v.y.toFloat()))

    override fun compareTo(other: IntVector2D): Int {
        if(y < other.y) return -1
        if(y > other.y) return 1
        if(x < other.x) return -1
        if(x > other.x) return 1
        return 0
    }

    override fun equals(other: Any?) = compareTo(other as IntVector2D) == 0

    override fun hashCode(): Int
        = 31 * x.hashCode() + y.hashCode()

    override operator fun plus(b: IntVector2D): IntVector2D
            = IntVector2D(x + b.x, y + b.y)

    override operator fun minus(b: IntVector2D): IntVector2D
            = IntVector2D(x - b.x, y - b.y)

    override operator fun times(k: Int): IntVector2D
            = IntVector2D(k * x, k * y)

    override operator fun div(k: Int): IntVector2D
            = IntVector2D(Math.round(x / k.toDouble()).toInt(), Math.round(y / k.toDouble()).toInt())

    override operator fun unaryMinus(): IntVector2D
            = IntVector2D(-x, -y)

    override fun inv()
            = IntVector2D(Math.round(1.0 / x).toInt(), Math.round(1.0 / y).toInt())

    override fun product(b: IntVector2D): Int
            = x * b.x + y * b.y

    override fun crossProduct(b: IntVector2D): Int
            = x * b.y - y * b.x // = perpendicular().product(b)

    override fun perpendicular() : IntVector2D
            = IntVector2D(-y,x)

    override fun length(): Int
            = throw NotImplementedError()

    override fun normalize(): IntVector2D
            = throw NotImplementedError()

    override fun angle(b: IntVector2D): Double
            = acos(product(b).toDouble() / (this.length() * b.length()))

    override fun rotate(angle: Double): IntVector2D  {
        val cos = Math.cos(angle)
        val sin = Math.sin(angle)

        return IntVector2D(round(x * cos - y * sin), round(x * sin + y * cos))
    }

    override fun rotate90(): IntVector2D
            = perpendicular()

    override fun rotateMinus90(): IntVector2D
            = IntVector2D(y,-x)

    override fun rotate180(): IntVector2D
            = IntVector2D(-x,-y)

    override fun equals(b: IntVector2D): Boolean
            = x == b.x && y == b.y

    override fun nearEquals(b: IntVector2D, epsilon: Int): Boolean
            = throw NotImplementedError()

    override fun nearEqualsRel(b: IntVector2D, epsilon: Int): Boolean
            = throw NotImplementedError()

    private fun round(v: Double): Int = Math.round(v).toInt()

    override fun toString(): String = "IntVector2D($x,$y)"
}

data class BarycentricCoordinates(val aC: Double, val bC: Double, val cC: Double)

/**
 * return true if point with coordinate c relative to triangle a,b,c is inside triangle
 */
fun insideTriangle(c: BarycentricCoordinates): Boolean
        = (0.0 <= c.aC && c.aC <= 1) && (0.0 <= c.bC && c.bC <= 1) && (0.0 <= c.cC && c.cC <= 1)

class BarycentricReference(val a: DoubleVector2D, b: DoubleVector2D, val c: DoubleVector2D) {

    private val v0 = b - a
    private val v1 = c-a

    private val d00 = v0.product(v0)
    private val d01 = v0.product(v1)
    private val d11 = v1.product(v1)
    private val denom = d00 * d11 - d01 * d01

    /**
     * Compute barycentric coordinate  for point p with respect to triangle (a, b, c)
     */
    fun coordinatesOf(p: DoubleVector2D): BarycentricCoordinates {

        val v2 = p - a

        val d20 = v2.product(v0)
        val d21 = v2.product(v1)

        val bC = (d11 * d20 - d01 * d21) / denom
        val cC = (d00 * d21 - d01 * d20) / denom
        val aC = 1.0 - bC - cC

        return BarycentricCoordinates(aC,bC,cC)
    }
}