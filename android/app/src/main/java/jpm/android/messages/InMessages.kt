package jpm.android.messages

import jpm.android.comm.MessageReader
import java.io.DataInputStream
import java.io.IOException
import jpm.android.messages.BitOper.toInt
import jpm.android.messages.BitOper.toLong
import jpm.lib.comm.Message
import jpm.lib.comm.MessageType

private fun readInt(inStream: DataInputStream): Int {
    val msb = inStream.readByte()
    val lsb = inStream.readByte()

    return toInt(msb,lsb)
}

private fun readLong(inStream: DataInputStream): Long {
    val msb4 = inStream.readByte()
    val msb3 = inStream.readByte()
    val msb2 = inStream.readByte()
    val msb1 = inStream.readByte()

    return toLong(msb4,msb3,msb2,msb1)
}

private fun readIntArray(array: IntArray, inStream: DataInputStream) {
    for(i in array.indices) {
        array[i] = readInt(inStream)
    }
}

private fun readFloatArrayFromString(array: FloatArray, inStream: DataInputStream) {
    val buf = CharArray(64)

    var count = 0
    val end: Char = 0.toChar()
    var char: Char

    do {
        char = inStream.readChar()
        buf[count] = char
        count += 1
    } while (char != end)

    val str = String(buf,0,count-1)

    val numbers = str.split(";")

    for (i in 0..2) {
        array[i] = numbers[i].toFloat()
    }
}

class MotorsSpeedMessageReader: MessageReader {

    @Throws(IOException::class)
    override fun read(inStream: DataInputStream): Message {
        val timeStamp = readLong(inStream)
        val frontLeftSpeed = readInt(inStream)
        val frontRightSpeed = readInt(inStream)
        val backLeftSpeed = readInt(inStream)
        val backRightSpeed = readInt(inStream)

        return MotorsSpeedMessage(timeStamp, frontLeftSpeed, frontRightSpeed,backLeftSpeed,backRightSpeed)
    }

    class MotorsSpeedMessage(time: Long, val frontLeftSpeed: Int, val frontRightSpeed: Int, val backLeftSpeed: Int, val backRightSpeed: Int):
        Message(MessageType.MotorsSpeed.header, 0, time)
}

class UsArrayDistancesReader: MessageReader {

    @Throws(IOException::class)
    override fun read(inStream: DataInputStream): Message {
        val timeStamp = readLong(inStream)
        val distances = IntArray(12)
        readIntArray(distances,inStream)

        return UsArrayDistancesMessage(timeStamp, distances)
    }

    class UsArrayDistancesMessage(time: Long, val distances: IntArray):
        Message(MessageType.UsArrayDistances.header,0,time)
}

class MpuSensorsValuesReader: MessageReader {

    @Throws(IOException::class)
    override fun read(inStream: DataInputStream): Message {
        val timeStamp = readLong(inStream)
        val accelerometer = IntArray(3)
        readIntArray(accelerometer,inStream)
        val gyroscope = IntArray(3)
        readIntArray(gyroscope,inStream)
        val compass = IntArray(3)
        readIntArray(compass,inStream)
        val temperature = readInt(inStream)

        return MpuSensorsValuesMessage(timeStamp, accelerometer, gyroscope, compass,temperature)
    }

    class MpuSensorsValuesMessage(time: Long, val accelerometer: IntArray, val gyroscope: IntArray, val compass: IntArray, val temperature: Int):
        Message(MessageType.MpuSensorsValues.header,0,time)
}

class CompassCalibrationValuesReader: MessageReader {

    @Throws(IOException::class)
    override fun read(inStream: DataInputStream): Message {
        val timeStamp = readLong(inStream)
        val compassBias = FloatArray(3)
        readFloatArrayFromString(compassBias,inStream)
        val compassScale = FloatArray(3)
        readFloatArrayFromString(compassScale,inStream)
        val factoryCompassCalibration = FloatArray(3)
        readFloatArrayFromString(factoryCompassCalibration,inStream)

        return CompassCalibrationValuesMessage(timeStamp, compassBias, compassScale, factoryCompassCalibration)
    }

    class CompassCalibrationValuesMessage(time: Long, val compassBias: FloatArray, val compassScale: FloatArray, val factoryCompassCalibration: FloatArray):
        Message(MessageType.CompassCalibrationValues.header,0,time)
}

class VccPowerReader: MessageReader {

    @Throws(IOException::class)
    override fun read(inStream: DataInputStream): Message {
        val timeStamp = readLong(inStream)
        val vcc = readInt(inStream)

        return VccPowerMessage(timeStamp, vcc)
    }

    class VccPowerMessage(time: Long, val vcc: Int):
        Message(MessageType.VccPower.header,0,time)
}

class LogMessageReader : MessageReader {

    @Throws(IOException::class)
    override fun read(inStream: DataInputStream): Message {
        val timeStamp = inStream.readLong()
        val severity = inStream.readByte().toInt()
        val log = inStream.readUTF()

        return LogMessage(timeStamp, severity, log)
    }

    class LogMessage(time: Long, val severity: Int, val log: String):
        Message(MessageType.Log.header,0,time)
}

class MpuAndSpeedSensorsValuesReader: MessageReader {

    @Throws(IOException::class)
    override fun read(inStream: DataInputStream): Message {
        val timeStamp = readLong(inStream)
        val accelerometer = IntArray(3)
        readIntArray(accelerometer,inStream)
        val gyroscope = IntArray(3)
        readIntArray(gyroscope,inStream)
        val compass = IntArray(3)
        readIntArray(compass,inStream)
        val temperature = readInt(inStream)
        val frontLeftSpeed = readInt(inStream)
        val frontRightSpeed = readInt(inStream)
        val backLeftSpeed = readInt(inStream)
        val backRightSpeed = readInt(inStream)

        return MpuAndSpeedSensorsValuesMessage(
                timeStamp, accelerometer, gyroscope, compass,temperature,
                frontLeftSpeed, frontRightSpeed, backLeftSpeed, backRightSpeed)
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
            val backRightSpeed: Int): Message(MessageType.MpuAndSpeedSensorsValues.header,0,time)
}
