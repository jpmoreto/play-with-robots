package jpm.android

import android.content.SharedPreferences

import jpm.android.com.Broker
import jpm.android.messages.*
import jpm.android.navigation.JoystickNavigator
import jpm.android.positionandmapping.PositionAndMappingBase


object App {
    private val MAX_VELOCITY_DEFAULT = 100
    private val MIN_VELOCITY_DEFAULT = 10
    private val MAX_ACCELERATION_DEFAULT = 10

    private val MAX_MESSAGE_TYPES = 20
    private val SEND_BUFFER_INITIAL_CAPACITY = 20
    private val REC_BUFFER_INITIAL_CAPACITY = 20

    var maxVelocity = MAX_VELOCITY_DEFAULT
    var minVelocity = MIN_VELOCITY_DEFAULT

    var maxAcceleration = MAX_ACCELERATION_DEFAULT

    private var broker: Broker? = null

    val motorsSpeedMessageReader = MotorsSpeedMessageReader()
    val logMessageReader = LogMessageReader()
    val mpuSensorsValuesReader = MpuSensorsValuesReader()
    val vccPowerReader = VccPowerReader()
    val usMessageReader = UsArrayDistancesReader()
    val compassCalibrationReader = CompassCalibrationValuesReader()

    val useJoystickNavigator = true
    var navigator: Any? = null

    val positionAndMapping = PositionAndMappingBase()

    fun init() {
       if (useJoystickNavigator)
           navigator = JoystickNavigator()
    }

    @Synchronized
    fun getBroker(): Broker {
        if (broker == null) {
            broker = Broker(true,MAX_MESSAGE_TYPES, SEND_BUFFER_INITIAL_CAPACITY,REC_BUFFER_INITIAL_CAPACITY)
            try {
                broker!!.setReader(ReaderMessageType.LOG.header, logMessageReader)
                broker!!.setReader(ReaderMessageType.UsArrayDistances.header, usMessageReader)
                broker!!.setReader(ReaderMessageType.MotorsSpeed.header, motorsSpeedMessageReader)
                broker!!.setReader(ReaderMessageType.MpuSensorsValues.header, mpuSensorsValuesReader)
                broker!!.setReader(ReaderMessageType.VccPower.header, vccPowerReader)
                broker!!.setReader(ReaderMessageType.CompassCalibrationValues.header, compassCalibrationReader)
            } catch (e: Exception) {
                e.printStackTrace()
            }

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
