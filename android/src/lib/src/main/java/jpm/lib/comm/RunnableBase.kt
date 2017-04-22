package jpm.lib.comm

/**
 * Created by jm on 18/02/17.
 *
 */
abstract class RunnableBase : Runnable {

    @Volatile var stop = false

    override fun run() {
        while (!stop) execute()
        close()
    }

    abstract fun execute()

    open fun close() {}
}