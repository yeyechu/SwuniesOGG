package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.env.myenv.InventoryAdapter

@BindingAdapter("badgeTitle")
fun TextView.setTitle(item: Badges?) {
    item?.let{
        text = item.title
    }
}

@BindingAdapter("badgeDetailImage")
fun detailImageSet(imageView: ImageView, data: Badges?) {
    data?.let {
        when(data.getDate) {
            null -> imageView.setImageBitmap(data.image.apply {
                imageView.setColorFilter(Color.parseColor("#F56A6A6A"))
                imageView.setBackgroundResource(R.drawable.badgelist_shape_badge_background_sealed)
            })
            else -> imageView.setImageBitmap(data.image)
        }
    }
}

@BindingAdapter("badgeImage")
fun ImageView.setImage(item: Badges?) {
    item?.let {
        when(item.getDate) {
            null -> setImageBitmap(item.image.apply {
                setColorFilter(Color.parseColor("#F56A6A6A"))
                setBackgroundResource(R.drawable.badgelist_shape_badge_background_sealed)
            })
            else -> setImageBitmap(item.image)
        }
    }
}

@BindingAdapter("badgeInventory")
fun bindRecyclerInventory(recyclerView: RecyclerView, data: List<Badges>?) {
    val adapter = recyclerView.adapter as InventoryAdapter
    adapter.submitList(data)
}
