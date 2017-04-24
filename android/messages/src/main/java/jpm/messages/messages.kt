package jpm.messages

import jpm.lib.comm.Message

/**
 * Created by jm on 24/04/17.
 *
 */
enum class MessageType(val header: Byte) {
    // special message
    // out messages
    Stop(10),
    SetVelocity(11),
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
    Message(MessageType.MotorsSpeed.header, 0, time)
class UsArrayDistancesMessage(time: Long, val distances: IntArray):
    Message(MessageType.UsArrayDistances.header,0,time)
class MpuSensorsValuesMessage(time: Long, val accelerometer: IntArray, val gyroscope: IntArray, val compass: IntArray, val temperature: Int):
    Message(MessageType.MpuSensorsValues.header,0,time)
class CompassCalibrationValuesMessage(time: Long, val compassBias: FloatArray, val compassScale: FloatArray, val factoryCompassCalibration: FloatArray):
    Message(MessageType.CompassCalibrationValues.header,0,time)
class VccPowerMessage(time: Long, val vcc: Int):
    Message(MessageType.VccPower.header,0,time)
class LogMessage(time: Long, val severity: Int, val log: String):
    Message(MessageType.Log.header,0,time)
class MpuAndSpeedSensorsValuesMessage(
    time: Long,
    val accelerometer: IntArray,
    val gyroscope: IntArray,
    val compass: IntArray,
    val temperature: Int,
    val frontLeftSpeed: Int,
    val frontRightSpeed: Int,
    val backLeftSpeed: Int,
    val backRightSpeed: Int): Message(MessageType.MpuAndSpeedSensorsValues.header,0,time)

// Arduino out messages
class SetVelocityMessage(time: Long, val leftVelocity: Int, val rightVelocity: Int): Message(MessageType.SetVelocity.header,10,time)
class StopMessage(time: Long): Message(MessageType.Stop.header,10,time)

// Internal Messages
class ChangeGraphVisibility(time: Long, val itemId: Int, val checked: Boolean): Message(MessageType.ChangeGraphVisibility.header, 10, time)
class JoystickMessage(time: Long,val degrees: Float, val offset: Float): Message(MessageType.Joystick.header,10,time)
class NewPoseMessage(time: Long,val degrees: Double, val x: Double, val y: Double): Message(MessageType.NewPose.header, 10, time)
