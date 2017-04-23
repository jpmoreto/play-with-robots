package jpm.lib.maps

import jpm.lib.math.DoubleVector2D
import jpm.lib.math.IntVector2D
import jpm.lib.math.Vector2D
import org.junit.Test
import java.util.*

/**
 * Created by jm on 19/03/17.
 *
 */

class KDTreePerformanceTest {
    val dimMin = 4
    val occupiedGlobalThreshold = 0.8
    val initialDim = Math.round(Math.pow(2.0, 14.0).toFloat())
    val centerPointD = DoubleVector2D(0.0, 0.0)
    val centerPoint = IntVector2D(centerPointD)

    val iterations = 500000

    val deltaXMin = -3000
    val deltaXMax = -deltaXMin * 2 + 1

    val deltaYMin = -3000
    val deltaYMax = -deltaYMin * 2 + 1

    // intersection parameters Double
    val pd = DoubleVector2D(-3100.0,-100.0)
    val vd = DoubleVector2D(0.2,1.0)

    val tmax = 1000.0

    init {
        KDTreeAbs.setThreshold(occupiedGlobalThreshold)
        KDTree.dimMin = dimMin
        KDTreeD.dimMin = dimMin.toDouble()
    }

    fun <T: KDTreeAbs.Node<T, V, N>, V: Vector2D<V, N>, N: Number> visit(t: T) {
        // do nothing
    }

    @Test
    fun testVisitAll_KDTreeD() {

        val tree = KDTreeD.Node(centerPointD, KDTreeAbs.SplitAxis.XAxis, initialDim.toDouble())
        val rndX = Random(43)
        val rndY = Random(101)

        measure("setOccupied KDTreeD($initialDim), iterations = $iterations") {
            setOccupiedD(tree,rndX,rndY)
        }

        measure("visitAll KDTreeD($initialDim), iterations = $iterations") {
            visitAll(tree)
        }
    }

    @Test
    fun testVisitAll_KDTree() {

        val tree = KDTree.Node(centerPoint, KDTreeAbs.SplitAxis.XAxis, initialDim)
        val rndX = Random(43)
        val rndY = Random(101)

        measure("setOccupied KDTree($initialDim), iterations = $iterations") {
            setOccupied(tree,rndX,rndY)
        }

        measure("visitAll KDTree($initialDim), iterations = $iterations") {
            visitAll(tree)
        }
    }

    @Test
    fun testIntersectRay_KDTreeD() {

        val tree = KDTreeD.Node(centerPointD, KDTreeAbs.SplitAxis.XAxis, initialDim.toDouble())
        val rndX = Random(43)
        val rndY = Random(101)

        measure("setOccupied KDTreeD($initialDim), iterations = $iterations") {
            setOccupiedD(tree,rndX,rndY)
        }

        var min:DoubleVector2D? = DoubleVector2D(0.0,0.0)
        measure("intersectRay KDTreeD($initialDim), iterations = $iterations") {
            min = tree.intersectRayMinPoint(pd,vd,tmax,0.7)
        }
        println("min = $min")
    }

    fun <T: KDTreeAbs.Node<T, V, N>, V: Vector2D<V, N>, N: Number> visitAll(tree: T) {
        tree.visitAll { visit(it) }
    }

    fun <T : Tree<T,IntVector2D,Int>> setOccupied(tree: T, xRnd: Random, yRnd: Random) {
        for (i in 1..iterations) {
            val p = IntVector2D(deltaXMin + xRnd.nextInt(deltaXMax), deltaYMin + yRnd.nextInt(deltaYMax))
            tree.setOccupied(p)
        }
    }

    fun <T : Tree<T,DoubleVector2D,Double>> setOccupiedD(tree: Tree<T,DoubleVector2D,Double>, xRnd: Random, yRnd: Random) {
        for (i in 1..iterations) {
            val p = DoubleVector2D((deltaXMin + xRnd.nextInt(deltaXMax)).toDouble(), (deltaYMin + yRnd.nextInt(deltaYMax)).toDouble())
            tree.setOccupied(p)
        }
    }

    private fun measure(label: String, toMeasure: () -> Unit) {
        val t0 = System.currentTimeMillis()
        toMeasure()
        val t1 = System.currentTimeMillis()

        println("measure $label t = ${t1 - t0} ms")
    }
}