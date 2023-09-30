package com.swu.dimiz.ogg.contents.listset

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.ui.env.stamp.setImage

class ListHolderAdapter(val context: Context,
                        private val listHolder: List<ListData>)
    : BaseAdapter() {

    override fun getCount(): Int = listHolder.size

    override fun getItem(position: Int): ListData = listHolder[position]

    override fun getItemId(p0: Int): Long = 0

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.fragment_listset_list_holder, null)

        val item = listHolder[position]

        val image: ImageView = view.findViewById(R.id.image_list_holder)
        val text: TextView = view.findViewById(R.id.text_list_holder)

        if (item.aId > 0) {
            text.text = ""
            text.setBackgroundResource(R.drawable.listset_shape_listholder_stroke)
            image.setImageResource(R.drawable.feed_button_reaction_like)
        } else {
            text.text = (position + 1).toString()
            text.setBackgroundResource(R.drawable.listset_shape_listholder_dash)
            image.setImageResource(R.color.transparency_transparent)
        }

        return view
    }
}