package jpm.android.messages

import jpm.lib.comm.Message
import jpm.lib.comm.MessageType

class ChangeGraphVisibility(time: Long, val itemId: Int, val checked: Boolean): Message(MessageType.ChangeGraphVisibility.header, 10, time)
class JoystickMessage(time: Long,val degrees: Float, val offset: Float): Message(MessageType.Joystick.header,10,time)
class NewPoseMessage(time: Long,val degrees: Double, val x: Double, val y: Double): Message(MessageType.NewPose.header, 10, time)

