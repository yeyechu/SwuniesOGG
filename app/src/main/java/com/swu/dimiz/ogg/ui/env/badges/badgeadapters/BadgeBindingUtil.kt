package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.convertLongToDateString
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges

@BindingAdapter("badgeTitle")
fun TextView.setTitle(item: Badges?) {
    item?.let{
        text = item.title
    }
}

@BindingAdapter("badgeCount")
fun TextView.setCount(item: Badges?) {
    item?.let{
        text = if(item.getDate != null) {
            resources.getString(R.string.badgelist_detail_date, convertLongToDateString(item.getDate!!))
        } else if(item.filter == "co2") {
            resources.getString(R.string.badgelist_detail_count_co2, item.count/1000f, item.baseValue/1000)
        } else {
            resources.getString(R.string.badgelist_detail_count, item.count, item.baseValue)
        }
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