package jpm.lib.simulator

import jpm.lib.maps.KDTreeAbs
import jpm.lib.maps.KDTreeD
import jpm.lib.math.DoubleVector2D

/**
 * Created by jm on 25/04/17.
 *
 * Builds a simulated space and returns a KDTreeD with the representation of the space
 */

object SpaceBuilder {

    private val dim = Math.pow(2.0, 11.0)
    private val minDim = 2.0
    private val centerPointD = DoubleVector2D(675.0, 350.0)

    private val bottomLeft = DoubleVector2D(2.0,2.0)
    private val topRight = DoubleVector2D(1348.0,700.0)

    fun build(): KDTreeD.Node {
        KDTreeD.dimMin = minDim

        val tree = KDTreeD.Node(centerPointD, KDTreeAbs.SplitAxis.XAxis, dim)

        rectangle(tree, bottomLeft,topRight)
        //line(tree,DoubleVector2D(2.0,250.0),DoubleVector2D(1348.0,250.0))

        line(tree,DoubleVector2D(2.0,250.0),DoubleVector2D(150.0,250.0))
        line(tree,DoubleVector2D(250.0,250.0),DoubleVector2D(550.0,250.0))
        line(tree,DoubleVector2D(650.0,250.0),DoubleVector2D(900.0,250.0))
        line(tree,DoubleVector2D(1000.0,250.0),DoubleVector2D(1150.0,250.0))
        line(tree,DoubleVector2D(1250.0,250.0),DoubleVector2D(1348.0,250.0))

        line(tree,DoubleVector2D(400.0,2.0),DoubleVector2D(400.0,250.0))
        line(tree,DoubleVector2D(800.0,2.0),DoubleVector2D(800.0,250.0))
        line(tree,DoubleVector2D(1100.0,2.0),DoubleVector2D(1100.0,250.0))

        line(tree,DoubleVector2D(2.0,400.0),DoubleVector2D(150.0,400.0))
        line(tree,DoubleVector2D(250.0,400.0),DoubleVector2D(600.0,400.0))
        line(tree,DoubleVector2D(750.0,400.0),DoubleVector2D(1150.0,400.0))
        line(tree,DoubleVector2D(1250.0,400.0),DoubleVector2D(1348.0,400.0))

        line(tree,DoubleVector2D(300.0,400.0),DoubleVector2D(300.0,700.0))
        line(tree,DoubleVector2D(1000.0,400.0),DoubleVector2D(1000.0,700.0))

        line(tree,DoubleVector2D(600.0,400.0),DoubleVector2D(600.0,600.0))
        line(tree,DoubleVector2D(750.0,400.0),DoubleVector2D(750.0,600.0))

        vTable(tree, DoubleVector2D(50.0, 40.0))
        vTable(tree, DoubleVector2D(285.0, 40.0))
        hTable(tree, DoubleVector2D(155.0, 160.0))

        hTable(tree, DoubleVector2D(450.0, 80.0))
        hTable(tree, DoubleVector2D(630.0, 80.0))

        vTable(tree, DoubleVector2D(860.0, 40.0))
        vTable(tree, DoubleVector2D(980.0, 40.0))

        vTable(tree, DoubleVector2D(1240.0, 40.0))

        vTable(tree, DoubleVector2D(50.0, 430.0))
        vTable(tree, DoubleVector2D(50.0, 570.0))
        vTable(tree, DoubleVector2D(200.0, 570.0))

        vTable(tree, DoubleVector2D(350.0, 570.0))
        hTable(tree, DoubleVector2D(450.0, 450.0))

        return tree
    }

    private fun rectangle(tree: KDTreeD.Node, bottomLeft: DoubleVector2D, topRight: DoubleVector2D) {
        println("rectangle($bottomLeft,$topRight)")
        val halfDimMin = KDTreeD.dimMin / 2.0

        var x = bottomLeft.x
        while(x <= topRight.x) {
            val top = DoubleVector2D(x, topRight.y)
            tree.setOccupied(top)

            val bottom = DoubleVector2D(x, bottomLeft.y)
            tree.setOccupied(bottom)
            x += halfDimMin
        }
        var y = bottomLeft.y
        while(y <= topRight.y) {
            val right = DoubleVector2D(topRight.x, y)
            tree.setOccupied(right)

            val left = DoubleVector2D(bottomLeft.x, y)
            tree.setOccupied(left)
            y += halfDimMin
        }
    }

    private fun line(tree: KDTreeD.Node, p1: DoubleVector2D, p2: DoubleVector2D) {
        println("line($p1,$p2)")
        val halfDimMin = KDTreeD.dimMin / 2.0

        /* y = a * x + b

           solve([p1_y = a * p1_x + b, p2_y = a * p2_x + b],[a,b])
           [[a=(p1_y-p2_y)/(p1_x-p2_x),b=-(p1_y*p2_x-p1_x*p2_y)/(p1_x-p2_x)]]
        */

        if(Math.abs(p2.x - p1.x) >= Math.abs(p2.y - p1.y)) {
            val xMax = Math.max(p1.x,p2.x)
            var x = Math.min(p1.x,p2.x)

            while(x <= xMax) {
                val a = (p1.y-p2.y)/(p1.x-p2.x)
                val b = -(p1.y*p2.x - p1.x*p2.y)/(p1.x-p2.x)
                tree.setOccupied(DoubleVector2D(x, a * x + b))
                x += halfDimMin
            }
        } else {
            val yMax = Math.max(p1.y,p2.y)
            var y = Math.min(p1.y,p2.y)

            while(y <= yMax) {
                val a = (p1.x-p2.x)/(p1.y-p2.y)
                val b = -(p1.x*p2.y - p1.y*p2.x)/(p1.y-p2.y)
                tree.setOccupied(DoubleVector2D(a * y + b, y))
                y += halfDimMin
            }
        }
    }

    private fun vTable(tree: KDTreeD.Node, pos: DoubleVector2D) {

        line(tree, DoubleVector2D(0.0, 0.0) + pos, DoubleVector2D(50.0, 0.0) + pos)
        line(tree, DoubleVector2D(0.0, 100.0) + pos, DoubleVector2D(50.0, 100.0) + pos)
        line(tree, DoubleVector2D(25.0, 0.0)+ pos, DoubleVector2D(25.0, 100.0) + pos)
    }

    private fun hTable(tree: KDTreeD.Node, pos: DoubleVector2D) {

        line(tree, DoubleVector2D(0.0, 0.0) + pos, DoubleVector2D(0.0, 50.0) + pos)
        line(tree, DoubleVector2D(100.0, 0.0) + pos, DoubleVector2D(100.0, 50.0) + pos)
        line(tree, DoubleVector2D(0.0, 25.0) + pos, DoubleVector2D(100.0, 25.0) + pos)
    }
}
