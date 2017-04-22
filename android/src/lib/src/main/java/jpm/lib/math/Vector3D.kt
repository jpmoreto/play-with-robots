package jpm.lib.math

import java.lang.Math.abs
import java.lang.Math.acos
import java.lang.Math.sqrt
/**
 * Created by jm on 13/03/17.
 *
 */
interface Vector3D<T: Vector3D<T,N>, N:Number>: Vector<T,N> {

    val x: N
    val y: N
    val z: N

    /**
     * get(0) == x
     * get(1) == y
     * get(2) == z
     */
    override operator fun get(pos: Int): N = if(pos == 0) x else if(pos == 2) y else z

    infix fun crossProduct(b: T): T
}

data class DoubleVector3D(override val x:Double, override val y:Double, override val z:Double): Vector3D<DoubleVector3D,Double> {

    override fun strictGt(b: DoubleVector3D): Boolean = x > b.x && y > b.y && z > b.z

    override fun compareTo(other: DoubleVector3D): Int {
        if(z < other.z) return -1
        if(z > other.z) return  1
        if(y < other.y) return -1
        if(y > other.y) return  1
        if(x < other.x) return -1
        if(x > other.x) return  1
        return 0
    }

    override fun equals(other: Any?) = compareTo(other as DoubleVector3D) == 0

    override fun hashCode(): Int
            = 67 * x.hashCode() + 31 * y.hashCode() + z.hashCode()

    override operator fun plus(b: DoubleVector3D): DoubleVector3D
            = DoubleVector3D(x + b.x, y + b.y, z + b.z)

    override operator fun minus(b: DoubleVector3D): DoubleVector3D
            = DoubleVector3D(x - b.x, y - b.y, z - b.z)

    override operator fun times(k: Double): DoubleVector3D
            = DoubleVector3D(k * x, k * y, k * z)

    override operator fun div(k: Double): DoubleVector3D
            = DoubleVector3D(x / k, y / k, z / k)

    override operator fun unaryMinus(): DoubleVector3D
            = DoubleVector3D(-x, -y, -z)

    override fun inv()
            = DoubleVector3D(1.0/x, 1.0/y, 1.0/z)

    override fun product(b: DoubleVector3D): Double
            = x * b.x + y * b.y + z * b.z

    override fun crossProduct(b: DoubleVector3D): DoubleVector3D
            = DoubleVector3D(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x)

    override fun length(): Double
            = sqrt(x * x + y * y + z * z)

    override fun normalize(): DoubleVector3D
            = this * (1.0 / length())

    override fun angle(b: DoubleVector3D): Double
            = acos(product(b) /(this.length() * b.length()))

    override fun equals(b: DoubleVector3D): Boolean
            = x == b.x && y == b.y && z == b.z

    override fun nearEquals(b: DoubleVector3D, epsilon: Double): Boolean
            = nearZero(x-b.x,epsilon) && nearZero(y-b.y,epsilon) && nearZero(z-b.z,epsilon)

    override fun nearEqualsRel(b: DoubleVector3D, epsilon: Double): Boolean
            = nearEqualsRel(x,b.x,epsilon) && nearEqualsRel(y,b.y,epsilon) && nearEqualsRel(z,b.z,epsilon)
}

class BarycentricReference3D(val a: DoubleVector3D, val b: DoubleVector3D, val c: DoubleVector3D) {

    // Unnormalized triangle normal
    private val m = (b - a).crossProduct(c - a)
    // Absolute components for determining projection plane
    private val x = abs(m.x)
    private val y = abs(m.y)
    private val z = abs(m.z)

    private fun triArea2D(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): Double
            = (x1 - x2) * (y2 - y3) - (x2 - x3) * (y1 - y2)

    /**
     * Compute barycentric coordinate  for point p with respect to triangle (a, b, c)
     */
    fun coordinatesOf(p: DoubleVector3D): BarycentricCoordinates {
        // Nominators and one-over-denominator for u and v ratios
        val nu: Double
        val nv: Double
        val ood: Double

        // Compute areas in plane of largest projection
        if (x >= y && x >= z) {
            // x is largest, project to the yz plane
            nu = triArea2D(p.y, p.z, b.y, b.z, c.y, c.z)
            // Area of PBC in yz plane
            nv = triArea2D(p.y, p.z, c.y, c.z, a.y, a.z)
            // Area of PCA in yz plane
            ood = 1.0f / m.x
            // 1/(2*area of ABC in yz plane)
        } else if (y >= x && y >= z) {
            // y is largest, project to the xz plane
            nu = triArea2D(p.x, p.z, b.x, b.z, c.x, c.z)
            nv = triArea2D(p.x, p.z, c.x, c.z, a.x, a.z)
            ood = 1.0f / -m.y
        } else {
            // z is largest, project to the xy plane
            nu = triArea2D(p.x, p.y, b.x, b.y, c.x, c.y)
            nv = triArea2D(p.x, p.y, c.x, c.y, a.x, a.y)
            ood = 1.0f / m.z
        }
        val aC = nu * ood
        val bC = nv * ood
        val cC = 1.0f - aC - bC
        return BarycentricCoordinates(aC,bC,cC)
    }
}