package com.swu.dimiz.ogg.contents.listset.listutils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.ListData

class ListHolderAdapter(val context: Context,
                        private val listHolder: List<ListData>)
    : BaseAdapter() {

    override fun getCount(): Int = listHolder.size

    override fun getItem(position: Int): ListData = listHolder[position]

    override fun getItemId(p0: Int): Long = 0

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val rootView: View = LayoutInflater.from(parent?.context).inflate(R.layout.fragment_listset_list_holder, null)

        val item = listHolder[position]

        val image: ImageView = rootView.findViewById(R.id.image_list_holder)
        val text: TextView = rootView.findViewById(R.id.text_list_holder)
        val badgeText: TextView = rootView.findViewById(R.id.text_list_holder_badge)

        if (item.aId > 0) {
            text.text = ""
            badgeText.visibility = View.VISIBLE
            badgeText.text = item.aNumber.toString()
            text.setBackgroundResource(R.drawable.listset_shape_listholder_stroke)
            setImage(item.aId, image)
        } else {
            text.text = (position + 1).toString()
            badgeText.visibility = View.GONE
            text.setBackgroundResource(R.drawable.listset_shape_listholder_dash)
            image.setImageResource(R.color.transparency_transparent)
        }
        return rootView
    }
}

private fun setImage(id: Int, imageView: ImageView) {
    when(id) {
        10001 -> imageView.setImageResource(R.drawable.daily_10001)
        10002 -> imageView.setImageResource(R.drawable.daily_10002)
        10003 -> imageView.setImageResource(R.drawable.daily_10003)
        10004 -> imageView.setImageResource(R.drawable.daily_10004)
        10005 -> imageView.setImageResource(R.drawable.daily_10005)
        10006 -> imageView.setImageResource(R.drawable.daily_10006)
        10007 -> imageView.setImageResource(R.drawable.daily_10007)
        10008 -> imageView.setImageResource(R.drawable.daily_10008)
        10009 -> imageView.setImageResource(R.drawable.daily_10009)
        10010 -> imageView.setImageResource(R.drawable.daily_10010)
        10011 -> imageView.setImageResource(R.drawable.daily_10011)
        10012 -> imageView.setImageResource(R.drawable.daily_10012)
        10013 -> imageView.setImageResource(R.drawable.daily_10013)
        10014 -> imageView.setImageResource(R.drawable.daily_10014)
        10015 -> imageView.setImageResource(R.drawable.daily_10015)
        10016 -> imageView.setImageResource(R.drawable.daily_10016)
        10017 -> imageView.setImageResource(R.drawable.daily_10017)
        10018 -> imageView.setImageResource(R.drawable.daily_10018)
        10019 -> imageView.setImageResource(R.drawable.daily_10019)
        10020 -> imageView.setImageResource(R.drawable.daily_10020)
    }

}