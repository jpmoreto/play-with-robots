package jpm.android.com

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import jpm.android.common.RunnableBase
import jpm.android.messages.MotorsSpeedMessageReader
import jpm.android.messages.MpuSensorsValuesReader
import jpm.android.messages.UsArrayDistancesReader
import jpm.android.messages.VccPowerReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.BlockingQueue

/**
 * Created by jm on 18/02/17.
 *
 */

/*
http://stackoverflow.com/questions/23801678/how-to-get-bluetooth-hardware-address-of-remote-device-from-its-properties
http://stackoverflow.com/questions/30214320/android-bluetooth-mac-address
http://stackoverflow.com/questions/19315523/how-to-get-mac-address-of-bluetooth-connected-device-in-android

http://www.allaboutcircuits.com/projects/control-an-arduino-using-your-phone/
http://www.instructables.com/id/Modify-The-HC-05-Bluetooth-Module-Defaults-Using-A/

https://forum.arduino.cc/index.php?topic=200545.15
http://www.martyncurrey.com/arduino-with-hc-05-bluetooth-module-at-mode/
http://www.martyncurrey.com/arduino-with-hc-05-bluetooth-module-in-slave-mode/
http://www.martyncurrey.com/hc-05-and-hc-06-zs-040-bluetooth-modules-first-look/
http://www.instructables.com/id/AT-command-mode-of-HC-05-Bluetooth-module/step5/AT-commands/

http://www.amarino-toolkit.net/index.php/getting-started.html
http://wormfood.net/avrbaudcalc.php

https://github.com/hmartiro/android-arduino-bluetooth
https://github.com/plastygrove/BlueSerial
https://www.intorobotics.com/how-to-develop-simple-bluetooth-android-application-to-control-a-robot-remote/
https://developer.android.com/guide/topics/connectivity/bluetooth.html
https://www.tutorialspoint.com/android/android_bluetooth.htm

 */

internal interface BluetoothInterface {
    fun isConnected(): Boolean
    fun disconnect()
}

internal class Bluetooth(
        val readers: Array<MessageReader>,
        val recQueue: BlockingQueue<Message>,
        val sendQueue: BlockingQueue<SendMessage>): BluetoothInterface {


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

    private class Reader(private val readers: Array<MessageReader>,
                         private val recQueue: BlockingQueue<Message>,
                         private val inStream: DataInputStream) : RunnableBase() {

        override fun execute() {
            try {
                if(inStream.available() > 0) {
                    val header = inStream.readByte()
                    if (readers.indices.contains(header)) {
                        val message = readers[header.toInt()].read(inStream)
                        if (message.header > 0) {
                            recQueue.offer(message)
                        }
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

    private class Writer(private val sendQueue: BlockingQueue<SendMessage>, private val outStream: DataOutputStream) : RunnableBase() {

        override fun execute() {
            try {
                val m = sendQueue.take()
                write(m.header, m.messageWriter)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        override fun close() {
            outStream.close()
        }

        @Throws(IOException::class)
        private fun write(header: Byte, messageWriter: MessageWriter) {
            outStream.writeByte(header.toInt())
            messageWriter.write(outStream)
        }
    }

    private var ba = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null

    private var reader: Reader? = null
    private var writer: Writer? = null

    init {
        val threadConnect = Thread(Connector())
        threadConnect.start()
    }

    override fun isConnected() = socket != null && socket!!.isConnected

    override fun disconnect() {
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

        reader = Reader(readers, recQueue, inStream)
        val threadReader = Thread(reader)
        threadReader.start()

        writer = Writer(sendQueue, outStream)
        val threadWriter = Thread(writer)
        threadWriter.start()
    }

    companion object {
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private val address = "00:00:00:00:00:00" // substituir pelo endereço da placa arduino
    }
}

internal class BluetoothMock(
        readers: Array<MessageReader>,
        recQueue: BlockingQueue<Message>,
        sendQueue: BlockingQueue<SendMessage>): BluetoothInterface {

    private val usSensorSimulator = UsSensorSimulator(recQueue,360,20)
    private val mpuSensorSimulator = MpuSensorSimulator(recQueue,10,2)
    private val motorsSpeedSensorSimulator = MotorsSpeedSensorSimulator(recQueue,10,2)
    private val vccSensorSimulator = VccSensorSimulator(recQueue,2000,20)

    override fun isConnected() = true

    override fun disconnect() {
        usSensorSimulator.stop = true
        mpuSensorSimulator.stop = true
        motorsSpeedSensorSimulator.stop = true
        vccSensorSimulator.stop = true
    }

    private abstract class SensorSimulator(val recQueue: BlockingQueue<Message>, val sleepTime: Long, val sleepTimeError: Int) : RunnableBase() {

        protected val rndValue = java.util.Random()
        protected val rndTime = java.util.Random()

        init {
            val thread = Thread(this)
            thread.start()
        }

        override fun execute() {
            sendMessage()
            try {
                Thread.sleep(sleepTime + rndTime.nextInt(sleepTimeError))
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        protected abstract fun sendMessage()
    }

    private class UsSensorSimulator(recQueue: BlockingQueue<Message>, sleepTime: Long, sleepTimeError: Int) : SensorSimulator(recQueue,sleepTime,sleepTimeError) {

        override fun sendMessage() {
            val newSensores = IntArray(12) { i -> sensores[i] + rndValue.nextInt(200) }

            recQueue.offer(
                    UsArrayDistancesReader.UsArrayDistancesMessage(
                            System.currentTimeMillis(),
                            newSensores)
            )
        }
        companion object {
            private val sensores = intArrayOf(333,450,670,230,450,600,500,233,343,334,660,890)
        }
    }

    private class MpuSensorSimulator(recQueue: BlockingQueue<Message>, sleepTime: Long, sleepTimeError: Int) : SensorSimulator(recQueue,sleepTime,sleepTimeError) {

        override fun sendMessage() {

            val accX = 50 + rndValue.nextInt(100)
            val accY = 30 + rndValue.nextInt(100)
            val accZ = 70 + rndValue.nextInt(100)

            val girX = 5 + rndValue.nextInt(100)
            val girY = 10 + rndValue.nextInt(100)
            val girZ = 20 + rndValue.nextInt(100)

            val cmpX = 38 + rndValue.nextInt(20)
            val cmpY = 19 + rndValue.nextInt(20)
            val cmpZ = 90 + rndValue.nextInt(20)

            val temp = 24 + rndValue.nextInt(4)

            recQueue.offer(
                    MpuSensorsValuesReader.MpuSensorsValuesMessage(
                            System.currentTimeMillis(),
                            intArrayOf(accX,accY,accZ),
                            intArrayOf(girX,girY,girZ),
                            intArrayOf(cmpX,cmpY,cmpZ),
                            temp
                            )
            )
        }
    }

    private class MotorsSpeedSensorSimulator(recQueue: BlockingQueue<Message>, sleepTime: Long, sleepTimeError: Int) : SensorSimulator(recQueue,sleepTime,sleepTimeError) {

        override fun sendMessage() {

            val fl_s = 50 + rndValue.nextInt(10)
            val fr_s = 50 + rndValue.nextInt(10)
            val bl_s = 50 + rndValue.nextInt(10)
            val br_s = 50 + rndValue.nextInt(10)

            recQueue.offer(
                    MotorsSpeedMessageReader.MotorsSpeedMessage(
                            System.currentTimeMillis(),
                            fl_s,
                            fr_s,
                            bl_s,
                            br_s
                    )
            )
        }
    }

    private class VccSensorSimulator(recQueue: BlockingQueue<Message>, sleepTime: Long, sleepTimeError: Int) : SensorSimulator(recQueue,sleepTime,sleepTimeError) {

        override fun sendMessage() {

            val vcc = 500 + rndValue.nextInt(100)

            recQueue.offer(
                    VccPowerReader.VccPowerMessage(System.currentTimeMillis(), vcc)
            )
        }
    }
}