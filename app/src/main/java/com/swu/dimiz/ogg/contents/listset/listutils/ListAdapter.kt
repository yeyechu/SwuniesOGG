package com.swu.dimiz.ogg.contents.listset.listutils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily

class ListAdapter(val context: Context,
private val dailyList: List<ActivitiesDaily>): BaseAdapter() {

    override fun getCount(): Int = dailyList.size

    override fun getItem(position: Int): ActivitiesDaily = dailyList[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val rootView: View = LayoutInflater.from(parent?.context).inflate(R.layout.fragment_listset_list_item, null)
        val item = dailyList[position]

        val image: ImageView = rootView.findViewById(R.id.image_activity)
        val text: TextView = rootView.findViewById(R.id.text_title)

        val card2: CardView = rootView.findViewById(R.id.card_view_car)
        val card1: CardView = rootView.findViewById(R.id.card_view)

        return rootView
    }
}