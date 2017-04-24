package jpm.android.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidplot.xy.*
import jpm.android.App
import jpm.android.R
import jpm.android.ui.common.BaseFragment
import jpm.messages.ChangeGraphVisibility
import jpm.messages.MessageType
import jpm.messages.MotorsSpeedMessage
import jpm.messages.MpuSensorsValuesMessage

class ChartFragment : BaseFragment() {

    override fun getName(context: Context): String = context.getString(R.string.section_chart)

    private var data: DataSource? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.i(TAG, "onCreateView "+this.hashCode())

        super.onCreate(savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_chart, container, false)

        // get handles to our View defined in layout.xml:
        val dynamicPlot = rootView.findViewById(R.id.dynamicXYPlot) as XYPlot

        data = getDataSource(dynamicPlot)

        return rootView
    }

    override fun onResume() {
        Log.i(TAG, "onResume "+this.hashCode())
        registerListeners()
        super.onResume()
    }

    override fun onPause() {
        Log.i(TAG, "onPause "+this.hashCode())
        removeListeners()
        super.onPause()
    }

    fun registerListeners() {
        App.getBroker().setListener(MessageType.MpuSensorsValues.header,this)
        App.getBroker().setListener(MessageType.MotorsSpeed.header,this)
        App.getBroker().setListener(MessageType.ChangeGraphVisibility.header, this)
    }

    fun removeListeners() {
        App.getBroker().removeListener(MessageType.MpuSensorsValues.header,this)
        App.getBroker().removeListener(MessageType.MotorsSpeed.header,this)
        App.getBroker().removeListener(MessageType.ChangeGraphVisibility.header,this)
    }

    override fun onMessage(message: jpm.lib.comm.Message) {
        if(message is MpuSensorsValuesMessage) {
            data!!.addDataPoint(0,message.time,message.accelerometer[0])
            data!!.addDataPoint(1,message.time,message.accelerometer[1])
            data!!.addDataPoint(2,message.time,message.accelerometer[2])

            data!!.addDataPoint(3,message.time,message.gyroscope[0])
            data!!.addDataPoint(4,message.time,message.gyroscope[1])
            data!!.addDataPoint(5,message.time,message.gyroscope[2])

            data!!.addDataPoint(6,message.time,message.compass[0])
            data!!.addDataPoint(7,message.time,message.compass[1])
            data!!.addDataPoint(8,message.time,message.compass[2])
            data!!.redraw()
        } else if(message is MotorsSpeedMessage) {
            data!!.addDataPoint(9,message.time,message.frontLeftSpeed)
            data!!.addDataPoint(10,message.time,message.frontRightSpeed)
            data!!.addDataPoint(11,message.time,message.backLeftSpeed)
            data!!.addDataPoint(12,message.time,message.backRightSpeed)
            data!!.redraw()
        } else if(message is ChangeGraphVisibility) {
            data!!.show(message.itemId, message.checked)
        }
    }

    companion object {
        private val TAG = "ChartFragment"

        private var dataSource: DataSource? = null

        private fun getDataSource(plot: XYPlot): DataSource {
            var ds = dataSource

            if(ds == null) {
                ds = DataSource(plot)
                addTimeSeries(ds)
            } else {
                //val oldPlot = ds.setPlot(plot)
                addTimeSeries(ds)
            }
            dataSource = ds
            return ds
        }

        private fun addTimeSeries(data: DataSource) {
            data.addTimeSeries(0, "accX", Color.BLUE)
            data.addTimeSeries(1, "accY", Color.RED)
            data.addTimeSeries(2, "accZ", Color.GREEN)
            data.addTimeSeries(3, "girX", Color.BLACK)
            data.addTimeSeries(4, "girY", Color.MAGENTA)
            data.addTimeSeries(5, "girZ", Color.YELLOW)
            data.addTimeSeries(6, "cmpX", Color.CYAN)
            data.addTimeSeries(7, "cmpY", Color.DKGRAY)
            data.addTimeSeries(8, "cmpY", Color.WHITE)

            data.addTimeSeries(9, "fl_s", 0xFFFF8400.toInt())
            data.addTimeSeries(10, "fr_s", 0xFFFF4400.toInt())
            data.addTimeSeries(11, "bl_s", 0xFFFF8888.toInt())
            data.addTimeSeries(12, "br_s", 0xFFFF8844.toInt())
        }
    }

    internal class DataSource(private var plot: XYPlot) {
        init {
            Log.i(TAG, "DataSource init " + this.hashCode() + " plot " + plot.hashCode())
        }

        fun setPlot(plot: XYPlot): XYPlot {
            val oldPlot = this.plot
            this.plot = plot
            init()
            return oldPlot
        }

        private class Sample(val maxSize: Int) {
            private var sampleSize = 0
            private var readPos    = 0
            private var writePos   = 0
            private val sample     = IntArray(maxSize, { 0 })
            private val time       = LongArray(maxSize, { 0 })

            private fun incRotate(p: Int): Int = if (p < maxSize - 1) p + 1 else 0

            val size: Int
               get() = synchronized(this) {sampleSize}

            fun addValue(v: Int, t: Long) {
                synchronized(this) {
                    if (readPos == writePos && sampleSize > 0) {
                        readPos = incRotate(readPos)
                    }

                    sample[writePos] = v
                    time[writePos] = t

                    writePos = incRotate(writePos)

                    if (sampleSize < maxSize) ++sampleSize
                }
            }

            fun getValue(i: Int): Int = synchronized(this) { sample[(readPos + i) % maxSize] }
            fun getTime(i: Int): Long = synchronized(this) { time[(readPos + i) % maxSize] }
            fun removeUntil(maxMinTime: Long) {
                synchronized(this) {
                    while(sampleSize > 0 && time[readPos] < maxMinTime) {
                        //Log.i(TAG, "removeUntil: maxMinTime = $maxMinTime; time[readPos] = ${time[readPos]}; sampleSize = $sampleSize")
                        readPos = incRotate(readPos)
                        --sampleSize
                    }
                }
            }
            fun  minTime(): Long = synchronized(this) { if(sampleSize == 0) Long.MIN_VALUE else time[readPos] }
        }

        private val sample    = Array(13) { Sample(SAMPLE_WINDOW_SIZE) }

        private val series = arrayOfNulls<Pair<TimeSeries, LineAndPointFormatter>>(13)

        private fun init() {
            plot.setRangeBoundaries(-1000, 1000, BoundaryMode.FIXED)

            plot.setRangeStep(StepMode.SUBDIVIDE, 20.0)
            plot.setDomainStep(StepMode.SUBDIVIDE, 20.0)

            plot.setDomainBoundaries(0, SAMPLE_WINDOW_SIZE - 1, BoundaryMode.AUTO)

            // para mostrar os as legendas dos eixos é necessário mudar o style no xml
        }

        init {
            init()
        }

        fun addTimeSeries(seriesIndex: Int, label: String, color: Int) {
            val timeSeries = TimeSeries(this, seriesIndex, label)

            val formatter = LineAndPointFormatter(color, null, null, null)
            formatter.linePaint.strokeWidth = 5f
            formatter.linePaint.strokeJoin = Paint.Join.ROUND

            series[seriesIndex] = Pair(timeSeries,formatter)
            plot.addSeries(timeSeries, formatter)
        }

        fun addDataPoint(index: Int, timeStamp: Long, value: Int) {
            sample[index].addValue(value,timeStamp)
        }

        fun redraw() {
            //alignFirstDate(sample)
            plot.redraw()
        }

        /*
        private fun alignFirstDate(sample: Array<Sample>) {
            val maxMinTime = sample.map { s -> s.minTime() }.max()?: 0L
            sample.forEach { s -> s.removeUntil(maxMinTime) }
        }
        */
        fun itemCount(series: Int): Int = sample[series].size

        fun getX(series: Int, index: Int): Number {
            if (index >= sample[series].size) {
                throw IllegalArgumentException()
            }
            return sample[series].getTime(index)
        }

        fun getY(series: Int, index: Int): Number {
            if (index >= sample[series].size) {
                throw IllegalArgumentException()
            }
            return sample[series].getValue(index)
        }

        companion object {
            val SAMPLE_WINDOW_SIZE = 100
        }

        fun  show(itemId: Int, checked: Boolean) {
           if(itemId in series.indices) {
               if(checked) {
                   plot.addSeries(series[itemId]!!.first,series[itemId]!!.second)
               }
               else plot.removeSeries(series[itemId]!!.first)
           }
        }
    }

    internal class TimeSeries(private val dataSource: DataSource, private val seriesIndex: Int, private val title: String) : XYSeries {

        override fun getTitle()       = title
        override fun size()           = dataSource.itemCount(seriesIndex)
        override fun getX(index: Int) = dataSource.getX(seriesIndex, index)
        override fun getY(index: Int) = dataSource.getY(seriesIndex, index)
    }

}
