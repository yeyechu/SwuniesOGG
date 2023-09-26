package com.swu.dimiz.ogg.ui.env.badges

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges

@BindingAdapter("badgeTitle")
fun TextView.setTitle(item: Badges?) {
    item?.let{
        text = item.title
    }
}

@BindingAdapter("badgeImage")
fun ImageView.setImage(item: Badges?) {
    item?.let {
        setImageBitmap(item.image)
    }
}

@BindingAdapter("badgeListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Badges>?) {
    val adapter = recyclerView.adapter as BadgeListAdapter
    adapter.submitList(data)
}