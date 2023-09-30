package com.swu.dimiz.ogg.ui.env.stamp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.StampData

class StampAdapter(val context: Context,
                   private val stampHolder: List<StampData>)
    : BaseAdapter() {

    override fun getCount(): Int = stampHolder.size

    override fun getItem(position: Int): StampData = stampHolder[position]

    override fun getItemId(p0: Int): Long = 0

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.layer_stamp_item, null)

        val item = stampHolder[position]
        val text: TextView = view.findViewById(R.id.text_stamp_date)

        text.text = item.sId.toString()

        return view
    }
}