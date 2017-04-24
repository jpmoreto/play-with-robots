package jpm.lib.comm

import java.io.Serializable

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
