package jpm.android.positionandmapping.ahrs

object FusionCompass {
    fun calculateHeading(accelerometer: FloatArray, magnetometer: FloatArray): Float {
        // Compute direction of 'magnetic west' (Earth's y axis)
        val magneticWest = vectorNormalise(vector3CrossProduct(accelerometer, magnetometer))

        // Compute direction of magnetic north (Earth's x axis)
        val magneticNorth = vectorNormalise(vector3CrossProduct(magneticWest, accelerometer))

        // Calculate angular heading relative to magnetic north
        return radiansToDegrees(Math.atan2(magneticWest[vX].toDouble(), magneticNorth[vX].toDouble()).toFloat())
    }
}