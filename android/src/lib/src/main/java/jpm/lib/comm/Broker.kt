package jpm.lib.comm

import java.io.IOException

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue


class Broker(bufferRecCapacity: Int) {

    init {
        //Log.i("Broker","Broker($useMock,$maxMessageTypes,$bufferSendInitialCapacity,$bufferRecInitialCapacity)")
    }

    private val messageListeners = mutableMapOf<Byte,MutableList<MessageListener>>()

    private val recQueue = ArrayBlockingQueue<Message>(bufferRecCapacity)

    private var notifier = Notifier(messageListeners,recQueue)

    init {
        val threadNotifier = Thread(notifier)
        threadNotifier.start()
    }

    fun stop() {
        send(StopBrokerMessage)
    }
    fun send(message: Message) {
        recQueue.offer(message)
    }

    @Synchronized
    fun setListener(header: Byte, listener: MessageListener) {
        var listeners = messageListeners.get(header)
        if(listeners != null) {
            listeners = mutableListOf<MessageListener>()
            messageListeners.put(header,listeners)
        }
        listeners?.add(listener)
    }

    @Synchronized
    fun removeListener(header: Byte, listener: MessageListener) {
        val listeners = messageListeners.get(header)
        if(listeners != null) {
            listeners.remove(listener)
            if(listeners.isEmpty()) messageListeners.remove(header)
        }
    }

    private class Notifier(private val messageListeners: Map<Byte,List<MessageListener>>,
                           private val recQueue: BlockingQueue<Message>) : RunnableBase() {

        override fun execute() {
            try {
                val m = recQueue.take()
                if(m.header == MessageType.StopBroker.header) return

                val listeners = messageListeners.get(m.header)

                if(listeners != null)
                    for (l in listeners) l.onMessage(m)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}
