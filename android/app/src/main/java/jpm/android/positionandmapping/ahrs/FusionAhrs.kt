package jpm.android.positionandmapping.ahrs

/*
  based on:
  https://github.com/xioTechnologies/Fusion/blob/master/Fusion/FusionAhrs.c
  http://x-io.co.uk/open-source-imu-and-ahrs-algorithms/
  https://hal-univ-tlse3.archives-ouvertes.fr/hal-00488376/document
 */
class FusionAhrs(val gain: Float, minMagneticField: Float,maxMagneticField: Float) {

    fun update(gyroscope: FloatArray, accelerometer: FloatArray, magnetometer: FloatArray, samplePeriod: Float) {

        // Calculate feedback error
        var halfFeedbackError = vector3Zero // scaled by 0.5 to avoid repeated multiplications by 2


        // Abandon feedback calculation if accelerometer measurement invalid
        if ( accelerometer[vX] != 0.0f || accelerometer[vY] != 0.0f || accelerometer[vZ] != 0.0f) {
            // Calculate direction of gravity assumed by quaternion
            val halfGravity = floatArrayOf(
                    quaternion[qX] * quaternion[qZ] - quaternion[qW] * quaternion[qY],
                    quaternion[qW] * quaternion[qX] + quaternion[qY] * quaternion[qZ],
                    quaternion[qW] * quaternion[qW] - 0.5f + quaternion[qZ] * quaternion[qZ])
            // equal to 3rd column of rotation matrix representation scaled by 0.5

            // Calculate accelerometer feedback error
            halfFeedbackError = vector3CrossProduct(vectorNormalise(accelerometer), halfGravity)

            // Abandon magnetometer feedback calculation if magnetometer measurement invalid
            val magnetometerNorm = magnetometer.fold(0.0f) { acc, e -> acc + e * e }

            if (magnetometerNorm in minMagneticFieldSquared..maxMagneticFieldSquared) {
                // Compute direction of 'magnetic west' assumed by quaternion
                val halfEast = floatArrayOf(
                        quaternion[qX] * quaternion[qY] + quaternion[qW] * quaternion[qZ],
                        quaternion[qW] * quaternion[qW] - 0.5f + quaternion[qY] * quaternion[qY],
                        quaternion[qY] * quaternion[qZ] - quaternion[qW] * quaternion[qX])
                // equal to 2nd column of rotation matrix representation scaled by 0.5

                // Calculate magnetometer feedback error
                halfFeedbackError = vectorAdd(halfFeedbackError, vector3CrossProduct(vectorNormalise(vector3CrossProduct(accelerometer, magnetometer)), halfEast))
            }
        }

        // Ramp down gain until initialisation complete
        if (gain == 0f) {
            rampedGain = 0f // skip initialisation if gain is zero
        }
        val feedbackGain = if (rampedGain > gain) {
            rampedGain -= (INITIAL_GAIN - gain) * samplePeriod / Companion.INITIALISATION_PERIOD
            rampedGain
        }
        else {
            gain
        }

        // Convert gyroscope to radians per second scaled by 0.5
        var halfGyroscope = vectorMult(gyroscope, 0.5f * degreesToRadians(1f))

        // Apply feedback to gyroscope
        halfGyroscope = vectorAdd(halfGyroscope, vectorMult(halfFeedbackError, feedbackGain))

        // Integrate rate of change of quaternion
        quaternion = vectorAdd(quaternion, quaternionMultVector3(quaternion, vectorMult(halfGyroscope, samplePeriod)))

        // Normalise quaternion
        quaternion = vectorNormalise(quaternion)

        // Calculate linear acceleration
        val gravity = floatArrayOf(
                2.0f * (quaternion[qX] * quaternion[qZ] - quaternion[qW] * quaternion[qY]),
                2.0f * (quaternion[qW] * quaternion[qX] + quaternion[qY] * quaternion[qZ]),
                2.0f * (quaternion[qW] * quaternion[qW] - 0.5f + quaternion[qZ] * quaternion[qZ]))
        // equal to 3rd column of rotation matrix representation

        linearAcceleration = vectorSub(accelerometer, gravity)
    }

    fun calculateEarthAcceleration(): FloatArray {
        val qwqw = quaternion[qW] * quaternion[qW] // calculate common terms to avoid repeated operations
        val qwqx = quaternion[qW] * quaternion[qX]
        val qwqy = quaternion[qW] * quaternion[qY]
        val qwqz = quaternion[qW] * quaternion[qZ]
        val qxqy = quaternion[qX] * quaternion[qY]
        val qxqz = quaternion[qX] * quaternion[qZ]
        val qyqz = quaternion[qY] * quaternion[qZ]

        // transpose of a rotation matrix representation of the quaternion multiplied with the linear acceleration
        return floatArrayOf(
          2.0f * ((qwqw - 0.5f + quaternion[qX] * quaternion[qX]) * linearAcceleration[vX] + (qxqy - qwqz) * linearAcceleration[vY] + (qxqz + qwqy) * linearAcceleration[vZ]),
          2.0f * ((qxqy + qwqz) * linearAcceleration[vX] + (qwqw - 0.5f + quaternion[qY] * quaternion[qY]) * linearAcceleration[vY] + (qyqz - qwqx) * linearAcceleration[vZ]),
          2.0f * ((qxqz - qwqy) * linearAcceleration[vX] + (qyqz + qwqx) * linearAcceleration[vY] + (qwqw - 0.5f + quaternion[qZ] * quaternion[qZ]) * linearAcceleration[vZ])
        )
    }

    fun isInitialising(): Boolean = rampedGain > gain

    fun reinitialise() {
        quaternion = quaternionIdentity
        linearAcceleration = vector3Zero
        rampedGain = INITIAL_GAIN
    }

    fun getQuaternion():         FloatArray = quaternion
    fun getLinearAcceleration(): FloatArray = linearAcceleration

    // http://www.novatel.com/solutions/attitude/
    // http://www.skylinesoft.com/SkylineGlobe/TerraExplorer/v6.5.0/APIReferenceGuide/Yaw_Pitch_and_Roll_Angles.htm
    // http://planning.cs.uiuc.edu/node102.html
    //
    //
    fun getRoll(): Double {
        return Math.atan2(
                quaternion[0] * quaternion[1].toDouble() + quaternion[2] * quaternion[3].toDouble(),
                0.5 - quaternion[1] * quaternion[1].toDouble() - quaternion[2] * quaternion[2].toDouble())
    }

    fun getPitch(): Double {
        return Math.asin(-2.0 * (quaternion[1] * quaternion[3].toDouble() - quaternion[0] * quaternion[2].toDouble()))
    }

    fun getYaw(): Double {
        return Math.atan2(
                quaternion[1] * quaternion[2].toDouble() + quaternion[0] * quaternion[3].toDouble(),
                0.5 - quaternion[2] * quaternion[2].toDouble() - quaternion[3] * quaternion[3].toDouble())
    }

    private val minMagneticFieldSquared = minMagneticField * minMagneticField
    private val maxMagneticFieldSquared = maxMagneticField * maxMagneticField

    private var quaternion = quaternionIdentity
    private var linearAcceleration = vector3Zero
    private var rampedGain = INITIAL_GAIN

    companion object {
        private val INITIALISATION_PERIOD = 3.0f
        private val INITIAL_GAIN = 10.0f
    }
}