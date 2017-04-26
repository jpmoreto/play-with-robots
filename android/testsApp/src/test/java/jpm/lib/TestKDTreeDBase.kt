package jpm.lib

/**
 * Created by jm on 18/03/17.

 */

import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

import javafx.stage.Stage
import jpm.lib.maps.*
import jpm.lib.math.*
import java.util.*

interface TestKDTreeDBase {

    val iterations:Int

    val deltaX:Int
    val deltaY:Int

    val rndX:Int
    val rndY:Int

    val dimMin: Double

    val initialDim: Int
    val centerPointD: DoubleVector2D

    fun startDraw(primaryStage: Stage) {
        primaryStage.title = "Drawing Operations Test"
        val root = Group()
        val canvas = Canvas(1350.0, 702.0)
        val gc = canvas.graphicsContext2D
        root.children.add(canvas)
        primaryStage.scene = Scene(root)
        primaryStage.show()
        drawShapes(gc)
    }

    private fun drawShapes(gc: GraphicsContext) {
        object : Thread() {
            override fun run() {
                Platform.runLater({ drawShapesKDTreeD(gc) })
            }
        }.start()
    }

    fun drawShapesKDTreeD(gc: GraphicsContext)

    fun setGcColor(gc: GraphicsContext, fill: Color, stroke: Color, lineWidth: Double) {
        gc.fill = fill
        gc.stroke = stroke
        gc.lineWidth = lineWidth

    }

    fun drawKDTreeD(gc: GraphicsContext, tree: KDTreeD.Node, threshold: Double) {
        tree.visitAll { t ->
            if (t.isLeaf && t.occupied() > threshold) {
                val dim = t.getDimVector()
                gc.fillRect(t.center.x - dim.x, t.center.y - dim.y, 2 * dim.x, 2 * dim.y)
            }
        }
    }

    fun <T : Tree<T, DoubleVector2D, Double>> setOccupiedD(tree: Tree<T, DoubleVector2D, Double>, xRnd: Random, yRnd: Random) {
        for (i in 1..iterations) {
            val p = DoubleVector2D((deltaX + xRnd.nextInt(rndX)).toDouble(), (deltaY + yRnd.nextInt(rndY)).toDouble())
            tree.setOccupied(p)
        }
    }

    fun <T : Tree<T, DoubleVector2D, Double>> setOccupiedDBound(tree: Tree<T, DoubleVector2D, Double>) {
        var x = deltaX
        while(x <= deltaX + rndX) {
            val ptop = DoubleVector2D(x.toDouble(), deltaY.toDouble())
            tree.setOccupied(ptop)
            val pbot = DoubleVector2D(x.toDouble(), deltaY.toDouble() + rndY)
            tree.setOccupied(pbot)
            x += 1
        }
        var y = deltaX
        while(y <= deltaY + rndY) {
            val ptop = DoubleVector2D(deltaX.toDouble(), y.toDouble())
            tree.setOccupied(ptop)
            val pbot = DoubleVector2D(deltaX.toDouble() + rndX, y.toDouble())
            tree.setOccupied(pbot)
            y += 1
        }
    }
}