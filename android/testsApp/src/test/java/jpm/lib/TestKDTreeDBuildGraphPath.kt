package jpm.lib

/**
 * Created by jm on 18/03/17.

 */

import javafx.application.Application
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
//import javafx.scene.text.Font
import javafx.stage.Stage
import jpm.lib.graph.algorithms.AStarAlgorithm
import jpm.lib.graph.graphbuilder.GraphBuilder
import jpm.lib.graph.graphs.WeightedSpaceGraph
import jpm.lib.maps.*

import jpm.lib.math.*
import org.junit.Test
import java.util.*

class TestKDTreeDBuildGraphPath {
    @Test
    fun test() {
        try {
            KDTreeDBuildGraphPath().run(arrayOf<String>())
        } catch (e: Throwable) {
            // ignore
        }
    }
}

class KDTreeDBuildGraphPath : TestKDTreeDBase, Application() {

    //override val iterations = 2500
    //override val iterations = 2090
    //override val iterations = 2700
    override val iterations = 1500
    //override val iterations = 2

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

    override val dimMin = 2.0

    override val initialDim = Math.round(Math.pow(2.0, 10.0).toFloat())
    override val centerPointD = DoubleVector2D(500.0, 500.0)

    val occupiedThreshold = 0.7

    val minOccup = 0.0
    val maxOccup = 0.50000001

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
       for(i in 1..39)  {
           gc.clearRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)
           drawShapesKDTreeD1(gc)
       }
    }

    fun drawShapesKDTreeD1(gc: GraphicsContext) {
        println("robotTotalWidth = $robotTotalWidth")
        KDTreeD.dimMin = dimMin
        val tree = KDTreeD.Node(centerPointD, KDTreeAbs.SplitAxis.XAxis, initialDim.toDouble())

        //val rndX_a = Random(43)
        //val rndY_a = Random(101)

        val rndX_a = Random()
        val rndY_a = Random()

        setOccupiedD(tree, rndX_a, rndY_a)

        val t0 = System.currentTimeMillis()

        val treeWithRobotWith = tree.clone(halfRobotTotalWidth,occupiedThreshold)

        val t1_0 = System.currentTimeMillis()

        setOccupiedDBound(treeWithRobotWith)

        val t1_1 = System.currentTimeMillis()

        //val nodes = treeWithRobotWith.getNodes(minOccup,maxOccup,deltaX.toDouble(),deltaY.toDouble(),deltaX.toDouble() + rndX,deltaY.toDouble() + rndY)

        val t2 = System.currentTimeMillis()

        //val rectangles = KDTreeD.fromNodeToRectangle(nodes)
        val rectangles = treeWithRobotWith.getRectangles(minOccup,maxOccup,deltaX.toDouble(),deltaY.toDouble(),deltaX.toDouble() + rndX,deltaY.toDouble() + rndY)

        val t3 = System.currentTimeMillis()

        val compactRectangles = compactRectangles(rectangles, 0, 20)

        val t4 = System.currentTimeMillis()

        val startPoint = calculateStartPoint(treeWithRobotWith)
        val endPoint = calculateEndPoint(treeWithRobotWith)

        val t5 = System.currentTimeMillis()

        val graph = GraphBuilder.build(startPoint, endPoint, compactRectangles)

        val t6 = System.currentTimeMillis()

        val aStarAlgorithm = AStarAlgorithm<WeightedSpaceGraph.Graph, WeightedSpaceGraph.Node, WeightedSpaceGraph.Arc>()

        aStarAlgorithm.apply(graph,graph.startNode!!, graph.endNode!!)

        val t7 = System.currentTimeMillis()

        val path = GraphBuilder.getPath(graph)
        val pathOptimized = GraphBuilder.optimizePath(path, treeWithRobotWith, occupiedThreshold)

        val t8 = System.currentTimeMillis()

        // draw begin
        //
        setGcColor(gc, Color.RED, Color.RED, 1.0)
        drawKDTreeD(gc,tree,occupiedThreshold)

        val red = Color(Color.ROSYBROWN.red, Color.ROSYBROWN.green, Color.ROSYBROWN.blue, 0.5)
        setGcColor(gc,red,red,1.0)
        drawKDTreeD(gc,treeWithRobotWith,occupiedThreshold)

        val blue = Color(Color.BLUE.red, Color.BLUE.green, Color.BLUE.blue, 0.5)
        setGcColor(gc,blue,blue,0.3)

        /*
        gc.font = Font.font(10.0)
        val red1 = Color(Color.RED.red, Color.RED.green, Color.RED.blue, 0.7)
        var i = 0
        */
        compactRectangles.forEach { r ->
            /*
            if(i % 2 == 0)
                setGcColor(gc,blue,blue,1.0)
            else
                setGcColor(gc,red1,red1,1.0)

            gc.fillText(i.toString(),r.p1.x + r.width()/2,r.p1.y+r.height()/2)
            println("i=$i visit($r)")
            i += 1
            */
            gc.strokeRect(r.p1.x, r.p1.y, r.width(), r.height())
        }

        val startColor = Color(Color.BLACK.red, Color.BLACK.green, Color.BLACK.blue, 0.5)
        setGcColor(gc,startColor,startColor,1.0)
        gc.fillOval(startPoint.x - 5, startPoint.y - 5, 10.0, 10.0)

        val endColor = Color(Color.OLIVEDRAB.red, Color.OLIVEDRAB.green, Color.OLIVEDRAB.blue, 0.5)
        setGcColor(gc,endColor,endColor,1.0)
        gc.fillOval(endPoint.x - 5, endPoint.y - 5, 10.0, 10.0)

        ///*
        setGcColor(gc, Color.RED, Color.RED, 1.0)
        for(node in graph.nodes.values) {
            if(node.minCostArc_ != null) {
                val a = node.middlePoint
                val b = node.minCostArc_!!.startNode.middlePoint
                gc.strokeLine(a.x, a.y, b.x, b.y)
            }
        }
        //*/
        /*
        setGcColor(gc, Color.BLACK, Color.BLACK, 1.0)
        for(node in graph.nodes.values) {
            gc.fillOval(node.middlePoint.x - 0.5,node.middlePoint.y - 0.5,1.0,1.0)
        }

        for(arc in graph.startNode!!.arcs) {
            val a = graph.startNode!!.middlePoint
            val b = if(arc.startNode == graph.startNode) arc.endNode.middlePoint else arc.startNode.middlePoint
            gc.strokeLine(a.x,a.y,b.x,b.y)
        }

        for(arc in graph.endNode!!.arcs) {
            val a = graph.endNode!!.middlePoint
            val b = if(arc.endNode == graph.endNode) arc.startNode.middlePoint else arc.endNode.middlePoint
            gc.strokeLine(a.x,a.y,b.x,b.y)
        }
        */
        setGcColor(gc, Color.BLACK, Color.BLACK, 1.0)

        gc.beginPath()

        var isFirst = true
        for(node in pathOptimized) {
            if(isFirst) {
                gc.moveTo(node.middlePoint.x, node.middlePoint.y)
                isFirst = false
            } else
                gc.lineTo(node.middlePoint.x, node.middlePoint.y)
        }

        gc.stroke()
        // draw end

        println("graph nodes = ${graph.nodes.size} - graph arcs = ${graph.nodes.flatMap { it.value.arcs }.toSet().size}")
        println("path size = ${pathOptimized.size}; original path size = ${path.size}")

        println("time to build tree with robot            = ${t1_0-t0} ms")
        println("time to build initial free rectangle set = ${t2-t1_1} ms")
        println("time to from node to rectangles          = ${t3-t2} ms")
        println("time to compact rectangle set            = ${t4-t3} ms")
        println("time to build graph                      = ${t6-t5} ms")
        println("time to AStarAlgorithm                   = ${t7-t6} ms")
        println("time to build path                       = ${t8-t7} ms")
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