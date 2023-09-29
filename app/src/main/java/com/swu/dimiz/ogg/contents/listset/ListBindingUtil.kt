package com.swu.dimiz.ogg.contents.listset

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
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
        text = "탄소감축량 "+ item.co2.toString() + "kg"
    }
}

@BindingAdapter("activityImage")
fun ImageView.setImage(item: ActivitiesDaily?) {
    item?.let {
        setImageBitmap(item.image)
    }
}

@BindingAdapter("activityFreq")
fun TextView.setFreq(item: ActivitiesDaily?) {
    item?.let {
        text = if (item.freq == 1) {
            "하루 " + item.freq.toString() + "번"
        } else {
            "하루 " + item.limit.toString() + "번"
        }
    }
}

@BindingAdapter("activityExampleImage")
fun ImageView.setExampleImage(item: ActivitiesDaily?) {
    item?.let {
        setImageBitmap(item.guideImage)
    }
}
@BindingAdapter("buttonVisibility")
fun buttonVisible(view: View, item: ActivitiesDaily?) {
    item?.let {
        view.visibility = if (item.waytoPost == 1) View.VISIBLE else View.GONE
    }
}

@BindingAdapter("buttonText")
fun TextView.setButtonText(item: ActivitiesDaily?) {
    item?.let {
        if (item.waytoPost == 3) text = "인증하기"
    }
}

@BindingAdapter("listAdapter")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<ActivitiesDaily>?) {
    val adapter = recyclerView.adapter as ActivityListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listDataSource")
fun bindRecyclerActivity(recyclerView: RecyclerView, data: List<ActivitiesDaily>?) {
    val adapter = recyclerView.adapter as ListsetAdapter
    adapter.submitList(data)
}

@BindingAdapter("textNumber")
fun setTextNumber(view:View, item: ActivitiesDaily?) {
    item?.let {
        //if (item.limit < view.tex) text = "인증하기"
    }
}
