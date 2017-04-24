package jpm.lib.comm

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by jm on 24/04/17.
 *
 */
class BrokerTst {

    class TestMessage(header: Byte, val payload: Int): Message(header,0,System.currentTimeMillis()) {
        override fun toString(): String {
            return "TestMessage(header=$header,payload=$payload)"
        }
    }

    class TestListener(val name: String, expectedMessages: Int): MessageListener {
        val recMessages = mutableListOf<Int>()
        private val countDownLatch = java.util.concurrent.CountDownLatch(expectedMessages)

        override fun onMessage(message: Message) {
            if(message is TestMessage) {
                recMessages.add(message.payload)
                println("$name TestListener.onMessage($message)")
                countDownLatch.countDown()
            }
        }

        fun waitMessageNumber() {
            countDownLatch.await()
        }
    }

    @Test
    fun testBroker() {
        val broker = Broker(10)
        broker.start()
        broker.stop()
        broker.start()

        val listener0 = TestListener("0",2)
        broker.setListener(MessageType.ChangeGraphVisibility.header,listener0)

        val listener1 = TestListener("1",1)
        broker.setListener(MessageType.Joystick.header,listener1)

        broker.send(TestMessage(MessageType.Joystick.header,1))
        broker.send(TestMessage(MessageType.ChangeGraphVisibility.header,1))
        broker.send(TestMessage(MessageType.ChangeGraphVisibility.header,2))

        Thread.sleep(100)
        broker.removeListener(MessageType.Joystick.header,listener1)

        val listener2 = TestListener("2",1)
        broker.setListener(MessageType.Joystick.header,listener2)

        broker.send(TestMessage(MessageType.Joystick.header,2))

        listener0.waitMessageNumber()
        listener1.waitMessageNumber()
        listener2.waitMessageNumber()

        broker.stop()

        println("recMessages0=${listener0.recMessages}")
        println("recMessages1=${listener1.recMessages}")
        println("recMessages2=${listener2.recMessages}")

        assertTrue(listener0.recMessages.size == 2)
        assertTrue(listener0.recMessages.contains(1))
        assertTrue(listener0.recMessages.contains(2))

        assertTrue(listener1.recMessages.size == 1)
        assertTrue(listener1.recMessages.contains(1))

        assertTrue(listener2.recMessages.size == 1)
        assertTrue(listener2.recMessages.contains(2))
    }
}