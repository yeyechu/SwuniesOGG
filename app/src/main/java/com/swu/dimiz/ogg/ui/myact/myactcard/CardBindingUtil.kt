package com.swu.dimiz.ogg.ui.myact.myactcard

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.ui.myact.extra.ExtraAdapter
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
        text = resources.getString(R.string.post_text_co2, item.co2)
        //setTextColor(resources.getColor(R.color.primary_blue, resources.newTheme()))
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
        text = item.instruction
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

@BindingAdapter("setDone", "limitation")
fun setBackground(view: CardView, item: ActivitiesSustainable, bool: Boolean) {
//    view.visibility = if(bool) {
//        when(item.dailyId) {
//            10010 -> View.VISIBLE
//            10011 -> View.VISIBLE
//            else -> View.GONE
//        }
//    } else {
//        View.GONE
//    }
}

@BindingAdapter("listDataSust")
fun bindRecyclerSust(recyclerView: RecyclerView, data: List<ActivitiesSustainable>?) {
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

@BindingAdapter("activityExampleImage")
fun ImageView.setExampleImage(item: ActivitiesExtra?) {
    item?.let {
        setImageBitmap(item.guideImage)
    }
}

@BindingAdapter("activityFreq")
fun TextView.setFreq(item: ActivitiesExtra?) {
    item?.let {
        text = if(item.limit != 0) {
            resources.getString(R.string.post_text_available_body_sust, item.limit)
        } else {
            "무제한"
        }
    }
}

@BindingAdapter("listDataExtra")
fun bindRecyclerExtra(recyclerView: RecyclerView, data: List<ActivitiesExtra>?) {
    val adapter = recyclerView.adapter as ExtraAdapter
    adapter.submitList(data)
}

@BindingAdapter("actitityInstruction")
fun TextView.setInstruction(item: ActivitiesExtra?) {
    item?.let {
        text = item.instruction
    }
}

@BindingAdapter("buttonText")
fun TextView.setButtonText(item: ActivitiesExtra?) {
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