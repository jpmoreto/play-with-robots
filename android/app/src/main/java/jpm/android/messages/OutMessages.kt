package jpm.android.messages

import jpm.android.comm.MessageWriter
import jpm.lib.comm.Message
import jpm.lib.comm.MessageType
import java.io.DataOutputStream
import java.io.IOException

class SetVelocityMessage(time: Long, val leftVelocity: Int, val rightVelocity: Int): Message(MessageType.SetVelocity.header,10,time)
class StopMessage(time: Long): Message(MessageType.Stop.header,10,time)

class CommandStopMessageWriter : MessageWriter {
    @Throws(IOException::class)
    override fun write(message: Message, outStream: DataOutputStream) {
    }
}

class SetVelocityMessageWriter : MessageWriter {

    @Throws(IOException::class)
    override fun write(message: Message, outStream: DataOutputStream) {
        if(message is SetVelocityMessage) {
            val buffer = ByteArray(2)

            BitOper.toByte(message.leftVelocity, buffer)
            outStream.writeByte(buffer[0].toInt())
            outStream.writeByte(buffer[1].toInt())

            BitOper.toByte(message.rightVelocity, buffer)
            outStream.writeByte(buffer[0].toInt())
            outStream.writeByte(buffer[1].toInt())
        }
    }
}
