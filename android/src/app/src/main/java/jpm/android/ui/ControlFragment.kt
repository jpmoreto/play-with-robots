package jpm.android.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jmedeisis.bugstick.Joystick
import com.jmedeisis.bugstick.JoystickListener
import jpm.android.App
import jpm.android.R
import jpm.android.messages.JoystickMessage
import jpm.android.navigation.JoystickNavigator
import jpm.android.ui.common.BaseFragment

class ControlFragment : BaseFragment() {

    override fun getName(context: Context): String = context.getString(R.string.section_control)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView =
                if(this.activity.resources.configuration.orientation ==  Configuration.ORIENTATION_LANDSCAPE)
                    inflater.inflate(R.layout.fragment_control_landscape, container, false)
                else
                    inflater.inflate(R.layout.fragment_control_portrait, container, false)

        val angleView = rootView.findViewById(R.id.tv_angle) as TextView
        val offsetView = rootView.findViewById(R.id.tv_offset) as TextView

        val bugView = rootView.findViewById(R.id.bugview) as BugView

        val angleNoneString   = getString(R.string.angle_value_none)
        val angleValueString  = getString(R.string.angle_value)
        val offsetNoneString  = getString(R.string.offset_value_none)
        val offsetValueString = getString(R.string.offset_value)

        val joystick = rootView.findViewById(R.id.joystick) as Joystick

        angleView.text = angleNoneString
        offsetView.text = offsetNoneString

        joystick.setJoystickListener(object : JoystickListener {
            override fun onDown() {}

            override fun onDrag(degrees: Float, offset: Float) {
                val v = JoystickNavigator.getVelocity(degrees,offset)
                angleView.text = String.format(angleValueString, degrees, v.first)
                offsetView.text = String.format(offsetValueString, offset, v.second)

                bugView.setAngleAndOffset(degrees, offset)
                App.getBroker().send(JoystickMessage(degrees, offset))
            }

            override fun onUp() {
                angleView.text = angleNoneString
                offsetView.text = offsetNoneString

                bugView.setAngleAndOffset(0f, 0f)
                App.getBroker().send(JoystickMessage(90f, 0f))
            }
        })

        return rootView
    }
}

class BugView : View {

    private val backGroundPaint = Paint()
    private val paint = Paint()
    private val dirPaint = Paint()

    private var width_: Int = 0
    private var height_: Int = 0

    private var degrees: Float = 0f
    private var offset: Float = 0f

    private val path = Path()
    private val verticalPath = Path()
    private val horizontalPath = Path()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        Log.i("BugView","init")
        backGroundPaint.color = Color.GRAY
        backGroundPaint.style = Paint.Style.FILL_AND_STROKE

        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f

        dirPaint.color = Color.LTGRAY
        dirPaint.style = Paint.Style.STROKE
        dirPaint.strokeWidth = 1f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width_ = w
        height_ = h

        verticalPath.rewind()
        verticalPath.moveTo((width_ / 2).toFloat(), 0f)
        verticalPath.lineTo((width_ / 2).toFloat(), height_.toFloat())

        horizontalPath.rewind()
        horizontalPath.moveTo(0f, (height_ / 2).toFloat())
        horizontalPath.lineTo(width_.toFloat(), (height_ / 2).toFloat())
    }

    fun setAngleAndOffset(degrees: Float, offset: Float) {
        this.degrees = degrees
        this.offset = offset
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val deltaX = (width_ / 2).toFloat()
        val deltaY = (height_ / 2).toFloat()
        val size = offset * (deltaY - 2)

        path.rewind()
        canvas.drawCircle(deltaX,deltaY,deltaY,backGroundPaint)

        canvas.drawPath(verticalPath, dirPaint)
        canvas.drawPath(horizontalPath, dirPaint)

        if (-0.000001 > offset || offset > 0.000001) {
            path.moveTo(deltaX, deltaY)
            path.lineTo(deltaX + size, deltaY)
            path.lineTo(deltaX + size - 10, deltaY - 10)
            path.moveTo(deltaX + size, deltaY)
            path.lineTo(deltaX + size - 10, deltaY + 10)

            canvas.rotate(-degrees, deltaX, deltaY)

            canvas.drawPath(path, paint)
        }
    }
} // end class BugView
