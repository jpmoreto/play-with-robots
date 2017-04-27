package jpm.android.positionandmapping

import jpm.android.positionandmapping.PositionAndMappingBase.Companion.getLinearVelocity
import jpm.android.robot.Pose
import jpm.android.robot.RobotDimensions
import org.junit.Test

/**
 * Created by jm on 06/03/17.
 *
 */
class PositionAndMappingBaseTst {

    @Test
    fun cubicPolynomialTest() {
        /*
        a diferença entre o angulo inicial e final < +- 90º senão dividir o path
         */
        //cubicPolynomialTest(0.0)
        //cubicPolynomialTest(1*Math.PI/8)
        //cubicPolynomialTest(2*Math.PI/8)
        //cubicPolynomialTest(3*Math.PI/8)
        cubicPolynomialTest(3.2*Math.PI/8)
        cubicPolynomialToTest(3.2*Math.PI/8)
        //cubicPolynomialTest(3.5*Math.PI/8)
        //cubicPolynomialTest(3.8*Math.PI/8)
        //cubicPolynomialTest(4*Math.PI/8)
        //cubicPolynomialTest(5*Math.PI/8)
        //cubicPolynomialTest(6*Math.PI/8)
        //cubicPolynomialTest(7*Math.PI/8)

        /*
        angulo final - inicial < +- 70º senão dividir o path
        x2 - x1 >= 0.1 ou outra maneira é:

        se |x2 - x1| > |y2 - y1| calcular x em função de y senão calcular y em função de x (que é o que estamos a fazer aqui)
         */
    }

    private fun cubicPolynomialTest(angle:Double) {
        val x2_ = 1.0
        val y2_ = 30.0

        run {
            val x2 = x2_ // escala no eixo dos x: 0.0 , x2_
            val y2 = y2_
            val endAngle = angle

            val p = PositionAndMappingBase.newCubicPolynomial(Pose(0, 0.0, 0.0, 0.0), Pose(0, x2, y2, endAngle))
            println("1-Normal CubicPolynomial(Pose(0,0.0,0.0,0.0), Pose(0,$x2,$y2,${Math.toDegrees(endAngle)})) = $p")
        }
        if(Math.abs(x2_) >= Math.abs(y2_)) {
            val x2 = x2_ // escala no eixo dos x: 0.0 , x2_
            val y2 = y2_
            val endAngle = angle

            val p = PositionAndMappingBase.newCubicPolynomial(Pose(0, 0.0, 0.0, 0.0), Pose(0, x2, y2, endAngle))
            println("CubicPolynomial(Pose(0,0.0,0.0,0.0), Pose(0,$x2,$y2,${Math.toDegrees(endAngle)})) = $p")
        } else {
            val x2 = -y2_  // escala no eixo dos x: -y2_ , 0.0
            val y2 = x2_
            val endAngle = Math.PI - angle

            val p = PositionAndMappingBase.newCubicPolynomial(Pose(0, 0.0, 0.0, 0.0), Pose(0, x2, y2, endAngle))
            println("2-Inverso CubicPolynomial(Pose(0,0.0,0.0,0.0), Pose(0,$x2,$y2,${Math.toDegrees(endAngle)})) = $p")
        }
    }

    private fun cubicPolynomialToTest(angle:Double) {
        val x2_ = 1.0
        val y2_ = 30.0

        run {
            val x2 = x2_ // escala no eixo dos x: 0.0 , x2_
            val y2 = y2_
            val endAngle = angle

            val p = PositionAndMappingBase.newCubicPolynomialTo(x2, y2, endAngle)
            println("1-Normal CubicPolynomialTo($x2,$y2,${Math.toDegrees(endAngle)}) = $p")
        }
        if(Math.abs(x2_) >= Math.abs(y2_)) {
            val x2 = x2_ // escala no eixo dos x: 0.0 , x2_
            val y2 = y2_
            val endAngle = angle

            val p = PositionAndMappingBase.newCubicPolynomialTo(x2, y2, endAngle)
            println("CubicPolynomialTo($x2,$y2,${Math.toDegrees(endAngle)}) = $p")
        } else {
            val x2 = -y2_  // escala no eixo dos x: -y2_ , 0.0
            val y2 = x2_
            val endAngle = Math.PI - angle

            val p = PositionAndMappingBase.newCubicPolynomialTo(x2, y2, endAngle)
            println("2-Inverso CubicPolynomialTo($x2,$y2,${Math.toDegrees(endAngle)}) = $p")
        }
    }

    //@Test
    fun changeCoordinates1() {

        test1(0.1,0.1,0.1)
        test1(0.1,0.15,0.1)
        test1(0.1,0.20,0.1)
        test1(0.1,0.30,0.1)
        test1(0.1,0.50,0.1)
        test1(0.1,2.0,0.1)
        test1(0.1,4.0,0.1)
        test1(0.0,4.0,0.1)
        test1(4.0,0.0,0.1)
        test1(4.0,-2.0,0.1)
        test1(4.0,-4.0,0.1)
        println("")
        test1(0.1,0.1,0.5)
        test1(0.1,0.15,0.5)
        test1(0.1,0.20,0.5)
        test1(0.1,0.30,0.5)
        test1(0.1,0.50,0.5)
        test1(0.1,2.0,0.5)
        test1(0.1,4.0,0.5)
        test1(0.0,4.0,0.5)
        test1(4.0,0.0,0.5)
        test1(4.0,-2.0,0.5)
        test1(4.0,-4.0,0.5)
        println("")
        test1(0.1,0.1,1.0)
        test1(0.1,0.15,1.0)
        test1(0.1,0.20,1.0)
        test1(0.1,0.30,1.0)
        test1(0.1,0.50,1.0)
        test1(0.1,2.0,1.0)
        test1(0.1,4.0,1.0)
        test1(0.0,4.0,1.0)
        test1(4.0,0.0,1.0)
        test1(4.0,-2.0,1.0)
        test1(4.0,-4.0,1.0)
        println("")
    }

    private fun test1(vl: Double,vr:Double, n: Double) {
        // a diferença entre o angulo teórico e o real tendo em conta o atrito aumenta quanto maior for o angulo
        val vteta = if(vr != 0.0 || vl != 0.0)
            ((vr - vl) / RobotDimensions.distanceWheel) * ( 6.0 + 4 * Math.abs(Math.min(vr,vl)/Math.max(vr,vl))) / 10.0
        else 0.0

        val v_a = getLinearVelocity(vl, vr, vteta, 0.0 ,n)
        val v_b = getLinearVelocity(vl, vr, 0.0, 0.0 ,n)

        println("(vteta=$vteta,\tn=$n)\tv_a=$v_a;\tv_b=$v_b;\tv_a/v_b=${v_a.y/v_b.y}")
    }

    @Test
    fun changeCoordinates() {

        test(Math.PI / 2.0,0.5)
        test(Math.PI / 4.0,0.5)
        test(Math.PI / 8.0,0.5)
        test(Math.PI / 16.0,0.5)

        test(Math.PI / 2.0,1.0)
        test(Math.PI / 4.0,1.0)
        test(Math.PI / 8.0,1.0)
        test(Math.PI / 16.0,1.0)

        test(Math.PI / 2.0,0.1)
        test(Math.PI / 4.0,0.1)
        test(Math.PI / 8.0,0.1)
        test(Math.PI / 16.0,0.1)
    }

    private fun test(vteta: Double, n: Double) {
        val v_a = getLinearVelocity(0.1, 0.15, vteta, 0.0 ,n)
        val v_b = getLinearVelocity(0.1, 0.15, 0.0, 0.0 ,n)

        println("(vteta=$vteta,\tn=$n)\tv_a=$v_a;\tv_b=$v_b;")
    }
}