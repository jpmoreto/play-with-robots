package jpm.android.navigation

import jpm.android.App
import jpm.android.com.Message
import jpm.android.com.MessageListener
import jpm.android.messages.CommandVelocityMessageWriter
import jpm.android.messages.InternalMessageType
import jpm.android.messages.JoystickMessage
import jpm.android.messages.WriterMessageType

/**
 * Created by jm on 20/02/17.
 *
 */

/*
usando wxmaxima

load(fourier_elim)$

inputs:
  v - velocidade
  d - angulo em graus

output:
  vl - velocidade do motor esquerdo
  vr - velocidade do motor direito

para todos os casos: (|vl| + |vr|)/2 = v

1º Octante 0º .. 45º vr(45º) = 0   vr(0º) = -vl ------------------------------------------------

solve([(-vr+vl)/2 = v, vl * (d - 45)/45 = vr],[vl,vr]);

=> val vl = -(90*v)/(d-90)
   val vr = -((2*d-90)*v)/(d-90)

2º Octante 45º .. 90º vr(45º) = 0   vr(90º) = vl ------------------------------------------------

solve([(vr+vl)/2 = v, vl * (d - 45)/45 = vr],[vl,vr]);

=>  val vl = (90*v)/d
    val vr = ((2*d-90)*v)/d

3º Octante 90º .. 135º vl(90º) = vl  vl(135º) = 0 ------------------------------------------------

solve([(vr+vl)/2 = v, vr * (135 - d)/45 = vl],[vl,vr]);

=>  val vl = ((2*d-270)*v)/(d-180)
    val vr = -(90*v)/(d-180)

4º Octante 135º .. 180º  vl(135º) = 0  vl(180º) = -vr ------------------------------------------------

solve([(vr-vl)/2 = v, vr * (d - 135)/45 = -vl],[vl,vr]);

=>  val vl = -((2*d-270)*v)/(d-90)
    val vr = (90*v)/(d-90)

5º Octante -180º .. -135º  vr(-180º) = -vl  vr(-135º) = 0 ------------------------------------------------

solve([(vr-vl)/2 = v, vl * (-135 - d)/45 = -vr],[vl,vr]);

=>  val vl = (90*v)/(d+90)
    val vr = ((2*d+270)*v)/(d+90)

6º Octante -135º .. -90º vr(-135º) = 0  vr(-90º) = vl -------------------------------------------------

solve([(vr+vl)/2 = -v, vl * (135 + d)/45 = vr],[vl,vr]);

=>  val vl = -(90*v)/(d+180)
    val vr = -((2*d+270)*v)/(d+180)

7º Octante -90º .. -45º  vl(-90º) = vr  vl(-45º) = 0 ------------------------------------------------

solve([(vr+vl)/2 = -v, vr * (45 + d)/45 = -vl],[vl,vr]);

=>  val vl = -((2*d+90)*v)/d
    val vr = (90*v)/d

8º Octante -45º .. 0º vl(-45º) = 0 vl(0º) = -vr ------------------------------------------------

solve([(vl-vr)/2 = v, vr * (d + 45)/45 = -vl],[vl,vr]);

=>  val vl = ((2*d+90)*v)/(d+90)
    val vr = -(90*v)/(d+90)

 */
class JoystickNavigator: MessageListener {

    init {
        App.getBroker().setListener(InternalMessageType.Joystick.header, this)
    }

    override fun onMessage(message: Message) {
        if (message is JoystickMessage) {
            val v = getVelocity(message.degrees,message.offset)
            App.getBroker().send(WriterMessageType.COMMAND_VELOCITY.header,
                    CommandVelocityMessageWriter(v.first,v.second))
        }
    }

    companion object {
        fun getVelocity(d: Float, v:Float): Pair<Int,Int> {
            // calculate left and right speed
            var vl = 0f
            var vr = 0f

            if      (   0f <= d && d <  45f)  { vl = -(90*v)/(d-90);         vr = -((2*d-90)*v)/(d-90)   }  // 1º
            else if (  45f <= d && d <  90f)  { vl =  (90*v)/d;              vr =  ((2*d-90)*v)/d        }  // 2º
            else if (  90f <= d && d <  135f) { vl =  ((2*d-270)*v)/(d-180); vr = -(90*v)/(d-180)        }  // 3º
            else if ( 135f <= d && d <= 180f) { vl = -((2*d-270)*v)/(d-90);  vr =  (90*v)/(d-90)         }  // 4º
            else if (-180f <= d && d < -135f) { vl =  (90*v)/(d+90);         vr =  ((2*d+270)*v)/(d+90)  }  // 5º
            else if (-135f <= d && d < -90f)  { vl = -(90*v)/(d+180);        vr = -((2*d+270)*v)/(d+180) }  // 6º
            else if ( -90f <= d && d < -45f)  { vl = -((2*d+90)*v)/d;        vr =  (90*v)/d              }  // 7º
            else if ( -45f <= d && d <  0f)   { vl = ((2*d+90)*v)/(d+90);    vr = -(90*v)/(d+90)         }  // 8º

            if(vl > 1.0f || vl < -1.0f) {
                val f = 1.0f / Math.abs(vl)

                vl *= f
                vr *= f
            }
            else if(vr > 1.0f || vr < -1.0f) {
                val f = 1.0f / Math.abs(vr)

                vl *= f
                vr *= f
            }

            return Pair(Math.round(vl * App.maxVelocity), Math.round(vr * App.maxVelocity))
        }
    }
}




