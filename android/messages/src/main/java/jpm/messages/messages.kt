package jpm.messages

import jpm.lib.comm.Message
import java.util.*

/**
 * Created by jm on 24/04/17.
 *
 */
enum class MessageType(val header: Byte) {
    // special message
    // out messages
    Start(10),
    Stop(11),
    SetVelocity(12),
    // in messages
    MotorsSpeed(30),
    UsArrayDistances(31),
    MpuSensorsValues(32),
    VccPower(33),
    Log(34),
    MpuAndSpeedSensorsValues(35),
    CompassCalibrationValues(36),
    // internal messages
    ChangeGraphVisibility(50),
    Joystick(51),
    NewPose(52)
}

// Arduino In messages

class MotorsSpeedMessage(time: Long, val frontLeftSpeed: Int, val frontRightSpeed: Int, val backLeftSpeed: Int, val backRightSpeed: Int):
    Message(MessageType.MotorsSpeed.header, 0, time) {
    override fun toString(): String {
        return "MotorsSpeedMessage(frontLeftSpeed=$frontLeftSpeed, frontRightSpeed=$frontRightSpeed, backLeftSpeed=$backLeftSpeed, backRightSpeed=$backRightSpeed)"
    }

}
class UsArrayDistancesMessage(time: Long, val distances: IntArray):
    Message(MessageType.UsArrayDistances.header,0,time) {
    override fun toString(): String {
        return "UsArrayDistancesMessage(distances=${Arrays.toString(distances)})"
    }

}
class MpuSensorsValuesMessage(time: Long, val accelerometer: IntArray, val gyroscope: IntArray, val compass: IntArray, val temperature: Int):
    Message(MessageType.MpuSensorsValues.header,0,time) {
    override fun toString(): String {
        return "MpuSensorsValuesMessage(accelerometer=${Arrays.toString(accelerometer)}, gyroscope=${Arrays.toString(gyroscope)}, compass=${Arrays.toString(compass)}, temperature=$temperature)"
    }

}
class CompassCalibrationValuesMessage(time: Long, val compassBias: FloatArray, val compassScale: FloatArray, val factoryCompassCalibration: FloatArray):
    Message(MessageType.CompassCalibrationValues.header,0,time) {
    override fun toString(): String {
        return "CompassCalibrationValuesMessage(compassBias=${Arrays.toString(compassBias)}, compassScale=${Arrays.toString(compassScale)}, factoryCompassCalibration=${Arrays.toString(factoryCompassCalibration)})"
    }

}
class VccPowerMessage(time: Long, val vcc: Int):
    Message(MessageType.VccPower.header,0,time) {
    override fun toString(): String {
        return "VccPowerMessage(vcc=$vcc)"
    }

}
class LogMessage(time: Long, val severity: Int, val log: String):
    Message(MessageType.Log.header,0,time) {
    override fun toString(): String {
        return "LogMessage(severity=$severity, log='$log')"
    }

}
class MpuAndSpeedSensorsValuesMessage(
    time: Long,
    val accelerometer: IntArray,
    val gyroscope: IntArray,
    val compass: IntArray,
    val temperature: Int,
    val frontLeftSpeed: Int,
    val frontRightSpeed: Int,
    val backLeftSpeed: Int,
    val backRightSpeed: Int): Message(MessageType.MpuAndSpeedSensorsValues.header,0,time) {
    override fun toString(): String {
        return "MpuAndSpeedSensorsValuesMessage(accelerometer=${Arrays.toString(accelerometer)}, gyroscope=${Arrays.toString(gyroscope)}, compass=${Arrays.toString(compass)}, temperature=$temperature, frontLeftSpeed=$frontLeftSpeed, frontRightSpeed=$frontRightSpeed, backLeftSpeed=$backLeftSpeed, backRightSpeed=$backRightSpeed)"
    }

}

// Arduino out messages
class SetVelocityMessage(time: Long, val leftVelocity: Int, val rightVelocity: Int): Message(MessageType.SetVelocity.header,10,time)  {
    override fun toString(): String {
        return "SetVelocityMessage(leftVelocity=$leftVelocity, rightVelocity=$rightVelocity)"
    }

}
class StopMessage(time: Long): Message(MessageType.Stop.header,10,time) {
    override fun toString(): String {
        return "StopMessage()"
    }

}
class StartMessage(time: Long): Message(MessageType.Start.header,10,time) {
    override fun toString(): String {
        return "StartMessage()"
    }

}

// Internal Messages
class ChangeGraphVisibility(time: Long, val itemId: Int, val checked: Boolean): Message(MessageType.ChangeGraphVisibility.header, 10, time) {
    override fun toString(): String {
        return "ChangeGraphVisibility(itemId=$itemId, checked=$checked)"
    }

}
class JoystickMessage(time: Long,val degrees: Float, val offset: Float): Message(MessageType.Joystick.header,10,time) {
    override fun toString(): String {
        return "JoystickMessage(degrees=$degrees, offset=$offset)"
    }

}
class NewPoseMessage(time: Long,val degrees: Double, val x: Double, val y: Double): Message(MessageType.NewPose.header, 10, time) {
    override fun toString(): String {
        return "NewPoseMessage(degrees=$degrees, x=$x, y=$y)"
    }

}
