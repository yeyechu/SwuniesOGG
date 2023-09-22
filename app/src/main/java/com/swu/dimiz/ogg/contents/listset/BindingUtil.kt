package com.swu.dimiz.ogg.contents.listset

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily

@BindingAdapter("actitityTitle")
fun TextView.setTitle(item: ActivitiesDaily?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("activityCo2")
fun TextView.setCo2(item: ActivitiesDaily?) {
    item?.let {
        text = "탄소감축량"+ item.co2.toString() + "kg"
    }
}

@BindingAdapter("activityImage")
fun ImageView.setImage(item: ActivitiesDaily?) {
    item?.let {
        setImageBitmap(item.image)
    }
}
