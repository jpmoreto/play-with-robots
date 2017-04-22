package jpm.android.positionandmapping

import jpm.android.App
import jpm.android.com.Message
import jpm.android.com.MessageListener
import jpm.lib.math.*
import jpm.android.messages.*
import jpm.android.positionandmapping.ahrs.FusionAhrs
import jpm.android.robot.Pose
import jpm.android.robot.RobotDimensions.distanceWheel

/**
 * Created by jm on 20/02/17.
 *
 * http://planning.cs.uiuc.edu/node102.html
 */

/*
  Consideramos 2 referencias para as coordenadas:

    - Referencia local (local frame):
        Referência que tem como base o próprio veículo. O centro é o centro geométrico do veículo
        o eixo dos y aponta na direcção do movimento do veiculo e o eixo dos x aponta para o lado direito do veículo.
        (x,y)

        vl - velocidade das rodas esquerdas
        vr - velocidade das rodas direitas

        v = (vx,vy)
        vx - velocidade linear projectada no eixo dos x
        vy - velocidade linear projectada no eixo dos y
        vteta - velocidade de rotação do veiculo
        teta - rotação do veiculo
        t - periodo de tempo correspondente ao movimento do robot

        inputs:
         - vl - dado pelos sensores de rotação
                vl = ((frontLeftSpeed + backLeftSpeed) / 2) * 2 * Math.PI * wheelRadius / wheelSensorTicksPerRotation
                   = (frontLeftSpeed + backLeftSpeed) * Math.PI * wheelRadius / wheelSensorTicksPerRotation
         - vr - dado pelos sensores de rotação
                vr = ((frontRightSpeed + backRightSpeed) / 2) * 2 * Math.PI * wheelRadius / wheelSensorTicksPerRotation
                   = (frontRightSpeed + backRightSpeed) * Math.PI * wheelRadius / wheelSensorTicksPerRotation
         - vteta - dado pelo gyroscópio + bussola
         - teta - dado pelo gyroscópio + bussola e o tempo de rotação
         - t
         - n - constante definida experimentalmente, mas deve ser >= 0 e possivelmente <= 1

        outputs:
         - vx
         - vy

         teta = vteta * t

         vx    = (( vr * (1 - ar) + vl * (1 - al) ) / 2) * cos(teta)
         vy    = (( vr * (1 - ar) + vl * (1 - al) ) / 2) * sin(teta)

         vteta = (( vr * (1 - ar) - vl * (1 - al) ) / distanceWheel)

         al = -sign(vl * vr) * ar * pow(abs(vr/vl), n), vl != 0

         ||
         ar = -sign(vl * vr) * al * pow(abs(vl/vr), n), vr != 0

         usando wxmaxima:

         solve([
         vx    = (( vr * (1 - ar) + vl * (1 - al) ) / 2) * cos_teta,
         vy    = (( vr * (1 - ar) + vl * (1 - al) ) / 2) * sin_teta,
         vteta = (( vr * (1 - ar) - vl * (1 - al) ) / distanceWheel),
         al = -sg * ar * po
         ],[vx,vy,al,ar]);

         vx = (vr*(cos_teta*distanceWheel*vteta+cos_teta*(2*po*sg+2)*vl)-cos_teta*distanceWheel*po*sg*vl*vteta)/(2*vr+2*po*sg*vl)
         vy = (vr*(distanceWheel*sin_teta*vteta+(2*po*sg+2)*sin_teta*vl)-distanceWheel*po*sg*sin_teta*vl*vteta)/(2*vr+2*po*sg*vl)
         al =-(-distanceWheel*po*sg*vteta+po*sg*vr-po*sg*vl)/(vr+po*sg*vl)
         ar = (-distanceWheel*vteta+vr-vl)/(vr+po*sg*vl)]]

         solve([
         vteta = (( vr * (1 - ar) - vl * (1 - al) ) / distanceWheel),
         al = -sg * ar * po
         ],[al,ar]);

         al =  (distanceWheel*po*sg*vteta-po*sg*vr+po*sg*vl)/(vr+po*sg*vl)
         ar = -(distanceWheel*vteta-vr+vl)/(vr+po*sg*vl)]]

         solve([
         vteta = (( vr * (1 - ar) - vl * (1 - al) ) / distanceWheel),
         ar = -sg * al * po
         ],[al,ar]);

         al=(distanceWheel*vteta-vr+vl)/(po*sg*vr+vl),
         ar=-(distanceWheel*po*sg*vteta-po*sg*vr+po*sg*vl)/(po*sg*vr+vl)

    - Referencia Global (global frame):
        Referencia estática global. O centro é um ponto arbitrário escolhido à priory
        o eixo dos y aponta para norte o eixo dos x aponta este.
        (X,Y)

        V = (VX,VY)
        VX - velocidade linear projectada no eixo dos X
        VY - velocidade linear projectada no eixo dos Y
        VTeta - velocidade de rotação do veiculo = vteta


Path suave:

y = a * x**3 +b * x ** 2 + c * x + d

y(x1) = y1
y(x2) = y2

y'(x1) = dx1 = tangent do angulo
y'(x2) = dx2 = tangent do angulo

usando wxmaxima:
y(x) := a * x^3 +b * x^2 + c * x + d;

diff(y(x),x);
 =
dy(x) := 3 * a * x^2 + 2 * b * x + c


solve([y(x1) = y1, y(x2) = y2, dy(x1) = tetax1, dy(x2) = tetax2],[a,b,c,d]);


a = -(-2*y2+2*y1+(tetax2+tetax1)*x2+(-tetax2-tetax1)*x1)/(-x2^3+3*x1*x2^2-3*x1^2*x2+x1^3)
b = (-3*x2*y2+x1*((tetax2-tetax1)*x2-3*y2)+(3*x2+3*x1)*y1+(tetax2+2*tetax1)*x2^2+(-2*tetax2-tetax1)*x1^2)/(-x2^3+3*x1*x2^2-3*x1^2*x2+x1^3),
c = -(x1*((2*tetax2+tetax1)*x2^2-6*x2*y2)+6*x1*x2*y1+tetax1*x2^3+(-tetax2-2*tetax1)*x1^2*x2-tetax2*x1^3)/(-x2^3+3*x1*x2^2-3*x1^2*x2+x1^3)
d = (x1^2*((tetax2-tetax1)*x2^2-3*x2*y2)+x1^3*(y2-tetax2*x2)+(3*x1*x2^2-x2^3)*y1+tetax1*x1*x2^3)/(-x2^3+3*x1*x2^2-3*x1^2*x2+x1^3)

 */
class PositionAndMappingBase : MessageListener {

    companion object {
        val n = 0.5

        /**
         * y(x) := a * x^3 +b * x^2 + c * x + d;
         */
        data class CubicPolynomial(val a: Double, val b: Double, val c: Double, val d: Double)

        fun newCubicPolynomial(orig: Pose, dest: Pose): CubicPolynomial {

            val x1 = orig.x
            val y1 = orig.y
            val tetax1 = Math.tan(orig.angle)
            val x2 = dest.x
            val y2 = dest.y
            val tetax2 = Math.tan(dest.angle)

            val x1P2 = Math.pow(x1, 2.0)
            val x1P3 = Math.pow(x1, 3.0)
            val x2P2 = Math.pow(x2, 2.0)
            val x2P3 = Math.pow(x2, 3.0)

            val tetax2Px1 = tetax2 + tetax1
            val tetax2Mx1 = tetax2 - tetax1
            val x2Xy2 = x2 * y2

            val div = -x2P3 + 3 * x1 * x2P2 - 3 * x1P2 * x2 + x1P3

            val a = (2 * (y2 + y1) + tetax2Px1 * (x1 - x2)) / div

            val b = (-3 * x2Xy2 + x1 * (tetax2Mx1 * x2 - 3 * y2) + 3 * (x2 + x1) * y1 + (tetax2 + 2 * tetax1) * x2P2 + (-2 * tetax2 - tetax1) * x1P2) / div

            val c = -(x1 * (2 * tetax2Px1 * x2P2 - 6 * x2Xy2) + 6 * x1 * x2 * y1 + tetax1 * x2P3 - (tetax2 + 2 * tetax1) * x1P2 * x2 - tetax2 * x1P3) / div

            val d = (x1P2 * (tetax2Mx1 * x2P2 - 3 * x2Xy2) + x1P3 * (y2 - tetax2 * x2) + (3 * x1 * x2P2 - x2P3) * y1 + tetax1 * x1 * x2P3) / div

            return CubicPolynomial(a,b,c,d)
        }

        /**
         * Calculates the parameters of a cubic polynomial that defines a smooth path defined in local frame
         * assuming that initial position is x1 = 0, y1 = 0 and angle relative to x axis is 0 (robot front is oriented in x axis direction)
         * @x2      x2,y2 defines the end point (destination of the path) in local frame
         * @y2
         * @angle2  the angle between the original orientation of the robot (that is always 0) and the final orientation (the turn angle)
         * @return  the parameters of the cubic polynomial that defines the smooth curve (c and d are always 0 because x1 = y1 = angle1 = 0)
         */
        fun newCubicPolynomialTo(x2: Double, y2: Double, angle2: Double): CubicPolynomial {
            // assumes that: referencial is robot center at Pose x1 = 0, y1 = 0, and angle is 0 (relative to x axis) => tan(0) = 0
            // x2 must be != 0

            val tan2   = Math.tan(angle2)
            val pow2x2 = x2 * x2
            val pow3x2 = pow2x2 * x2

            val a = (tan2 * x2     -2 * y2)      / pow3x2
            val b = (tan2 * pow2x2 -3 * y2 * x2) / pow3x2

            return CubicPolynomial(a,b,0.0,0.0)
        }

        /**
         * @vl    left  wheel linear velocity = 2 * PI * wheelRadius * left wheels angular velocity
         * @vl    right wheel linear velocity = 2 * PI * wheelRadius * right wheels angular velocity
         * @vteta robot angular velocity (given by gyro + acc + compass fusion)
         * @teta  robot orientation relative to global frame (calculated by integration of previous vteta * deltaT)
         * @n     parameter discovered by tests 0 < n < 1
         *
         * @return vx, vy    linear velocity relative to global frame (is it? confirm that the reference is global frame)
         */
        fun getLinearVelocity(vl: Double, vr: Double, vteta: Double, teta: Double, n: Double): DoubleVector2D {
            if (nearZero(vl,1E-30) && nearZero(vr,1E-30)) {
                return DoubleVector2D(0.0, 0.0)
            }
            if (!nearZero(vteta,1E-30)) {
                val sg = if (vl * vr > 0) 1.0 else -1.0
                val distanceWheel_vteta = distanceWheel * vteta

                if (!nearZero(vl,1E-30)) {
                    val nvr = if (vr == 0.0) 1E-30 else vr

                    val po = Math.pow(Math.abs(nvr / vl), n)
                    val po_sg = po * sg
                    val vr_po_sg_vl = nvr + po_sg * vl

                    val al = if (vr_po_sg_vl == 0.0) 0.0 else po_sg * (distanceWheel_vteta - nvr + vl) / vr_po_sg_vl
                    val ar = if (vr_po_sg_vl == 0.0) 0.0 else (nvr - vl - distanceWheel_vteta) / vr_po_sg_vl

                    val common = (nvr * (1 - ar) + vl * (1 - al)) / 2

                    val vx = common * Math.sin(teta)
                    val vy = common * Math.cos(teta)

                    return DoubleVector2D(vx, vy)
                }
                val nvl = if (vl == 0.0) 1E-30 else vl

                val po = Math.pow(Math.abs(nvl / vr), n)
                val po_sg = po * sg
                val vl_po_sg_vr = nvl + po_sg * vr

                val al = if (vl_po_sg_vr == 0.0) 0.0 else (distanceWheel_vteta - vr + nvl) / vl_po_sg_vr
                val ar = if (vl_po_sg_vr == 0.0) 0.0 else po_sg * (vr - nvl - distanceWheel_vteta) / vl_po_sg_vr

                val common = (vr * (1 - ar) + nvl * (1 - al)) / 2

                val vx = common * Math.sin(teta)
                val vy = common * Math.cos(teta)

                return DoubleVector2D(vx, vy)
            }
            val common = (vr + vl) / 2

            val vx = common * Math.sin(teta)
            val vy = common * Math.cos(teta)

            return DoubleVector2D(vx, vy)
        }

        /**
         *
         * return vl, vr
         */
        fun getWheelVelocities(vx: Double, vy: Double, vteta: Double, teta: Double, n: Double): DoubleVector2D {
            val vl = 0.0
            val vr = 0.0

            return DoubleVector2D(vl, vr)
        }
    }

    // https://en.wikipedia.org/wiki/Earth's_magnetic_field
    private val fusionAhrs = FusionAhrs(0.5f, 0.02f, 0.07f)
    private var previousTime = -1L

    private var compassBias = floatArrayOf(0.0f, 0.0f, 0.0f)
    private var compassScale = floatArrayOf(0.0f, 0.0f, 0.0f)
    private var factoryCompassCalibration = floatArrayOf(0.0f, 0.0f, 0.0f)
    var compassCalibrationDone = false

    init {
        App.getBroker().setListener(ReaderMessageType.MpuAndSpeedSensorsValues.header, this)
        App.getBroker().setListener(ReaderMessageType.CompassCalibrationValues.header, this)
    }

    var lastPose = Pose(0,0.0,0.0,0.0)

    override fun onMessage(message: Message) {
        if (message is CompassCalibrationValuesReader.CompassCalibrationValuesMessage) {
            compassBias = message.compassBias
            compassScale = message.compassScale
            factoryCompassCalibration = message.factoryCompassCalibration
            compassCalibrationDone = true
        } else if (message is MpuAndSpeedSensorsValuesReader.MpuAndSpeedSensorsValuesMessage) {

            if (lastPose.time != 0L) {
                val gyroscope = toDegreesPerSecond(message.gyroscope)
                val accelerometer = toG(message.accelerometer)
                val compass = toMilliT(message.compass)

                fusionAhrs.update(
                        gyroscope,
                        accelerometer,
                        compass,
                        (message.timeStamp - previousTime) / 1000.0f)

                if (!fusionAhrs.isInitialising()) {

                    //val linearAcceleration = fusionAhrs.getLinearAcceleration()
                    //val earthAcceleration = fusionAhrs.calculateEarthAcceleration()

                    //val roll = fusionAhrs.getRoll()
                    //val pitch = fusionAhrs.getPitch()
                    val yaw = fusionAhrs.getYaw() // angle? rotation? in x y plane

                    val vteta = (yaw - lastPose.angle) / (message.timeStamp - lastPose.time) // ??

                    //val earthDir = FusionCompass.calculateHeading(accelerometer, compass)

                    val leftVelocity  = (message.frontLeftSpeed  + message.backLeftSpeed)  / 2.0
                    val rightVelocity = (message.frontRightSpeed + message.backRightSpeed) / 2.0

                    val velocity = getLinearVelocity(leftVelocity, rightVelocity, vteta, yaw /* earthDir ? */,n)

                    App.getBroker().send(NewPoseMessage(
                            yaw /* earthDir ? */,
                            velocity.x * (message.timeStamp - lastPose.time),
                            velocity.y * (message.timeStamp - lastPose.time)))
                }
            }
            previousTime = message.timeStamp
        }
    }

    private fun getNewPose(rotation: Float, leftVelocity: Float, rightVelocity: Float) {


    }

    private fun toDegreesPerSecond(gyroscope: IntArray) = FloatArray(3) { i -> gyroscope[i] * 250.0f / 32768.0f }
    private fun toG(accelerometer: IntArray) = FloatArray(3) { i -> accelerometer[i] * 2.0f / 32768.0f }
    private fun toMilliT(compass: IntArray) =
            if (compassCalibrationDone)
                FloatArray(3) { i -> compass[i] * 49120.0f / 32760.0f * factoryCompassCalibration[i] - compassBias[i] }
            else FloatArray(3) { i -> compass[i] * 49120.0f / 32760.0f }
}