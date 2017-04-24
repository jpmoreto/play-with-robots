package jpm.lib.comm

import java.io.Serializable

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


interface MessageListener {
    fun onMessage(message: Message)
}

abstract class Message(val header: Byte, val priority: Int, val time: Long): Serializable, Comparable<Message> {

    override operator fun compareTo(other: Message): Int {
        if (this.priority < other.priority)
            return -1
        else if (this.priority > other.priority)
            return 1
        else
            return time.compareTo(other.time)
    }
}
