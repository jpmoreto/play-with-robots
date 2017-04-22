package jpm.android.robot

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.reflect.KClass

object Robot {

    data class SensorInfo<T: Number>(val name: String, val unit: String, val unitShortName: String, val toUnits: Double, val dataCls: KClass<T>) {
        fun convert(from: T) = from.toDouble() * toUnits
    }

    val ACC_SENSOR  = 0
    val GYRO_SENSOR = 1
    val CMP_SENSOR  = 2
    val US_SENSOR   = 3
    val TEMP_SENSOR = 4
    val ROT_SENSOR  = 5

    val sensorsInfo = arrayOf(
            SensorInfo("Acc",  "g","g",      1.0/200.0, Int::class),
            SensorInfo("Gyro", "radian/second", "r/s",  1.0/200.0, Int::class),
            SensorInfo("Cmp",  "degree", "º", 1.0/200.0, Int::class),
            SensorInfo("US",   "meter",  "m", 1.0,       Int::class),
            SensorInfo("Temp", "degree", "º", 1.0,       Int::class),
            SensorInfo("Rot",  "meter",  "m", 2 * Math.PI * RobotDimensions.wheelRadius / 100.0, Int::class)
    )

    private val robot = Path()

    fun getRobot(deltaX: Float, deltaY: Float, scale: Float): Path {

        fun toScale(value: Float) = scale * value

        val halfRobotWith   = toScale(RobotDimensions.bodyWidth.toFloat()) / 2.0f
        val halfRobotLength = toScale(RobotDimensions.robotRectLength.toFloat()) / 2.0f

        val deltaXMinusHalfRobotWith = deltaX - halfRobotWith
        val deltaXPlusHalfRobotWith  = deltaX + halfRobotWith

        val deltaYMinusHalfRobotLength = deltaY - halfRobotLength
        val deltaYPlusHalfRobotLength = deltaY + halfRobotLength

        robot.rewind()

        robot.moveTo(deltaXMinusHalfRobotWith, deltaYMinusHalfRobotLength)
        robot.lineTo(deltaXMinusHalfRobotWith, deltaYPlusHalfRobotLength)
        robot.arcTo(
                deltaXMinusHalfRobotWith, deltaYPlusHalfRobotLength - halfRobotWith,
                deltaXPlusHalfRobotWith,  deltaYPlusHalfRobotLength + halfRobotWith,
                -180f,-180f,true)
        robot.lineTo(deltaXPlusHalfRobotWith, deltaYMinusHalfRobotLength)
        robot.arcTo(
                deltaXMinusHalfRobotWith, deltaYMinusHalfRobotLength - halfRobotWith,
                deltaXPlusHalfRobotWith,  deltaYMinusHalfRobotLength + halfRobotWith,
                0f,-180f,true)

        return robot
    }

    /*
       angles in android: http://www.cumulations.com/blogs/5/Understanding-Sweep-angle-in-drawArc-method-of-android

       rotate positive clockwise and 0 degrees is 3 hours (like trigonometric circle)
    */
    fun drawUsRead(deltaX: Float, deltaY: Float, scale: Float, sensorNum: Int, distance: Int, paint: Paint, canvas: Canvas) {
        fun toScale(value: Double) = scale * value.toFloat()

        val s = RobotDimensions.sonarSensors[sensorNum]

        canvas.drawArc(
                deltaX + toScale(s.x) - distance, deltaY - toScale(s.y) - distance,
                deltaX + toScale(s.x) + distance, deltaY - toScale(s.y) + distance,
                RobotDimensions.sonarSensors[sensorNum].angleDeg + 270f - RobotDimensions.sonarAngle / 2f,
                RobotDimensions.sonarAngle.toFloat(), true,paint)
    }
}
