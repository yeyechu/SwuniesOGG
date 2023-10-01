package com.swu.dimiz.ogg.contents.listset

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.swu.dimiz.ogg.R

class NumberHolderAdapter(val context: Context,
                        private val numberHolder: List<NumberData>)
    : BaseAdapter() {

    override fun getCount(): Int = numberHolder.size

    override fun getItem(position: Int): NumberData = numberHolder[position]

    override fun getItemId(p0: Int): Long = 0

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.fragment_listset_list_item, null)

        val item = numberHolder[position]

        val text: TextView = view.findViewById(R.id.text_number)

        if (item.nId == position) {
            text.text = item.nNumber.toString()
        }

        return view
    }
}