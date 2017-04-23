package jpm.android.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jpm.android.R
import jpm.android.ui.common.BaseFragment

class LogsFragment : BaseFragment() {

    override fun getName(context: Context): String = context.getString(R.string.section_logs)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_logs, container, false)
        val textView = rootView.findViewById(R.id.section_label) as TextView
        textView.text = getString(R.string.section_format, "LogsFragment")
        return rootView
    }
}
