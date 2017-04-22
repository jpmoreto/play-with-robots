package jpm.android.ui.common

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment

/**
 * A placeholder fragment containing a simple view.
 */
abstract class BaseFragment : Fragment(), jpm.android.com.MessageListener {
    abstract fun getName(context: Context): String

    private class MessageHandler(val fragment: BaseFragment): Handler() {
        override fun handleMessage(msg: Message) {
            val message = msg.data["message"] as jpm.android.com.Message
            fragment.handleMessage(message)
        }
    }

    private val messageHandler = MessageHandler(this)

    override fun onMessage(message: jpm.android.com.Message) {
        val handlerMessage = messageHandler.obtainMessage()
        val b = Bundle(1)
        b.putSerializable("message",message)
        handlerMessage.data = b
        messageHandler.sendMessage(handlerMessage)
    }

    open fun handleMessage(message: jpm.android.com.Message) {}
}