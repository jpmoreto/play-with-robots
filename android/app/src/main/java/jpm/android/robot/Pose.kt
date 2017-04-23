package jpm.android.robot

import jpm.lib.math.power2

/**
 * Created by jm on 19/02/17.
 * @time time in millisecond when the robot has the pose
 * @x position in meter
 * @x position in mter
 * @angle in radian - angle between Global and Local frame.
 *        On star we can assume that the robot is oriented with global frame,
 *        or if we have a compass we can assume that global frame Y points to north and X to west.
 *        Global Frame orientation = Local frame orientation + angle
 *        (angle positive in clockwise - unlike trigonometric circle)
 */
data class Pose(val time: Long, val x: Double, val y: Double, val angle: Double) {

    // return value in millimeters
    fun distanceTo(endPose: Pose): Double =
            Math.sqrt(power2(endPose.x - x) + power2(endPose.y - y))

    // return value in milli radians
    fun rotationTo(endPose: Pose): Double =
            endPose.angle - angle

    // return value in millimeter / millisecond
    fun velocityTo(endPose: Pose): Double =
            distanceTo(endPose) / (endPose.time - time)

    // return value in milli radians / millisecond
    fun angularVelocityTo(endPose: Pose): Double =
            rotationTo(endPose) / (endPose.time - time)

    // return value in millimeter / millisecond^2
    fun accelerationTo(pose1: Pose, pose2: Pose): Double = // v = v0 + a * t => a = (v - v0) / t
            (pose1.velocityTo(pose2) - velocityTo(pose1)) / (pose2.time - pose1.time)

    // return value in milli radians / millisecond^2
    fun angularAccelerationTo(pose1: Pose, pose2: Pose): Double = // v = v0 + a * t => a = (v - v0) / t
            (pose1.angularVelocityTo(pose2) - angularVelocityTo(pose1)) / (pose2.time - pose1.time)

}

fun toGlobalCoordinates(l: Pose): Pose {
    val cos = Math.cos(l.angle)
    val sin = Math.sin(l.angle)

    return Pose(l.time, l.x * cos - l.y * sin, l.x * sin + l.y * cos, l.angle)
}

fun toLocalCoordinates(g: Pose): Pose {
    val cos = Math.cos(g.angle)
    val sin = Math.sin(g.angle)

    return Pose(g.time, g.x * cos + g.y * sin, -g.x * sin + g.y * cos, g.angle)
}
