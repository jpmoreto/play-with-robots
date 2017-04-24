package jpm.android

import android.content.SharedPreferences
import jpm.android.comm.Bluetooth

import jpm.android.messages.*
import jpm.android.navigation.JoystickNavigator
//import jpm.android.positionandmapping.PositionAndMappingBase
import jpm.lib.comm.Broker
import jpm.lib.comm.MessageType


object App {
    private val MAX_VELOCITY_DEFAULT = 100
    private val MIN_VELOCITY_DEFAULT = 10
    private val MAX_ACCELERATION_DEFAULT = 10

    private val BROKER_BUFFER_CAPACITY = 20
    private val BLUETOOTH_BUFFER_CAPACITY = 20

    var maxVelocity = MAX_VELOCITY_DEFAULT
    var minVelocity = MIN_VELOCITY_DEFAULT

    var maxAcceleration = MAX_ACCELERATION_DEFAULT

    private var broker: Broker? = null
    private var bluetooth: Bluetooth? = null

    val useJoystickNavigator = true
    var navigator: Any? = null

    //val positionAndMapping = PositionAndMappingBase()

    fun init() {
       if (useJoystickNavigator)
           navigator = JoystickNavigator()
    }

    @Synchronized
    fun getBroker(): Broker {
        if (broker == null) {
            broker = Broker(BROKER_BUFFER_CAPACITY)
            bluetooth = Bluetooth(broker!!, BLUETOOTH_BUFFER_CAPACITY)
            bluetooth!!.setReader(MessageType.Log.header, LogMessageReader())
            bluetooth!!.setReader(MessageType.UsArrayDistances.header, UsArrayDistancesReader())
            bluetooth!!.setReader(MessageType.MotorsSpeed.header, MotorsSpeedMessageReader())
            bluetooth!!.setReader(MessageType.MpuSensorsValues.header, MpuSensorsValuesReader())
            bluetooth!!.setReader(MessageType.VccPower.header, VccPowerReader())
            bluetooth!!.setReader(MessageType.CompassCalibrationValues.header, CompassCalibrationValuesReader())

            bluetooth!!.setWriter(MessageType.CompassCalibrationValues.header, CommandStopMessageWriter())
            bluetooth!!.setWriter(MessageType.CompassCalibrationValues.header, SetVelocityMessageWriter())

            broker!!.start()
            bluetooth!!.connect()
        }
        return broker!!
    }

    fun readConfiguration(prefs: SharedPreferences) {
        prefs.getInt("maxVelocity", MAX_VELOCITY_DEFAULT)
        prefs.getInt("minVelocity", MIN_VELOCITY_DEFAULT)
        prefs.getInt("maxAcceleration", MAX_ACCELERATION_DEFAULT)
    }

    fun writeConfiguration(prefs: SharedPreferences) {
        val prefsEditor = prefs.edit()
        prefsEditor.putInt("maxVelocity", maxVelocity)
        prefsEditor.putInt("minVelocity", minVelocity)
        prefsEditor.putInt("maxAcceleration", maxAcceleration)

        prefsEditor.apply() // or commit();
    }
}
