package jpm.android.messages

import jpm.android.com.Message

enum class InternalMessageType(val header: Byte) {
    ChangeGraphVisibility(10),
    Joystick(11),
    NewPose(12)
}

class ChangeGraphVisibility(val itemId: Int, val checked: Boolean): Message(InternalMessageType.ChangeGraphVisibility.header)
class JoystickMessage(val degrees: Float, val offset: Float): Message(InternalMessageType.Joystick.header)
class NewPoseMessage(val degrees: Double, val x: Double, val y: Double): Message(InternalMessageType.NewPose.header)

