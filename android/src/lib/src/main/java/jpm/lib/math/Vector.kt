package jpm.lib.math

interface Vector<T: Vector<T,N>,N:Number>: Comparable<T> {

    infix fun strictGt(b: T): Boolean

    operator fun plus(b: T): T
    operator fun minus(b: T): T
    operator fun times(k: N): T
    operator fun div(k: N): T
    operator fun unaryMinus(): T

    /**
     * get(0) == x
     * get(1) == y
     * get(2) == z
     * ...
     */
    operator fun get(pos: Int): N

    infix fun product(b: T): N
    fun inv(): T
    fun length(): N
    fun normalize(): T

    fun angle(b: T): Double

    fun equals(b: T): Boolean
    fun nearEquals(b: T, epsilon: N): Boolean
    fun nearEqualsRel(b: T, epsilon: N): Boolean

}
