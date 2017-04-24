package jpm.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jpm.android.App
import jpm.android.R
import jpm.android.robot.Robot
import jpm.android.ui.common.BaseFragment
import jpm.android.ui.common.ZoomAndMoveView
import jpm.messages.MessageType
import jpm.messages.UsArrayDistancesMessage

class RobotFragment : BaseFragment() {

    override fun getName(context: Context): String = context.getString(R.string.section_robot)

    var robotView: RobotView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_robot, container, false)

        robotView = rootView.findViewById(R.id.robotview) as RobotView

        return rootView
    }

    override fun onResume() {
        App.getBroker().setListener(MessageType.UsArrayDistances.header, this)
        super.onResume()
    }

    override fun onPause() {
        App.getBroker().removeListener(MessageType.UsArrayDistances.header,this)
        super.onPause()
    }

    override fun handleMessage(message: jpm.lib.comm.Message) {
        if(message is UsArrayDistancesMessage) {
            robotView!!.setPoseAndUSSensors(456.1f, 3536.33f, 45.3f, message.distances)
        }
    }
}

class RobotView : ZoomAndMoveView {

    private val robotScale = 1000f
    private var xPos = 0.0f
    private var yPos = 0.0f
    private var degrees = 0f
    private var sensors =  intArrayOf(0,0,0,0,0,0,0,0,0,0,0,0)

    private val paint = Paint()
    private val dirPaint = Paint()

    private var deltaX = 0.0f
    private var deltaY = 0.0f

    private var robot = Path()
    private val robotPaint = Paint()
    private val textPaint = Paint()

    private val scalRobotPath = Path()

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

        paint.color = Color.GRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f

        dirPaint.color = Color.RED
        dirPaint.style = Paint.Style.STROKE
        dirPaint.strokeWidth = 1f

        robotPaint.color = Color.BLUE
        robotPaint.style = Paint.Style.STROKE
        robotPaint.strokeWidth = 4f

        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.STROKE
        textPaint.strokeWidth = 1f
        textPaint.textSize = 30f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        deltaX = w / 2.0f
        deltaY = h / 2.0f

        robot = Robot.getRobot(deltaX,deltaY,robotScale)
    }

    fun setPoseAndUSSensors(xPos: Float, yPos: Float, degrees: Float, sensors: IntArray) {
        this.xPos = xPos
        this.yPos = yPos
        this.degrees = degrees
        this.sensors = sensors
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val deltaXScaled = deltaX  / mScaleFactor
        val deltaYScaled = deltaY  / mScaleFactor

        val centerDeltaX = deltaXScaled-deltaX
        val centerDeltaY = deltaYScaled-deltaY

        canvas.drawText(String.format("X = %+7.1f  Y = %+7.1f  Compass = %+7.1fº", xPos, yPos, degrees),20f ,40f ,textPaint)

        canvas.save()
        canvas.scale(mScaleFactor, mScaleFactor)
        canvas.translate(mPosX / mScaleFactor, mPosY / mScaleFactor)

        scalRobotPath.rewind()
        scalRobotPath.addPath(robot,centerDeltaX,centerDeltaY)

        canvas.drawPath(scalRobotPath, robotPaint)

        for(i in 0..11) {
            Robot.drawUsRead(deltaXScaled,deltaYScaled,mScaleFactor * robotScale,i,sensors[i] / 2,paint,canvas)
        }
        canvas.restore()

    }
} // end class RobotView
