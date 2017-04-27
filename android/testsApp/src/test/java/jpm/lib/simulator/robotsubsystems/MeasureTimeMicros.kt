package jpm.lib.simulator.robotsubsystems

/**
 * Created by jm on 27/04/17.
 *
 */
class MeasureTimeMicros(val sleepTimeMicro: Long) {

    fun setup() {
        getDeltaTime(System.nanoTime() / 1000L)
    }

    fun getDeltaTime(actualTimeMicro: Long): Long {
        val delta =
            if(actualTimeMicro > previousTime) actualTimeMicro - previousTime
            else Long.MAX_VALUE - previousTime + actualTimeMicro

        if(isFirstTime == false) {
            sleepTimeError = delta - sleepTimeMicro;
        }

        previousTime = actualTimeMicro;
        isFirstTime = false;

        return delta;
    }

    fun sleep() {
        val mili = (sleepTimeMicro - sleepTimeError) / 1000
        val nano = ((sleepTimeMicro - sleepTimeError) - mili * 1000) * 1000
        Thread.sleep(mili,nano.toInt())
    }

    private var previousTime = 0L
    private var sleepTimeError = 0L
    private var isFirstTime = true
}