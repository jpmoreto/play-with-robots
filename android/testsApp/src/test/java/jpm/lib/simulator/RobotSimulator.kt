package jpm.lib.simulator

import jpm.lib.comm.Broker
import jpm.lib.comm.Message
import jpm.lib.comm.MessageListener
import jpm.lib.comm.RunnableBase
import jpm.lib.maps.KDTreeD
import jpm.lib.simulator.robotsubsystems.MeasureTimeMicros
import jpm.messages.MessageType
import jpm.messages.SetVelocityMessage
import jpm.messages.StartMessage
import jpm.messages.StopMessage
import java.util.concurrent.ArrayBlockingQueue

/**
 * Created by jm on 27/04/17.
 *
 */
class RobotSimulator(val broker: Broker, simulatedSpace: KDTreeD.Node): RunnableBase(), MessageListener {

    private val recQueue = ArrayBlockingQueue<Message>(20)

    private val TIMER_SLEEP_TIME_MICROS = 50_000L
    // operations periodicity defined in terms of time ticks (1 time tick = sleep time)
    // sleep time == 50000 => 20 time tiks/second
    //
    private val TIKS_READ_COMMANDS     = 10 // 10 * TIME_TICK ms
    private val TIKS_CONTROL_ACTUATORS = 1
    private val TIKS_READ_US_SENSOR    = 1
    private val TIKS_READ_MPU          = 1
    private val TIKS_READ_VCC          = 1000

    private val timerControl = MeasureTimeMicros(TIMER_SLEEP_TIME_MICROS)

    init {
        Thread(this).start()
        broker.setListener(MessageType.Start.header,this)
        broker.setListener(MessageType.Stop.header,this)
        broker.setListener(MessageType.SetVelocity.header,this)
    }

    private var ticksReadCommandsCount = 0
    private var ticksControlActuatorsCount = 0
    private var ticksReadUsSensor = 0
    private var ticksReadMpuSensor = 0
    private var ticksReadVCC = 0

    private var actualTimeMicros = 0L

    @Volatile private var started = false

    fun tryCall(ticksCount: Int, maxTicks: Int, func: () -> Unit): Int {

        var res = ticksCount + 1

        if (res == maxTicks) {
            res = 0
            func()
        }

        return res
    }


    fun tryReadAndProcessCommands() {
        while(!recQueue.isEmpty()) {
            val message = recQueue.take()
            println("RobotSimulator.tryReadAndProcessCommands($message)")
            when(message) {
                is StartMessage -> start()
                is StopMessage -> stop()
                is SetVelocityMessage -> if(started) setVelocity(message.leftVelocity,message.rightVelocity)
                else -> println("RobotSimulator.tryReadAndProcessCommands($message) unknown message")
            }
        }
    }

    fun tryControlActuators() {
    }

    fun tryReadUsSensors() {
    }

    fun tryReadVcc() {
    }

    fun tryReadMpuSensors() {
    }

    override fun execute() {

        timerControl.sleep();

        actualTimeMicros = System.nanoTime() / 1000L

        /*const unsigned long deltaTime = */timerControl.getDeltaTime(actualTimeMicros);

        if(!started) tryReadAndProcessCommands()
        else {
            ticksControlActuatorsCount = tryCall(ticksControlActuatorsCount, TIKS_CONTROL_ACTUATORS) { tryControlActuators() }
            ticksReadCommandsCount = tryCall(ticksReadCommandsCount, TIKS_READ_COMMANDS) { tryReadAndProcessCommands() }
            ticksReadMpuSensor = tryCall(ticksReadMpuSensor, TIKS_READ_MPU) { tryReadMpuSensors() }
            ticksReadUsSensor = tryCall(ticksReadUsSensor, TIKS_READ_US_SENSOR) { tryReadUsSensors() }
            ticksReadVCC = tryCall(ticksReadVCC, TIKS_READ_VCC) { tryReadVcc() }
        }
    }

    override fun onMessage(message: Message) {
        recQueue.offer(message)
    }

    private fun start() {
        started = true
    }

    private fun stop() {
        started = false
    }

    private fun setVelocity(leftVelocity: Int, rightVelocity: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}