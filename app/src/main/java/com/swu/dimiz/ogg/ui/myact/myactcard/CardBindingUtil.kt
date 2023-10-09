package com.swu.dimiz.ogg.ui.myact.myactcard

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.ui.myact.sust.SustCardItemAdapter


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

@BindingAdapter("activityExampleImage")
fun ImageView.setExampleImage(item: ActivitiesSustainable?) {
    item?.let {
        setImageBitmap(item.guideImage)
    }
}

@BindingAdapter("activityFreq")
fun TextView.setFreq(item: ActivitiesSustainable?) {
    item?.let {
        text = resources.getString(R.string.post_text_available_body_sust, item.limit)
    }
}

@BindingAdapter("actitityInstruction")
fun TextView.setInstruction(item: ActivitiesSustainable?) {
    item?.let {
        text = item.instructionCount
    }
}

@BindingAdapter("buttonText")
fun TextView.setButtonText(item: ActivitiesSustainable?) {
    item?.let {
        text = when(item.waytoPost) {
            2 -> resources.getString(R.string.post_button_2)
            3 -> resources.getString(R.string.post_button_3)
            4 -> resources.getString(R.string.post_button_4)
            5 -> resources.getString(R.string.post_button_5)
            else -> resources.getString(R.string.post_button_take_photo)
        }
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