package jpm.lib.simulator

import jpm.lib.comm.Broker
import jpm.messages.StartMessage

/**
 * Created by jm on 27/04/17.
 *
 */
object SimulatorController {

    val broker = Broker(24)
    val simulatedSpace = SpaceBuilder.build()
    val robotSimulator = RobotSimulator(broker,simulatedSpace)

    init {
        broker.send(StartMessage(System.currentTimeMillis()))
    }
}