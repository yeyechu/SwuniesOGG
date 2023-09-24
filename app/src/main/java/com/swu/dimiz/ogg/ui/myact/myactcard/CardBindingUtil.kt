package com.swu.dimiz.ogg.ui.myact.myactcard

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable


@BindingAdapter("actitityTitle")
fun TextView.setTitle(item: ActivitiesSustainable?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("activityCo2")
fun TextView.setCo2(item: ActivitiesSustainable?) {
    item?.let {
        text = "탄소감축량"+ item.co2.toString() + "kg"
    }
}

@BindingAdapter("activityImage")
fun ImageView.setImage(item: ActivitiesSustainable?) {
    item?.let {
        setImageBitmap(item.image)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<ActivitiesSustainable>?) {
    val adapter = recyclerView.adapter as SustCardItemAdapter
    adapter.submitList(data)
}

//-------------Extra--------------

@BindingAdapter("actitityTitle")
fun TextView.setTitle(item: ActivitiesExtra?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("activityCo2")
fun TextView.setCo2(item: ActivitiesExtra?) {
    item?.let {
        text = "탄소감축량"+ item.co2.toString() + "kg"
    }
}

@BindingAdapter("activityImage")
fun ImageView.setImage(item: ActivitiesExtra?) {
    item?.let {
        setImageBitmap(item.image)
    }
}