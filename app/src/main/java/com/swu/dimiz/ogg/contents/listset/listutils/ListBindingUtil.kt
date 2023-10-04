package com.swu.dimiz.ogg.contents.listset.listutils

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.ListsetAdapter
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import com.swu.dimiz.ogg.ui.myact.post.TextAdapter

@BindingAdapter("actitityTitle")
fun TextView.setTitle(item: ActivitiesDaily?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("activityCo2")
fun TextView.setCo2(item: ActivitiesDaily?) {
    item?.let {
        text = resources.getString(R.string.post_text_co2, item.co2)
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
        text = resources.getString(R.string.post_text_available_body, item.limit)
    }
}

@BindingAdapter("activityExampleImage")
fun ImageView.setExampleImage(item: ActivitiesDaily?) {
    item?.let {
        setImageBitmap(item.guideImage)
    }
}
@BindingAdapter("freq")
fun TextView.setLimit(item: ActivitiesDaily?) {
    item?.let {
        text = item.freq.toString()
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
        text = when(item.waytoPost) {
            2 -> resources.getString(R.string.post_button_2)
            3 -> resources.getString(R.string.post_button_3)
            4 -> resources.getString(R.string.post_button_4)
            5 -> resources.getString(R.string.post_button_5)
            else -> resources.getString(R.string.post_button_take_photo)
        }
    }
}
@BindingAdapter("minusButton")
fun minusButton(view: ImageButton, item: ActivitiesDaily?) {
    item?.let {
//        if (item.freq == 0) {
//            view.isClickable = false
//            view.setColorFilter(R.color.black)
//        } else {
//            view.isClickable = true
//            view.setColorFilter(R.color.transparency_transparent)
//        }
    }
}

@BindingAdapter("listDataSource")
fun bindRecyclerActivity(recyclerView: RecyclerView, data: List<ActivitiesDaily>?) {
    val adapter = recyclerView.adapter as ListsetAdapter
    adapter.submitList(data)
}

@BindingAdapter("instructionAdapter")
fun bindRecyclerInstruction(recyclerView: RecyclerView, data: List<Instruction>?) {
    data?.let {
        val adapter = recyclerView.adapter as TextAdapter
        adapter.data = data!!
    }
}
