package jpm.android.robot

/**
 * Created by jm on 17/01/17.
 *
 *
 * see drawings:
 *   /opt/docs/ARDUINO/myStufs/robot/openscad/robot_diagram.dxf
 *   /opt/docs/ARDUINO/myStufs/robot/openscad/robot_diagram_symbols.dxf
 *   /opt/docs/ARDUINO/myStufs/robot/openscad/trignometria.dxf
 *
 *   resume:
 *   - Y axis aligned with length positive angle to front
 *   - X axis aligned with width positive angle to right
 *   - 0 º angle aligned with front, rotate clockwise
 */
object RobotDimensions {

    data class SonarInfo(val angleDeg: Int, val angleRad: Double, val x: Double, val y: Double)

    // units meters:
    //
    val robotCircleRadius = 0.07                                        // BW / 2
    val robotRectLength = 0.056                                         // BSd
    val bodyWidth = 2 * robotCircleRadius                               // BW
    val bodyTotalLength = 2 * robotCircleRadius + robotRectLength       // Bl

    val wheelBodyDistance = 0.005                                       //
    val wheelRadius = 0.0325                                            // Wd / 2
    val wheelWidth = 0.026                                              // Ww
    val distanceWheel = bodyWidth + wheelWidth + 2 * wheelBodyDistance  // BWw
    val wheelAxisLenght = 0.075                                         // BWl

    val robotTotalWidth = bodyWidth + 2 * (wheelBodyDistance + wheelWidth)

    val sonarAngle = 30                                                 // usDelta

    val wheelSensorTicksPerRotation = 100

    val sonarSensors = Array(12) { i ->
        val angleDeg = i * sonarAngle
        val angleRad = Math.toRadians(angleDeg.toDouble())

        val (x,y) = when(i) {
            9       -> Pair(-robotCircleRadius, 0.0) // left
            3       -> Pair( robotCircleRadius, 0.0) // right
            0       -> Pair(0.0,  robotRectLength / 2.0 + robotCircleRadius) // front
            6       -> Pair(0.0, -robotRectLength / 2.0 - robotCircleRadius) // back
            4,5,7,8 -> Pair(robotCircleRadius * Math.sin(angleRad), -robotRectLength / 2.0 + robotCircleRadius * Math.cos(angleRad)) // negative Y
            else    -> Pair(robotCircleRadius * Math.sin(angleRad),  robotRectLength / 2.0 + robotCircleRadius * Math.cos(angleRad)) // positive Y (1,2,10,11)
        }

        SonarInfo(angleDeg, angleRad, x, y)
    }
/*
    init {
        var i = 0
        sonarSensors.forEach { s ->
            println("Sendor($i) \t= $s\n")
            i += 1
        }
    }
*/
}