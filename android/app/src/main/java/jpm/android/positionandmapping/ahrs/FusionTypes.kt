package jpm.android.positionandmapping.ahrs

/*
* based on https://github.com/xioTechnologies/Fusion/blob/master/Fusion/FusionTypes.h

 */

val vector3Zero            = floatArrayOf(0.0f, 0.0f, 0.0f)
val quaternionIdentity     = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f)
val rotationMatrixIdentity = floatArrayOf(1.0f, 0.0f, 0.0f,
                                          0.0f, 1.0f, 0.0f,
                                          0.0f, 0.0f, 1.0f)
val vX = 0
val vY = 1
val vZ = 2

val qW = 0
val qX = 1
val qY = 2
val qZ = 3

fun degreesToRadians(degrees: Float): Float = degrees * Math.PI.toFloat() / 180.0f
fun radiansToDegrees(degrees: Float): Float = degrees * 180.0f / Math.PI.toFloat()

fun vectorAdd(a: FloatArray, b: FloatArray): FloatArray = FloatArray(a.size, { i -> a[i] + b[i] })
fun vectorSub(a: FloatArray, b: FloatArray): FloatArray = FloatArray(a.size, { i -> a[i] - b[i] })
fun vectorMult(a: FloatArray, s: Float): FloatArray     = FloatArray(a.size, { i -> s * a[i] })

fun vector3CrossProduct(a: FloatArray, b: FloatArray): FloatArray = floatArrayOf(
        a[vY] * b[vZ] - a[vZ] * b[vY],
        a[vZ] * b[vX] - a[vX] * b[vZ],
        a[vX] * b[vY] - a[vY] * b[vX])

fun vectorNormalise(a: FloatArray): FloatArray {
    val norm = inverseSqrt(a)

    return FloatArray(a.size, { i -> norm * a[i] })
}

fun vectorMagnitude(a: FloatArray): Float = Math.sqrt(a.fold(0.0f) { acc, e -> acc + e * e }.toDouble()).toFloat()

fun quaternionMult(a: FloatArray, b: FloatArray): FloatArray = floatArrayOf(
        a[qW] * b[qW] - a[qX] * b[qX] - a[qY] * b[qY] - a[qZ] * b[qZ],
        a[qW] * b[qX] + a[qX] * b[qW] + a[qY] * b[qZ] - a[qZ] * b[qY],
        a[qW] * b[qY] - a[qX] * b[qZ] + a[qY] * b[qW] + a[qZ] * b[qX],
        a[qW] * b[qZ] + a[qX] * b[qY] - a[qY] * b[qX] + a[qZ] * b[qW])

fun quaternionMultVector3(q: FloatArray, v: FloatArray): FloatArray = floatArrayOf(
        -q[qX] * v[vX] - q[qY] * v[vY] - q[qZ] * v[vZ],
         q[qW] * v[vX] + q[qY] * v[vZ] - q[qZ] * v[vY],
         q[qW] * v[vY] - q[qX] * v[vZ] + q[qZ] * v[vX],
         q[qW] * v[vZ] + q[qX] * v[vY] - q[qY] * v[vX])

fun quaternionConjugate(q: FloatArray): FloatArray = floatArrayOf(q[qW], -q[qX], -q[qY], -q[qZ])

fun quaternionToRotationMatrix(q: FloatArray): FloatArray {
    val qwqw = q[qW] * q[qW] // calculate common terms to avoid repeated operations
    val qwqx = q[qW] * q[qX]
    val qwqy = q[qW] * q[qY]
    val qwqz = q[qW] * q[qZ]
    val qxqy = q[qX] * q[qY]
    val qxqz = q[qX] * q[qZ]
    val qyqz = q[qY] * q[qZ]

    return floatArrayOf(
            2.0f * (qwqw - 0.5f + q[qX] * q[qX]),  // xx
            2.0f * (qxqy + qwqz),                  // xy
            2.0f * (qxqz - qwqy),                  // xz
            2.0f * (qxqy - qwqz),                  // yx
            2.0f * (qwqw - 0.5f + q[qY] * q[qY]),  // yy
            2.0f * (qyqz + qwqx),                  // yz
            2.0f * (qxqz + qwqy),                  // zx
            2.0f * (qyqz - qwqx),                  // zy
            2.0f * (qwqw - 0.5f + q[qZ] * q[qZ]))  // zz
}

fun quaternionToEulerAngles(q: FloatArray): FloatArray {
    val qwSquaredMinusHalf = q[qW] * q[qW] - 0.5f

    return floatArrayOf(
            radiansToDegrees(Math.atan2((q[qY] * q[qZ] - q[qW] * q[qX]).toDouble(), (qwSquaredMinusHalf + q[qZ] * q[qZ]).toDouble()).toFloat()), // roll
            radiansToDegrees(-Math.asin((2.0f * (q[qX] * q[qZ] + q[qW] * q[qY])).toDouble()).toFloat()),                                         // pitch
            radiansToDegrees(Math.atan2((q[qX] * q[qY] - q[qW] * q[qZ]).toDouble(), (qwSquaredMinusHalf + q[qX] * q[qX]).toDouble()).toFloat())) // yaw
}

fun inverseSqrt(a: FloatArray): Float = (1.0f / Math.sqrt(a.fold(0.0f) { acc, e -> acc + e * e }.toDouble())).toFloat()
