package jpm.android.comm

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import jpm.lib.comm.Broker
import jpm.lib.comm.Message
import jpm.lib.comm.MessageListener
import jpm.lib.comm.RunnableBase
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue

/**
 * Created by jm on 18/02/17.
 *
 */

class Bluetooth(val broker: Broker,bufferRecCapacity: Int): MessageListener {

    override fun onMessage(message: Message) {
        sendQueue.offer(message)
    }

    private val sendQueue = PriorityBlockingQueue<Message>(bufferRecCapacity)
    private val readers = mutableMapOf<Byte,MessageReader>()
    private val writers = mutableMapOf<Byte,MessageWriter>()

    private inner class Connector : Runnable {
        override fun run() {
            while (!isConnected()) {
                try {
                    connect()
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
    }

    private class Reader(private val readers: Map<Byte,MessageReader>,
                         private val broker: Broker,
                         private val inStream: DataInputStream) : RunnableBase() {

        override fun execute() {
            try {
                if(inStream.available() > 0) {
                    val header = inStream.readByte()
                    synchronized(readers) {
                        val reader = readers[header]
                        if(reader != null) broker.send(reader.read(inStream))
                    }
                } else {
                    Thread.sleep(1,0)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun close() {
            inStream.close()
        }
    }

    private class Writer(private val writers: Map<Byte,MessageWriter>, private val sendQueue: BlockingQueue<Message>, private val outStream: DataOutputStream) : RunnableBase() {

        override fun execute() {
            try {
                val m = sendQueue.take()
                synchronized(writers) {
                    val writer = writers[m.header]
                    if(writer != null) {
                        outStream.writeByte(m.header.toInt())
                        writer.write(m, outStream)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        override fun close() {
            outStream.close()
        }
    }

    private var ba = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null

    private var reader: Reader? = null
    private var writer: Writer? = null

    fun setWriter(header: Byte, writer: MessageWriter) {
        synchronized(writers) {
            writers.put(header,writer)
        }
        broker.setListener(header,this)
    }

    fun setReader(header: Byte, reader: MessageReader) {
        synchronized(readers) {
            readers.put(header,reader)
        }
    }

    init {
        val threadConnect = Thread(Connector())
        threadConnect.start()
    }

    fun isConnected() = socket != null && socket!!.isConnected

    fun disconnect() {
        writer?.stop = true
        reader?.stop = true
        try {
            socket?.close()
        } catch (e: IOException) {
        }
    }

    @Synchronized @Throws(IOException::class)
    internal fun connect() {
        if(ba == null) {
            // Device does not support Bluetooth
        }
        if(!ba.isEnabled) {
            // Bluetooth is not enabled
        }
        ba.cancelDiscovery()

        val device = ba.getRemoteDevice(address)
        socket = device.createInsecureRfcommSocketToServiceRecord (SPP_UUID)

        socket?.connect()
        if (!socket!!.isConnected) throw IOException("Error connecting to nxt")

        val outStream = DataOutputStream(socket?.outputStream)
        val inStream = DataInputStream(socket?.inputStream)

        reader = Reader(readers, broker, inStream)
        val threadReader = Thread(reader)
        threadReader.start()

        writer = Writer(writers, sendQueue, outStream)
        val threadWriter = Thread(writer)
        threadWriter.start()
    }

    companion object {
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private val address = "00:00:00:00:00:00" // substituir pelo endereço da placa arduino
    }
}
