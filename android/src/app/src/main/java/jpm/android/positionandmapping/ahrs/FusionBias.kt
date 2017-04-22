package jpm.android.positionandmapping.ahrs

/**
 * Created by jm on 07/01/17.
 * based on https://github.com/xioTechnologies/Fusion/blob/master/Fusion/FusionBias.c
 */
class FusionBias(val adcThreshold: Int = 0, val samplePeriod: Float = 0f) {

    fun update(xAdc: Int, yAdc: Int, zAdc: Int) {
        if ( xAdc > adcThreshold || xAdc < -adcThreshold ||
             yAdc > adcThreshold || yAdc < -adcThreshold ||
             zAdc > adcThreshold || zAdc < -adcThreshold ) {

            stationaryTimer = 0.0f

        } else {
            if (stationaryTimer >= STATIONARY_PERIOD) {
              val gyroscope = vectorSub(floatArrayOf (xAdc.toFloat(), yAdc.toFloat(), zAdc.toFloat()) , gyroscopeBias)
              gyroscopeBias = vectorAdd(gyroscopeBias, vectorMult(gyroscope, (2.0 * Math.PI * CORNER_FREQUENCY * samplePeriod).toFloat()))
            } else {
              stationaryTimer += samplePeriod
            }
        }
    }

    fun isActive():Boolean             = stationaryTimer >= STATIONARY_PERIOD
    fun getGyroscopeBias(): FloatArray = gyroscopeBias

    private var stationaryTimer = 0f // internal state (must not be modified by the application)
    private var gyroscopeBias = vector3Zero // algorithm output (may be modified at any time by the application)

    companion object {
        private val STATIONARY_PERIOD = 5.0f
        private val CORNER_FREQUENCY = 0.02f
    }
}