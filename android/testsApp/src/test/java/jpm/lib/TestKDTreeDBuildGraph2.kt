package jpm.lib

/**
 * Created by jm on 18/03/17.

 */

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet
import javafx.application.Application
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.stage.Stage
import jpm.lib.graph.algorithms.AStarAlgorithm
import jpm.lib.graph.graphbuilder.GraphBuilder
import jpm.lib.graph.graphs.WeightedSpaceGraph
import jpm.lib.maps.*

import jpm.lib.math.*
import org.junit.Test
import java.util.*

class TestKDTreeDBuildGraph2 {
    @Test
    fun test() {
        try {
            KDTreeDBuildGraph2().run(arrayOf<String>())
        } catch (e: Throwable) {
            // ignore
        }
    }
}

class KDTreeDBuildGraph2 : TestKDTreeDBase, Application() {

    override val iterations = 2500

    override val deltaX = 10
    override val deltaY = 10

    override val rndX = 950
    override val rndY = 650

    val startPoint = DoubleVector2D(120.0,140.0)
    val endPoint = DoubleVector2D(780.0,480.0)

    val startPointMin = DoubleVector2D(deltaX + 2.0,deltaY + 2.0)
    val startPointMax = DoubleVector2D(deltaX + 50.0,deltaY + rndY - 2.0)

    val endPointMin = DoubleVector2D(deltaX + rndX - 50.0,deltaY + 2.0)
    val endPointMax = DoubleVector2D(deltaX + rndX - 2.0,deltaY + rndY - 2.0)

    //override val dimMin = 4.0
    override val dimMin = 2.0

    override val initialDim = Math.round(Math.pow(2.0, 10.0).toFloat())
    override val centerPointD = DoubleVector2D(500.0, 500.0)

    val occupiedThreshold = 0.7

    val minOccup = 0.0
    val maxOccup =0.50000001

    val robotCircleRadius = 0.07                                        // BW / 2
    val bodyWidth = 2 * robotCircleRadius                               // BW
    val wheelBodyDistance = 0.005                                       //
    val wheelWidth = 0.026                                              // Ww
    val robotTotalWidth = (bodyWidth + 2 * (wheelBodyDistance + wheelWidth)) * 100 / 2.0

    val halfRobotTotalWidth = robotTotalWidth / 2.0

    override fun start(primaryStage: Stage) {
        startDraw(primaryStage)
    }

    fun run(args: Array<String>) {
        launch(*args)
    }

    override fun drawShapesKDTreeD(gc: GraphicsContext) {
       for(i in 0..0)  {
           gc.clearRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)
           drawShapesKDTreeD1(gc)
       }
    }

    fun drawShapesKDTreeD1(gc: GraphicsContext) {
        println("robotTotalWidth = $robotTotalWidth")
        KDTreeD.dimMin = dimMin
        val tree = KDTreeD.Node(centerPointD, KDTreeAbs.SplitAxis.XAxis, initialDim.toDouble())

        val rndX_a = Random(43)
        val rndY_a = Random(101)

        setOccupiedD(tree, rndX_a, rndY_a)
        setGcColor(gc, Color.RED, Color.RED, 1.0)
        drawKDTreeD(gc,tree,occupiedThreshold)

        val treeWithRobotWith = KDTreeD.Node(centerPointD, KDTreeAbs.SplitAxis.XAxis, initialDim.toDouble())

        val t_1 = System.currentTimeMillis()

        tree.visitAll { t ->
            if (t.isLeaf && t.occupied() > occupiedThreshold) {

                val dim = t.getDimVector()
                treeWithRobotWith.setOccupied(
                        t.center.x - dim.x - halfRobotTotalWidth,
                        t.center.y - dim.y - halfRobotTotalWidth,
                        t.center.x + dim.x + halfRobotTotalWidth,
                        t.center.y + dim.y + halfRobotTotalWidth)
            }
        }

        setOccupiedDBound(treeWithRobotWith)

        val red = Color(Color.ROSYBROWN.red, Color.ROSYBROWN.green, Color.ROSYBROWN.blue, 0.5)
        setGcColor(gc,red,red,1.0)
        drawKDTreeD(gc,treeWithRobotWith,occupiedThreshold)

        val blue = Color(Color.BLUE.red, Color.BLUE.green, Color.BLUE.blue, 0.7)
        setGcColor(gc,blue,blue,1.0)

        val result = ObjectAVLTreeSet<KDTreeD.Node>(KDTreeD.CompareNodesX)

        val deltaX_plus_rndX = deltaX + rndX
        val deltaY_plus_rndY = deltaY + rndY

        val t0 = System.currentTimeMillis()
        treeWithRobotWith.visitAll { t ->
            if(t.isLeaf) {
                val occup = t.occupied()
                if(minOccup <= occup && occup <= maxOccup) {
                    val x = t.center.x
                    val y = t.center.y
                    val halfXDim = t.xDim / 2.0
                    val halfYDim = t.yDim / 2.0

                    if(deltaX <= x - halfXDim && x + halfXDim <= deltaX_plus_rndX && deltaY <= y - halfYDim && y + halfYDim <= deltaY_plus_rndY)
                        result.add(t)
                }
            }
        }
        val t1 = System.currentTimeMillis()

        val toRectangles = KDTreeD.fromNodeToRectangle(result)

        val t2 = System.currentTimeMillis()

        val compactRectangles = compactRectangles(toRectangles, 1000,10)

        val t3 = System.currentTimeMillis()

        compactRectangles.forEach { r ->
            gc.strokeRect(r.p1.x, r.p1.y, r.width(), r.height())
            //println("visit($r)")
        }
        val t4 = System.currentTimeMillis()

        val startPoint = calculateStartPoint(treeWithRobotWith)
        val endPoint = calculateEndPoint(treeWithRobotWith)

        val start = Color(Color.BLACK.red, Color.BLACK.green, Color.BLACK.blue, 0.5)
        setGcColor(gc,start,start,1.0)
        gc.fillOval(startPoint.x - 5, startPoint.y - 5, 10.0, 10.0)

        val end = Color(Color.OLIVEDRAB.red, Color.OLIVEDRAB.green, Color.OLIVEDRAB.blue, 0.5)
        setGcColor(gc,end,end,1.0)
        gc.fillOval(endPoint.x - 5, endPoint.y - 5, 10.0, 10.0)

        val graph = GraphBuilder.build(startPoint, endPoint, compactRectangles)
        val t5 = System.currentTimeMillis()
        val alg1 = AStarAlgorithm<WeightedSpaceGraph.Graph, WeightedSpaceGraph.Node, WeightedSpaceGraph.Arc>()
        alg1.apply(graph,graph.startNode!!, graph.endNode!!)
        val t6 = System.currentTimeMillis()

        var node = graph.endNode
        val startNode = graph.startNode

        if(node != null && startNode != null) {
            setGcColor(gc, Color.BLACK, Color.BLACK, 1.0)

            gc.beginPath()
            gc.moveTo(node.middlePoint.x, node.middlePoint.y)

            node = node.from()
            if(node != null) {
                while (node != null && node != startNode) {
                    gc.lineTo(node.middlePoint.x, node.middlePoint.y)
                    node = node.from()
                }
                gc.lineTo(startNode.middlePoint.x, startNode.middlePoint.y)
            }
            gc.stroke()
        }
        println("graph nodes = ${graph.nodes.size} - graph arcs = ${graph.nodes.flatMap { it.value.arcs }.toSet().size}")

        println("time to build tree with robot            = ${t0-t_1} ms")
        println("time to build initial free rectangle set = ${t1-t0} ms")
        println("time to from node to rectangles          = ${t2-t1} ms")
        println("time to compact rectangle set            = ${t3-t2} ms")
        println("time to build graph                      = ${t5-t4} ms")
        println("time to AStarAlgorithm                  = ${t6-t5} ms")
    }

    private fun calculateEndPoint(tree: KDTreeD.Node): DoubleVector2D {
        val rndX = Random()
        val rndY = Random()

        for(i in 0..2000) {
            val endPoint = DoubleVector2D(
                    endPointMin.x + rndX.nextInt(Math.round((endPointMax.x - endPointMin.x).toFloat())),
                    endPointMin.y + rndY.nextInt(Math.round((endPointMax.y - endPointMin.y).toFloat())))
            if(tree.free(endPoint) >= 0.5) return endPoint
        }
        return this.endPoint
    }

    private fun calculateStartPoint(tree: KDTreeD.Node): DoubleVector2D {
        val rndX = Random()
        val rndY = Random()

        for(i in 0..2000) {
            val startPoint = DoubleVector2D(
                    startPointMin.x + rndX.nextInt(Math.round((startPointMax.x - startPointMin.x).toFloat())),
                    startPointMin.y + rndY.nextInt(Math.round((startPointMax.y - startPointMin.y).toFloat())))
            if(tree.free(startPoint) >= 0.5) return startPoint
        }
        return this.startPoint
    }
}