package com.swu.dimiz.ogg.ui.env.stamp

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.StampData

class StampAdapter(private val aim: Float,
                   private val stampHolder: List<StampData>)
    : BaseAdapter() {

    override fun getCount(): Int = stampHolder.size

    override fun getItem(position: Int): StampData = stampHolder[position]

    override fun getItemId(position: Int): Long = 0

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val rootView: View =
            LayoutInflater.from(parent?.context).inflate(R.layout.layer_stamp_item, null)

        val item = stampHolder[position]
        val text: TextView = rootView.findViewById(R.id.text_stamp_date)
        val image: ImageView = rootView.findViewById(R.id.image_stamp_face)

        when (item.today) {
            0 -> {
                // 아직 안 지난 날
                text.text = item.sId.toString()
                image.setImageResource(R.color.transparency_transparent)
            }
            1 -> {
                // 오늘
                text.text = item.sId.toString()
                text.setTextColor(Color.parseColor("#6897F3"))
                text.setBackgroundResource(R.drawable.env_shape_stamp_circle_stroke)
            }
            2 -> {
                // 이미 지난 날
                text.setBackgroundResource(R.color.transparency_transparent)
                when (item.sNumber / aim * 100) {
                    0f -> image.setImageResource(R.drawable.env_image_stamp_000)
                    in 1f..99f -> image.setImageResource(R.drawable.env_image_stamp_050)
                    else -> image.setImageResource(R.drawable.env_image_stamp_100)
                }
            }
        }
        return rootView
    }
}