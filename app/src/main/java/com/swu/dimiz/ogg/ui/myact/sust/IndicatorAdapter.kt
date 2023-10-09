package com.swu.dimiz.ogg.ui.myact.sust

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.swu.dimiz.ogg.R

class IndicatorAdapter (val context: Context,
                        private val dotList: List<Int>)
    : BaseAdapter() {

    override fun getCount(): Int = dotList.size

    override fun getItem(position: Int): Int = dotList[position]

    override fun getItemId(position: Int): Long = 0

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val rootView: View =
            LayoutInflater.from(parent?.context).inflate(R.layout.item_indicator, null)

        val item = dotList[position]
        val image: ImageView = rootView.findViewById(R.id.indicator1)


        return rootView
    }
}