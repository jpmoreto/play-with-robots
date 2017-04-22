package jpm.lib

/**
 * Created by jm on 18/03/17.

 */

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet
import javafx.application.Application
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.stage.Stage
import jpm.lib.maps.*
import jpm.lib.math.*
import org.junit.Test
import java.util.*

class TestKDTreeDSpaceFree {
    @Test
    fun test() {
        KDTreeDSpaceFree().run(arrayOf<String>())
    }
}

class KDTreeDSpaceFree : TestKDTreeDBase, Application() {

    override val iterations = 1000

    override val deltaX = 100
    override val deltaY = 100

    override val rndX = 700
    override val rndY = 500

    override val dimMin = 4.0

    override val initialDim = Math.round(Math.pow(2.0, 10.0).toFloat())
    override val centerPointD = DoubleVector2D(500.0, 500.0)

    val occupiedThreshold = 0.7

    val minOccup = 0.49999999
    val maxOccup =0.50000001

    override fun start(primaryStage: Stage) {
        startDraw(primaryStage)
    }

    fun run(args: Array<String>) {
        launch(*args)
    }

    override fun drawShapesKDTreeD(gc: GraphicsContext) {
        KDTreeD.dimMin = dimMin
        val tree = KDTreeD.Node(centerPointD, KDTreeAbs.SplitAxis.XAxis, initialDim.toDouble())

        val rndX_a = Random(43)
        val rndY_a = Random(101)

        setOccupiedD(tree, rndX_a, rndY_a)
        setOccupiedDBound(tree)

        setGcColor(gc, Color.RED, Color.RED, 1.0)
        drawKDTreeD(gc,tree,occupiedThreshold)

        val blue = Color(Color.BLUE.red, Color.BLUE.green, Color.BLUE.blue, 0.7)
        setGcColor(gc,blue,blue,1.0)

        val result = ObjectAVLTreeSet<KDTreeD.Node>(KDTreeD.CompareNodesX)

        val t0 = System.currentTimeMillis()
        tree.visitAll { t ->
            if(t.isLeaf) {
                val occup = t.occupied()
                if(minOccup <= occup && occup <= maxOccup) {
                    if(deltaX <= t.left() && t.right() <= deltaX + rndX && deltaY <= t.bottom() && t.top() <= deltaY + rndY)
                        result.add(t)
                }
            }
        }
        val t1 = System.currentTimeMillis()

        val toRectangles = KDTreeD.fromNodeToRectangle(result)

        val t2 = System.currentTimeMillis()

        val compactRectangles = compactRectangles(toRectangles)

        val t3 = System.currentTimeMillis()

        compactRectangles.forEach { r ->
            gc.strokeRect(r.p1.x, r.p1.y, r.width(), r.height())
            println("visit($r)")
        }

        println("time to build initial free rectangle set = ${t1-t0} ms")
        println("time to from node to rectangles          = ${t2-t1} ms")
        println("time to compact rectangle set            = ${t3-t2} ms")
    }
}