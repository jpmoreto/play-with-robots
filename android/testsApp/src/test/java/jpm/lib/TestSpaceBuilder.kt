package jpm.lib

/**
 * Created by jm on 18/03/17.

 */

import javafx.application.Application
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.stage.Stage
import jpm.lib.math.DoubleVector2D
import jpm.lib.simulator.SpaceBuilder
import org.junit.Test

class TestSpaceBuilder {
    @Test
    fun test() {
        try {
            SpaceBuilderTest().run(arrayOf<String>())
        } catch (e: Throwable) {
            // ignore
        }
    }
}

class SpaceBuilderTest : TestKDTreeDBase, Application() {
    override val deltaX: Int = 0
    override val deltaY: Int = 0
    override val rndX: Int = 0
    override val rndY: Int = 0
    override val dimMin: Double = 2.0
    override val initialDim: Int = 0
    override val centerPointD: DoubleVector2D = DoubleVector2D(0.0,0.0)
    override val iterations: Int = 1

    val occupiedThreshold = 0.7

    override fun start(primaryStage: Stage) {
        startDraw(primaryStage)
    }

    fun run(args: Array<String>) {
        launch(*args)
    }

    override fun drawShapesKDTreeD(gc: GraphicsContext) {
         drawShapesKDTreeD1(gc)
    }

    fun drawShapesKDTreeD1(gc: GraphicsContext) {
        val tree = SpaceBuilder.build()

        // draw begin
        //
        setGcColor(gc, Color.BLACK, Color.BLACK, 1.0)
        drawKDTreeD(gc,tree,occupiedThreshold)
    }
}