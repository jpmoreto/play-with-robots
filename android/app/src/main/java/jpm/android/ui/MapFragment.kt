package jpm.android.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jpm.android.R
import jpm.android.ui.common.BaseFragment

class MapFragment : BaseFragment() {

    override fun getName(context: Context): String = context.getString(R.string.section_map)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_map, container, false)
        val textView = rootView.findViewById(R.id.section_label) as TextView
        textView.text = getString(R.string.section_format, "MapFragment")
        return rootView
    }
}
