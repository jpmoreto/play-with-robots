package jpm.android.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jpm.android.R
import jpm.android.ui.common.BaseFragment

class ConfigureFragment : BaseFragment() {

    override fun getName(context: Context): String = context.getString(R.string.section_configure)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_configure, container, false)

        val textDirection = rootView.findViewById(R.id.section_label_direction) as TextView
        textDirection.setTextColor(Color.BLACK)

        val textVelocity = rootView.findViewById(R.id.section_label_velocity) as TextView
        textVelocity.setTextColor(Color.BLACK)

        return rootView
    }
}
