package jpm.android.com

import android.util.Log
import jpm.android.common.RunnableBase
import java.io.DataInputStream
import java.io.IOException
import java.util.concurrent.PriorityBlockingQueue

import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue


class Broker(useMock: Boolean, maxMessageTypes: Int, bufferSendInitialCapacity: Int, bufferRecInitialCapacity: Int) {

    init {
        Log.i("Broker","Broker($useMock,$maxMessageTypes,$bufferSendInitialCapacity,$bufferRecInitialCapacity)")
    }

    private val readers = Array( maxMessageTypes , { nopMessageReader } )
    private val messageListeners = Array( maxMessageTypes , { LinkedList<MessageListener>() } )

    private val sendQueue = PriorityBlockingQueue<SendMessage>(bufferSendInitialCapacity)
    private val recQueue = ArrayBlockingQueue<Message>(bufferRecInitialCapacity)

    private val bluetooth:BluetoothInterface =
            if(useMock) BluetoothMock(readers,recQueue,sendQueue)
            else Bluetooth(readers,recQueue,sendQueue)

    fun isConnected() = bluetooth.isConnected()

    private var notifier = Notifier(messageListeners,recQueue)

    init {
        val threadNotifier = Thread(notifier)
        threadNotifier.start()
    }

    fun disconnect() {
        notifier.stop = true
        bluetooth.disconnect()
    }

    fun send(message: Message) { // send internal messages
        recQueue.offer(message)
    }

    fun send(header: Byte, messageWriter: MessageWriter) { // send bluetooth messages
        sendQueue.offer(SendMessage(header, messageWriter, 100))
    }

    fun sendPriority(header: Byte, messageWriter: MessageWriter, priority: Int) { // send bluetooth messages
        sendQueue.offer(SendMessage(header, messageWriter, priority))
    }

    @Synchronized @Throws(Exception::class)
    fun setReader(header: Byte, messageReader: MessageReader) {
        //if (readers.indices.contains(header.toInt())) throw Exception("setReader: key out of range key = ${header.toInt()}, readers.indices = ${readers.indices}")
        if (readers.size < header.toInt()) throw Exception("setReader: key out of range key = ${header.toInt()}, readers.indices = ${readers.indices}")
        readers[header.toInt()] = messageReader
    }

    @Synchronized
    fun setListener(header: Byte, listener: MessageListener) {
        if (messageListeners.indices.contains(header.toInt())) {
            messageListeners[header.toInt()].add(listener)
        }
    }

    @Synchronized
    fun removeListener(header: Byte, listener: MessageListener) {
        if (messageListeners.indices.contains(header.toInt())) {
            messageListeners[header.toInt()].remove(listener)
        }
    }

    private class Notifier(private val messageListeners: Array<LinkedList<MessageListener>>,
                           private val recQueue: BlockingQueue<Message>) : RunnableBase() {

        override fun execute() {
            try {
                val m = recQueue.take()
                if (messageListeners.indices.contains(m.header)) {
                    for (l in messageListeners[m.header.toInt()]) {
                        l.onMessage(m)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private class NoMessage: Message(-1)

        private class NopMessageReader: MessageReader {
            @Throws(IOException::class)
            override fun read(inStream: DataInputStream): Message = NoMessage()
        }
        private val nopMessageReader:MessageReader = NopMessageReader()
    }
}
