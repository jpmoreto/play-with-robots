package jpm.lib.maps

import jpm.lib.math.DoubleVector2D
import jpm.lib.math.IntVector2D
import org.junit.Test
import java.util.*

/**
 * Created by jm on 13/03/17.
 *
 */

class QuadTreeTest {

    val dimMin = 4
    val occupiedThreshold = 0.7
    val occupiedGlobalThreshold = 0.8
    val initialDim = Math.round(Math.pow(2.0, 14.0).toFloat())
    val centerPoint = IntVector2D(0, 0)

    val iterations = 10000

    val deltaX = -2000
    val deltaY = -deltaX + 1

    // intersection parameters Double
    val pd = DoubleVector2D(-2010.0,2010.0)
    val vd = DoubleVector2D(400.0,400.0)

    // intersection parameters Double
    val pi = IntVector2D(-2100,2100)
    val vi = IntVector2D(400,400)

    val tmax = 1000.0

    @Test
    fun kdTreeTestIntersection() {
        KDTreeAbs.setThreshold(occupiedGlobalThreshold)
        KDTree.dimMin = dimMin
        KDTreeD.dimMin = dimMin.toDouble()

        testIntersection(KDTree.Node(centerPoint, KDTreeAbs.SplitAxis.XAxis, initialDim))
        testIntersectionD(KDTreeD.Node(DoubleVector2D(0.0,0.0),  KDTreeAbs.SplitAxis.XAxis, initialDim.toDouble()))
    }

    @Test
    fun kdTreeTest1() {
        KDTreeAbs.setThreshold(occupiedGlobalThreshold)
        KDTree.dimMin = dimMin

        test1(KDTree.Node(centerPoint, KDTreeAbs.SplitAxis.XAxis, initialDim))
    }

    @Test
    fun kdTreeTest2() {
        KDTreeAbs.setThreshold(occupiedGlobalThreshold)
        KDTree.dimMin = dimMin

        test2(KDTree.Node(centerPoint, KDTreeAbs.SplitAxis.XAxis, initialDim))
    }

    @Test
    fun quadTreeTestSize() {
        KDTreeAbs.setThreshold(occupiedGlobalThreshold)
        KDTree.dimMin = dimMin

        test3D("KDTreeD:", KDTreeD.Node(DoubleVector2D(0.0,0.0),  KDTreeAbs.SplitAxis.XAxis, initialDim.toDouble()))

        test3("KDTree:",KDTree.Node(centerPoint, KDTreeAbs.SplitAxis.XAxis, initialDim))
    }

    @Test
    fun quadTreeTestFix() {
        KDTreeAbs.setThreshold(occupiedGlobalThreshold)
        KDTree.dimMin = dimMin

        val kdtree = KDTree.Node(centerPoint, KDTreeAbs.SplitAxis.XAxis, initialDim)

        val points1 = listOf(
                IntVector2D(x = -50, y = -98),
                IntVector2D(x = -78, y = -18),
                IntVector2D(x = -110, y = -66),
                IntVector2D(x = -30, y = -202),
                IntVector2D(x = -178, y = -30),
                IntVector2D(x = -166, y = -118),
                IntVector2D(x = -150, y = -250),
                IntVector2D(x = -242, y = -238),
                IntVector2D(x = -34, y = -262),
                IntVector2D(x = -50, y = -298),
                IntVector2D(x = -18, y = -338),
                IntVector2D(x = -114, y = -378),
                IntVector2D(x = -154, y = -282),
                IntVector2D(x = -190, y = -294),
                IntVector2D(x = -382, y = -30),
                IntVector2D(x = -374, y = -54),
                IntVector2D(x = -302, y = -174),
                IntVector2D(x = -258, y = -226),
                IntVector2D(x = -274, y = -266),
                IntVector2D(x = -318, y = -386)
        )

        for (p in points1) {
            kdtree.setOccupied(p)
        }
        //println("QuadTree: ${qtree.toStringLeaf()}")
        //println("KDTree: ${kdtree.toStringLeaf()}")
    }

    private fun <T : Tree<T,IntVector2D,Int>> test3(label: String, tree: Tree<T,IntVector2D,Int>) {

        val rndX_a = Random(43)
        val rndY_a = Random(101)

        setOccupied("", tree, rndX_a, rndY_a)

        val rndDX1_a = Random(43)
        val rndDY1_a = Random(101)

        countOccupied("same random", tree, rndDX1_a, rndDY1_a)

        val rndDX_a = Random(101)
        val rndDY_a = Random(1013451)

        //val set_a_1 = countOccupied("different random", tree, rndDX_a, rndDY_a)
        setFree("", tree, rndX_a, rndY_a)
        //val set_a_2 = countOccupied("different random after free", tree, rndDX_a, rndDY_a)
        //println("$label ${tree.toStringLeaf()}")
    }

    private fun <T : Tree<T,DoubleVector2D,Double>> test3D(label: String,tree: Tree<T,DoubleVector2D,Double>) {

        val rndX_a = Random(43)
        val rndY_a = Random(101)

        setOccupiedD("", tree, rndX_a, rndY_a)

        val rndDX1_a = Random(43)
        val rndDY1_a = Random(101)

        countOccupiedD("same random", tree, rndDX1_a, rndDY1_a)

        val rndDX_a = Random(101)
        val rndDY_a = Random(1013451)

        val set_a_1 = countOccupiedD("different random", tree, rndDX_a, rndDY_a)
        setFreeD("", tree, rndX_a, rndY_a)
        val set_a_2 = countOccupiedD("different random after free", tree, rndDX_a, rndDY_a)
        //println("$label ${tree.toStringLeaf()}")
    }

    private fun compare(set_a_1: MutableSet<IntVector2D>, set_b_1: MutableSet<IntVector2D>) {
        val a_minus_b = mutableSetOf<IntVector2D>()
        a_minus_b.addAll(set_a_1)
        a_minus_b.removeAll(set_b_1)
        println("(a - b) size = ${a_minus_b.size}: $a_minus_b")

        val b_minus_a = mutableSetOf<IntVector2D>()
        b_minus_a.addAll(set_b_1)
        b_minus_a.removeAll(set_a_1)

        println("(b - a) size = ${b_minus_a.size} : $b_minus_a")
    }

    fun <T : Tree<T,DoubleVector2D,Double>> testIntersectionD(tree: Tree<T,DoubleVector2D,Double>) {
        val rndX = Random(43)
        val rndY = Random(101)

        setOccupiedD("", tree, rndX, rndY)

        fun visit(tree: Tree<T,DoubleVector2D,Double>, p: DoubleVector2D, v: DoubleVector2D, t: Double): Boolean {

            println("visit(${tree.toStringNode()},$p,$v,$t)")
            return true
        }

        tree.intersectRay(pd,vd,tmax,::visit)
    }

    fun <T : Tree<T,IntVector2D,Int>> testIntersection(tree: Tree<T,IntVector2D,Int>) {
        val rndX = Random(43)
        val rndY = Random(101)

        setOccupied("", tree, rndX, rndY)

        fun visit(tree: Tree<T,IntVector2D,Int>, p: DoubleVector2D, v: DoubleVector2D, t: Double): Boolean {

            println("visit(${tree.toStringNode()},$p,$v,$t)")
            return true
        }

        tree.intersectRay(pd,vd,tmax,::visit)
    }

    fun <T : Tree<T,IntVector2D,Int>> test1(tree: Tree<T,IntVector2D,Int>) {

        //rootNode.setOccupied(IntVector2D(-100,100))
        tree.setOccupied(IntVector2D(-99, 99))
        tree.setOccupied(IntVector2D(-99, 101))
        tree.setOccupied(IntVector2D(-101, 99))
        tree.setOccupied(IntVector2D(-101, 101))

        tree.setOccupied(IntVector2D(100, 100))
        tree.setOccupied(IntVector2D(-100, -100))
        tree.setOccupied(IntVector2D(100, -100))

        tree.setFree(IntVector2D(150, 100))

        val occupied = tree.occupied(IntVector2D(50, 50))
        println("tree.occupied(IntVector2D(50,50)) = $occupied")
        val free = tree.free(IntVector2D(50, 50))
        println("tree.free(IntVector2D(50,50)) = $free")

        val occupied1 = tree.occupied(IntVector2D(-100, 100))
        println("tree.occupied(IntVector2D(-100,100)) = $occupied1")
        val free1 = tree.free(IntVector2D(-100, 100))
        println("tree.free(IntVector2D(-100,100)) = $free1")

        val occupied2 = tree.occupied(IntVector2D(150, 100))
        println("tree.occupied(IntVector2D(150,100)) = $occupied2")
        val free2 = tree.free(IntVector2D(150, 100))
        println("tree.free(IntVector2D(150,100)) = $free2")

        println("size = ${tree.size()}\n$tree")

        tree.setFree(IntVector2D(-101, 99))
        println("--------------------------------------------------------------------")
        println("size = ${tree.size()}\n$tree")
    }

    fun <T : Tree<T,IntVector2D,Int>> test2(tree: Tree<T,IntVector2D,Int>) {

        val rndX = Random(43)
        val rndY = Random(101)

        setOccupied("", tree, rndX, rndY)

        val rndDX1 = Random(43)
        val rndDY1 = Random(101)

        countOccupied("same random", tree, rndDX1, rndDY1)

        val rndDX = Random(101)
        val rndDY = Random(1013451)

        countOccupied("different random", tree, rndDX, rndDY)
        setFree("", tree, rndX, rndY)
        countOccupied("different random after free", tree, rndDX, rndDY)
    }

    fun <T : Tree<T,IntVector2D,Int>> setOccupied(label: String, tree: Tree<T,IntVector2D,Int>, xRnd: Random, yRnd: Random) {
        val t0 = System.currentTimeMillis()
        //println("setOccupied:")
        for (i in 1..iterations) {
            val p = IntVector2D(deltaX + xRnd.nextInt(deltaY), deltaX + yRnd.nextInt(deltaY))
            tree.setOccupied(p)
            //println("$p,")
        }
        val t1 = System.currentTimeMillis()
        println("  setOccupied size = ${tree.size()}\t t = ${t1 - t0} ms\t $label")
    }

    fun <T : Tree<T,DoubleVector2D,Double>> setOccupiedD(label: String, tree: Tree<T,DoubleVector2D,Double>, xRnd: Random, yRnd: Random) {
        val t0 = System.currentTimeMillis()
        //println("setOccupied:")
        for (i in 1..iterations) {
            val p = DoubleVector2D((deltaX + xRnd.nextInt(deltaY)).toDouble(), (deltaX + yRnd.nextInt(deltaY)).toDouble())
            tree.setOccupied(p)
            //println("$p,")
        }
        val t1 = System.currentTimeMillis()
        println("  setOccupied size = ${tree.size()}\t t = ${t1 - t0} ms\t $label")
    }

    fun <T : Tree<T,IntVector2D,Int>> setFree(label: String, tree: Tree<T,IntVector2D,Int>, xRnd: Random, yRnd: Random) {
        val t0 = System.currentTimeMillis()
        for (i in 1..iterations) {
            tree.setFree(IntVector2D(deltaX + xRnd.nextInt(deltaY), deltaX + yRnd.nextInt(deltaY)))
        }
        val t1 = System.currentTimeMillis()
        println("      setFree size = ${tree.size()}\t t = ${t1 - t0} ms\t $label")
    }

    fun <T : Tree<T,DoubleVector2D,Double>> setFreeD(label: String, tree: Tree<T,DoubleVector2D,Double>, xRnd: Random, yRnd: Random) {
        val t0 = System.currentTimeMillis()
        for (i in 1..iterations) {
            tree.setFree(DoubleVector2D((deltaX + xRnd.nextInt(deltaY)).toDouble(), (deltaX + yRnd.nextInt(deltaY)).toDouble()))
        }
        val t1 = System.currentTimeMillis()
        println("      setFree size = ${tree.size()}\t t = ${t1 - t0} ms\t $label")
    }

    fun <T : Tree<T,IntVector2D,Int>> countOccupied(label: String, tree: Tree<T,IntVector2D,Int>, xRnd: Random, yRnd: Random): MutableSet<IntVector2D> {
        val t0 = System.currentTimeMillis()
        val occupiedSet = mutableSetOf<IntVector2D>()
        var sum = 0
        for (i in 1..iterations) {
            val p = IntVector2D(deltaX + xRnd.nextInt(deltaY), deltaX + yRnd.nextInt(deltaY))
            val occ = tree.occupied(p)
            if (occ > occupiedThreshold) {
                occupiedSet.add(p)
                ++sum
            }
        }
        val t1 = System.currentTimeMillis()
        println("countOccupied size = ${tree.size()}\t t = ${t1 - t0} ms\t sum  = $sum\t $label")
        return occupiedSet
    }

    fun <T : Tree<T,DoubleVector2D,Double>> countOccupiedD(label: String, tree: Tree<T,DoubleVector2D,Double>, xRnd: Random, yRnd: Random): MutableSet<DoubleVector2D> {
        val t0 = System.currentTimeMillis()
        val occupiedSet = mutableSetOf<DoubleVector2D>()
        var sum = 0
        for (i in 1..iterations) {
            val p = DoubleVector2D((deltaX + xRnd.nextInt(deltaY)).toDouble(), (deltaX + yRnd.nextInt(deltaY)).toDouble())
            val occ = tree.occupied(p)
            if (occ > occupiedThreshold) {
                occupiedSet.add(p)
                ++sum
            }
        }
        val t1 = System.currentTimeMillis()
        println("countOccupied size = ${tree.size()}\t t = ${t1 - t0} ms\t sum  = $sum\t $label")
        return occupiedSet
    }
}

/*

QuadTree:
Node(IntVector2D(x=-382, y=-34),4,0,1,true, 0.0)
Node(IntVector2D(x=-374, y=-58),4,1,0,true, 1.0)
Node(IntVector2D(x=-302, y=-174),4,1,0,true, 1.0)
Node(IntVector2D(x=-258, y=-230),4,1,0,true, 1.0)
Node(IntVector2D(x=-178, y=-30),4,1,0,true, 1.0)
Node(IntVector2D(x=-166, y=-118),4,0,1,true, 0.0)
Node(IntVector2D(x=-78, y=-18),4,0,1,true, 0.0)
Node(IntVector2D(x=-110, y=-70),4,0,1,true, 0.0)
Node(IntVector2D(x=-50, y=-98),4,1,0,true, 1.0)
Node(IntVector2D(x=-242, y=-238),4,0,1,true, 0.0)
Node(IntVector2D(x=-150, y=-250),4,0,1,true, 0.0)
Node(IntVector2D(x=-30, y=-206),4,0,1,true, 0.0)
Node(IntVector2D(x=-274, y=-266),4,0,1,true, 0.0)
Node(IntVector2D(x=-318, y=-386),4,1,0,true, 1.0)
Node(IntVector2D(x=-154, y=-286),4,1,0,true, 1.0)
Node(IntVector2D(x=-190, y=-298),4,0,1,true, 0.0)
Node(IntVector2D(x=-34, y=-262),4,1,0,true, 1.0)
Node(IntVector2D(x=-50, y=-302),4,1,0,true, 1.0)
Node(IntVector2D(x=-114, y=-378),4,0,1,true, 0.0)
Node(IntVector2D(x=-18, y=-342),4,1,0,true, 1.0)


KDTree:
Node(IntVector2D(x=-50, y=-98),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-78, y=-18),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-110, y=-66),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-30, y=-202),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-178, y=-30),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-166, y=-118),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-150, y=-250),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-242, y=-238),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-34, y=-262),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-50, y=-298),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-18, y=-338),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-114, y=-378),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-154, y=-282),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-190, y=-294),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-382, y=-30),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-374, y=-54),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-302, y=-174),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-258, y=-226),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-274, y=-266),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-318, y=-386),XAxis,4,4,1,0,true, 1.0)




QuadTree:
Node(IntVector2D(x=-110, y=-70),4,0,1,true, 0.0)
Node(IntVector2D(x=-114, y=-378),4,0,1,true, 0.0)
Node(IntVector2D(x=-150, y=-250),4,0,1,true, 0.0)
Node(IntVector2D(x=-154, y=-286),4,1,0,true, 1.0)
Node(IntVector2D(x=-166, y=-118),4,0,1,true, 0.0)
Node(IntVector2D(x=-178, y=-30),4,1,0,true, 1.0)
Node(IntVector2D(x=-18, y=-342),4,1,0,true, 1.0)
Node(IntVector2D(x=-190, y=-298),4,0,1,true, 0.0)
Node(IntVector2D(x=-242, y=-238),4,0,1,true, 0.0)
Node(IntVector2D(x=-258, y=-230),4,1,0,true, 1.0)
Node(IntVector2D(x=-274, y=-266),4,0,1,true, 0.0)
Node(IntVector2D(x=-302, y=-174),4,1,0,true, 1.0)
Node(IntVector2D(x=-30, y=-206),4,0,1,true, 0.0)
Node(IntVector2D(x=-318, y=-386),4,1,0,true, 1.0)
Node(IntVector2D(x=-34, y=-262),4,1,0,true, 1.0)
Node(IntVector2D(x=-374, y=-58),4,1,0,true, 1.0)
Node(IntVector2D(x=-382, y=-34),4,0,1,true, 0.0)
Node(IntVector2D(x=-50, y=-302),4,1,0,true, 1.0)
Node(IntVector2D(x=-50, y=-98),4,1,0,true, 1.0)
Node(IntVector2D(x=-78, y=-18),4,0,1,true, 0.0)

QuadTree:
Node(IntVector2D(x=-110, y=-70),4,1,0,true, 1.0)
Node(IntVector2D(x=-114, y=-378),4,1,0,true, 1.0)
Node(IntVector2D(x=-150, y=-250),4,1,0,true, 1.0)
Node(IntVector2D(x=-154, y=-286),4,1,0,true, 1.0)
Node(IntVector2D(x=-166, y=-118),4,1,0,true, 1.0)
Node(IntVector2D(x=-178, y=-30),4,1,0,true, 1.0)
Node(IntVector2D(x=-18, y=-342),4,1,0,true, 1.0)
Node(IntVector2D(x=-190, y=-298),4,1,0,true, 1.0)
Node(IntVector2D(x=-242, y=-238),4,1,0,true, 1.0)
Node(IntVector2D(x=-258, y=-230),4,1,0,true, 1.0)
Node(IntVector2D(x=-274, y=-266),4,1,0,true, 1.0)
Node(IntVector2D(x=-302, y=-174),4,1,0,true, 1.0)
Node(IntVector2D(x=-30, y=-206),4,1,0,true, 1.0)
Node(IntVector2D(x=-318, y=-386),4,1,0,true, 1.0)
Node(IntVector2D(x=-34, y=-262),4,1,0,true, 1.0)
Node(IntVector2D(x=-374, y=-58),4,1,0,true, 1.0)
Node(IntVector2D(x=-382, y=-34),4,1,0,true, 1.0)
Node(IntVector2D(x=-50, y=-302),4,1,0,true, 1.0)
Node(IntVector2D(x=-50, y=-98),4,1,0,true, 1.0)
Node(IntVector2D(x=-78, y=-18),4,1,0,true, 1.0)

KDTree:
Node(IntVector2D(x=-110, y=-66),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-114, y=-378),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-150, y=-250),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-154, y=-282),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-166, y=-118),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-178, y=-30),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-18, y=-338),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-190, y=-294),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-242, y=-238),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-258, y=-226),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-274, y=-266),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-302, y=-174),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-30, y=-202),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-318, y=-386),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-34, y=-262),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-374, y=-54),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-382, y=-30),XAxis,4,4,0,1,true, 0.0)
Node(IntVector2D(x=-50, y=-298),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-50, y=-98),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-78, y=-18),XAxis,4,4,0,1,true, 0.0)

KDTree:
Node(IntVector2D(x=-110, y=-70),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-114, y=-378),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-150, y=-250),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-154, y=-286),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-166, y=-118),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-178, y=-30),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-18, y=-342),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-190, y=-298),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-242, y=-238),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-258, y=-230),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-274, y=-266),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-302, y=-174),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-30, y=-206),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-318, y=-386),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-34, y=-262),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-374, y=-58),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-382, y=-34),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-50, y=-302),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-50, y=-98),XAxis,4,4,1,0,true, 1.0)
Node(IntVector2D(x=-78, y=-18),XAxis,4,4,1,0,true, 1.0)

*/