package jpm.android.messages

import jpm.android.comm.MessageWriter
import jpm.lib.comm.Message
import jpm.messages.SetVelocityMessage
import java.io.DataOutputStream
import java.io.IOException

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
