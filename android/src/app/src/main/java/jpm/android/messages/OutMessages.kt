package jpm.android.messages

import jpm.android.com.MessageWriter
import java.io.DataOutputStream
import java.io.IOException

enum class WriterMessageType(val header: Byte) {
    COMMAND_STOP(0),
    COMMAND_VELOCITY(1),
}

class CommandStopMessageWriter : MessageWriter {
    @Throws(IOException::class)
    override fun write(outStream: DataOutputStream) {
    }
}

class CommandVelocityMessageWriter(private val leftVelocity: Int, private val rightVelocity: Int) : MessageWriter {

    @Throws(IOException::class)
    override fun write(outStream: DataOutputStream) {
        val buffer = ByteArray(2)

        BitOper.toByte(leftVelocity,buffer)
        outStream.writeByte(buffer[0].toInt())
        outStream.writeByte(buffer[1].toInt())

        BitOper.toByte(rightVelocity,buffer)
        outStream.writeByte(buffer[0].toInt())
        outStream.writeByte(buffer[1].toInt())
    }
}
