package jpm.android.com

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.Serializable

interface MessageWriter {
    @Throws(IOException::class)
    fun write(outStream: DataOutputStream)
}

interface MessageReader {
    @Throws(IOException::class)
    fun read(inStream: DataInputStream): Message
}

interface MessageListener {
    fun onMessage(message: Message)
}

abstract class Message(val header: Byte): Serializable

internal class SendMessage(val header: Byte, val messageWriter: MessageWriter, private val priority: Int) : Comparable<SendMessage> {
    private val orderNumber = getOrderNumber()

    override operator fun compareTo(other: SendMessage): Int {
        if (this.priority < other.priority)
            return -1
        else if (this.priority > other.priority)
            return 1
        else if (this.orderNumber < other.orderNumber)
            return -1
        else if (this.orderNumber > other.orderNumber)
            return 1
        else
            return 0
    }

    companion object {
        private var orderNumberSeed: Long = 0

        @Synchronized private fun getOrderNumber(): Long {
            return ++orderNumberSeed
        }
    }
}
