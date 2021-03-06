package jpm.lib

/**
 * Created by jm on 18/03/17.

 */

import javafx.application.Application
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.stage.Stage
import jpm.lib.maps.*
import jpm.lib.math.*
import org.junit.Test
import java.util.*

class TestKDTreeDRayTracing {
    @Test
    fun test() {
        try {
            KDTreeDRayTracing().run(arrayOf<String>())
        } catch (e: Throwable) {
            // ignore
        }
    }
}

class KDTreeDRayTracing : TestKDTreeDBase, Application() {

    override val iterations = 1000

    override val deltaX = 100
    override val deltaY = 100

    override val rndX = 700
    override val rndY = 500

    override val dimMin = 4.0

    override val initialDim = Math.round(Math.pow(2.0, 10.0).toFloat())
    override val centerPointD = DoubleVector2D(500.0, 500.0)

    val occupiedThreshold = 0.7

    // intersection parameters Double

    //val pd = DoubleVector2D(120.0,120.0)
    //val vd = DoubleVector2D(1.0,1.0)

    val pd = DoubleVector2D(140.0, 138.0)
    val vd = DoubleVector2D(2.5, 1.5)
    val tmax = 400.0

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

        setGcColor(gc, Color.GREEN, Color.GREEN, 1.0)

        val endPoint = pd + vd * tmax
        gc.strokeLine(pd.x, pd.y,endPoint.x, endPoint.y)

        val blue = Color(Color.BLUE.red, Color.BLUE.green, Color.BLUE.blue, 0.7)
        setGcColor(gc,blue,blue,1.0)

        val trayEnd = pd + vd * tmax
        var nearPointFromPd = pd + vd * tmax

        val dim = 2.0
        val f = 3.0

        println("firstPoint = $pd, lastPoint = $nearPointFromPd")

        var countIterations = 0
        fun visit(tree: KDTreeD.Node, p: DoubleVector2D, v: DoubleVector2D, t: Double): Boolean {

            if (tree.isLeaf && tree.occupied() > occupiedThreshold) {

                countIterations += 1

                val intersectionPoint = tree.intersectionPoint(p,trayEnd)

                if((pd - intersectionPoint!!).length() < (pd - nearPointFromPd).length()) {
                    nearPointFromPd = intersectionPoint
                }
                //println("visit(${tree.toStringNode()},$p,$v,$t) nearPointFromPd = $nearPointFromPd")

                val center = intersectionPoint
                //gc.fillRect(tree.center.x - tree.getDimVector().x, tree.center.y - tree.getDimVector().y, tree.getDimVector().x * 2, tree.getDimVector().y * 2)
                //gc.strokeRect(tree.center.x - tree.getDimVector().x - 1, tree.center.y - tree.getDimVector().y - 1 , 2 * tree.getDimVector().x + 2, 2 * tree.getDimVector().y + 2)
                //gc.strokeRect(tree.center.x - 2 * tree.getDimVector().x, tree.center.y - 2 * tree.getDimVector().y, 4 * tree.getDimVector().x, 4 * tree.getDimVector().y)

                gc.strokeRect(center.x - dim / 2.0 - 1 * f, center.y - dim / 2.0 - 1 * f, dim + 2 * f, dim + 2 * f)
            }

            return true
        }

        tree.intersectRay(pd, vd, tmax, ::visit)
        val near = tree.intersectRayMinPoint(pd, vd, tmax, occupiedThreshold)
        println("nearPointFromPd = $nearPointFromPd, countIterations = $countIterations")
        println("near = $near")
        //gc.strokeOval(nearPointFromPd.x - dim / 2.0 - 3 * f, nearPointFromPd.y  - dim / 2.0 - 3 * f, dim + 6 * f, dim + 6 * f)
        if(near != null) {
            gc.strokeOval(near.x - dim / 2.0 - 3 * f, near.y - dim / 2.0 - 3 * f, dim + 6 * f, dim + 6 * f)
        }
    }
}