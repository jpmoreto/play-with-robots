package jpm.lib.comm

import java.io.IOException

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue


class Broker(bufferRecCapacity: Int) {

    private val messageListeners = mutableMapOf<Byte,MutableList<MessageListener>>()
    private val recQueue = ArrayBlockingQueue<Message>(bufferRecCapacity)
    private var notifier = Notifier(messageListeners,recQueue)

    init {
        notifier.stop = true
    }

    fun stop() {
        notifier.stop = true
    }

    fun start() {
        if(notifier.stop) {
           notifier.stop = false
           Thread(notifier).start()
        }
    }

    fun send(message: Message) = recQueue.offer(message)

    /**
     * The listener must do trivial work, otherwise must delegate the work in another thread and do not use the caller thread for that
     */
    fun setListener(header: Byte, listener: MessageListener) {
        synchronized(messageListeners) {
            var listeners = messageListeners[header]
            if (listeners == null) {
                listeners = mutableListOf<MessageListener>()
                messageListeners.put(header, listeners)
            }
            listeners.add(listener)
        }
    }

    fun removeListener(header: Byte, listener: MessageListener) {
        synchronized(messageListeners) {
            val listeners = messageListeners[header]
            if (listeners != null) {
                listeners.remove(listener)
                if (listeners.isEmpty()) messageListeners.remove(header)
            }
        }
    }

    private class Notifier(private val messageListeners: Map<Byte,List<MessageListener>>,
                           private val recQueue: BlockingQueue<Message>) : RunnableBase() {
        override fun execute() {
            try {
                val m = recQueue.take()
                synchronized(messageListeners) {
                    val listeners = messageListeners[m.header]
                    if(listeners != null)
                        for (l in listeners) l.onMessage(m)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}
