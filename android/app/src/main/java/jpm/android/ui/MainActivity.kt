package jpm.android.ui


import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import jpm.android.App
import jpm.messages.ChangeGraphVisibility
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnItemSelectedListener {

    private class SectionsPagerAdapter(fm: FragmentManager, private val context: Context) : FragmentPagerAdapter(fm) {

        private val fragments = arrayOf(ChartFragment(), LogsFragment(), ControlFragment(), ConfigureFragment(), MapFragment(), RobotFragment())

        override fun getItem(position: Int): Fragment = fragments[position]
        override fun getPageTitle(position: Int): CharSequence = fragments[position].getName(context)
        override fun getCount(): Int                           = fragments.size // number of tabs.
    }

    private var sensors: Map<String,Pair<Int,Int>>? = null
    private var optionsMenu: Menu? = null

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private fun initSensors() {
        sensors = mapOf(
            getString(jpm.android.R.string.sel_chart_accelerometer_x) to Pair(0, jpm.android.R.id.sel_chart_accelerometer_x),
            getString(jpm.android.R.string.sel_chart_accelerometer_y) to Pair(1, jpm.android.R.id.sel_chart_accelerometer_y),
            getString(jpm.android.R.string.sel_chart_accelerometer_z) to Pair(2, jpm.android.R.id.sel_chart_accelerometer_z),
            getString(jpm.android.R.string.sel_chart_gyroscope_x) to Pair(3, jpm.android.R.id.sel_chart_gyroscope_x),
            getString(jpm.android.R.string.sel_chart_gyroscope_y) to Pair(4, jpm.android.R.id.sel_chart_gyroscope_y),
            getString(jpm.android.R.string.sel_chart_gyroscope_z) to Pair(5, jpm.android.R.id.sel_chart_gyroscope_z),
            getString(jpm.android.R.string.sel_chart_compass_x) to Pair(6, jpm.android.R.id.sel_chart_compass_x),
            getString(jpm.android.R.string.sel_chart_compass_y) to Pair(7, jpm.android.R.id.sel_chart_compass_y),
            getString(jpm.android.R.string.sel_chart_compass_z) to Pair(8, jpm.android.R.id.sel_chart_compass_z),

            getString(jpm.android.R.string.sel_chart_motor_front_left) to Pair(9, jpm.android.R.id.sel_chart_motor_front_left),
            getString(jpm.android.R.string.sel_chart_motor_front_right) to Pair(10, jpm.android.R.id.sel_chart_motor_front_right),
            getString(jpm.android.R.string.sel_chart_motor_back_left) to Pair(11, jpm.android.R.id.sel_chart_motor_back_left),
            getString(jpm.android.R.string.sel_chart_motor_back_right) to Pair(12, jpm.android.R.id.sel_chart_motor_back_right)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        App.readConfiguration(getPreferences(Context.MODE_PRIVATE))

        setUiVisibility()

        setContentView(jpm.android.R.layout.activity_main)
        initSensors()

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, this)
        this.container.offscreenPageLimit = mSectionsPagerAdapter!!.count
        this.container.adapter = mSectionsPagerAdapter

        // disable lateral scrolling
        this.container.setOnTouchListener { _, _ -> true }

        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        spinner.adapter = ArrayAdapter<CharSequence>(
                toolbar.context,
                android.R.layout.simple_list_item_1,
                Array(mSectionsPagerAdapter!!.count, { i ->  mSectionsPagerAdapter!!.getPageTitle(i)}))

        spinner.onItemSelectedListener = this
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        App.init()
        App.getBroker() // init Bluetooth connection
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setUiVisibility()
        }
    }

    private fun setUiVisibility() {
        window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        App.writeConfiguration(getPreferences(Context.MODE_PRIVATE))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        optionsMenu = menu
        menuInflater.inflate(jpm.android.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        fun activateGraph(index: Int, activate: Boolean): Boolean {
            Log.i("MainActivity","activateGraph($index,$activate)")
            App.getBroker().send(ChangeGraphVisibility(System.currentTimeMillis(),index,activate))
            return true
        }

        fun activateGraph(index: Int): Boolean {
            item.isChecked = !item.isChecked
            return activateGraph(index,item.isChecked)
        }

        fun activateGraph(title: String, menu: SubMenu?, activate: Boolean) {
            sensors!!.forEach { e ->
                val menuItem = menu!!.findItem(e.value.second)
                Log.i("MainActivity","activateGraph($title,${menu.size()},$activate,${e.value.second}) menuItem = $menuItem")
                if(menuItem != null) {
                    menuItem.isChecked = activate
                    activateGraph(sensors!![menuItem.title.toString()]!!.first,activate)
                }
            }
        }

        when (item.itemId) {
            jpm.android.R.id.action_settings                         -> {
                Snackbar.make(findViewById(jpm.android.R.id.toolbar), "Menu item ${item.title} called", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                return true
            }
            jpm.android.R.id.action_quit                             -> {
                Snackbar.make(findViewById(jpm.android.R.id.toolbar), "Menu item ${item.title} called", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                this.finishAffinity()
                return true
            }
            jpm.android.R.id.action_stop                             -> {
                Snackbar.make(findViewById(jpm.android.R.id.toolbar), "Menu item ${item.title} called", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                return true
            }
            jpm.android.R.id.action_select_all                       -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(2).title.toString(),group.getItem(2).subMenu,true)
                activateGraph(group.getItem(3).title.toString(),group.getItem(3).subMenu,true)
                activateGraph(group.getItem(4).title.toString(),group.getItem(4).subMenu,true)
                activateGraph(group.getItem(5).title.toString(),group.getItem(5).subMenu,true)
                return true
            }
            jpm.android.R.id.action_unselect_all                     -> {
                Log.i("MainActivity","action_unselect_all(${optionsMenu!!.getItem(2).title})")
                val group = optionsMenu!!.getItem(2).subMenu
                Log.i("MainActivity","action_unselect_all getItem(2) = ${group!!.getItem(2).title}")
                activateGraph(group.getItem(2).title.toString(),group.getItem(2).subMenu,false)
                activateGraph(group.getItem(3).title.toString(),group.getItem(3).subMenu,false)
                activateGraph(group.getItem(4).title.toString(),group.getItem(4).subMenu,false)
                activateGraph(group.getItem(5).title.toString(),group.getItem(5).subMenu,false)
                return true
            }
            jpm.android.R.id.action_select_all_motor_graph           -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(2).title.toString(),group.getItem(2).subMenu,true)
                return true
            }
            jpm.android.R.id.action_unselect_all_motor_graph         -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(2).title.toString(),group.getItem(2).subMenu,false)
                return true
            }
            jpm.android.R.id.action_select_all_accelerometer_graph   -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(3).title.toString(),group.getItem(3).subMenu,true)
               return true
            }
            jpm.android.R.id.action_unselect_all_accelerometer_graph -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(3).title.toString(),group.getItem(3).subMenu,false)
                return true
            }
            jpm.android.R.id.action_select_all_gyroscope_graph       -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(4).title.toString(),group.getItem(4).subMenu,true)
                return true
            }
            jpm.android.R.id.action_unselect_all_gyroscope_graph     -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(4).title.toString(),group.getItem(4).subMenu,false)
                return true
            }
            jpm.android.R.id.action_select_all_compass_graph         -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(5).title.toString(),group.getItem(5).subMenu,true)
                return true
            }
            jpm.android.R.id.action_unselect_all_compass_graph       -> {
                val group = optionsMenu!!.getItem(2).subMenu
                activateGraph(group!!.getItem(5).title.toString(),group.getItem(5).subMenu,false)
                return true
            }
            else                                                     -> {
                if(sensors!!.containsKey(item.title.toString())) {
                    return activateGraph(sensors!![item.title.toString()]!!.first)
                }
                Log.i("MainActivity","onOptionsItemSelected(${item.title},${item.isChecked})")
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =
        this.container.setCurrentItem(position,false)
}
