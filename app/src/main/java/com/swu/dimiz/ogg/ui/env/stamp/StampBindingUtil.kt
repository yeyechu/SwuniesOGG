package com.swu.dimiz.ogg.ui.env.stamp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.converLongToDateString
import com.swu.dimiz.ogg.convertDurationToFormatted
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.ui.env.User

@BindingAdapter("expandButtonImage")
fun ImageView.setImage(boolean: Boolean) {
    boolean.let {
        if(boolean)
            setImageResource(R.drawable.common_button_arrow_up)
        else
            setImageResource(R.drawable.common_button_arrow_down)
    }
}

@BindingAdapter("userCo2")
fun TextView.setCo2(item: User?) {
    item?.let {
        text = (item.aim * 21f).toString()
    }
}

@BindingAdapter("leftCo2")
fun TextView.setleftText(item: User?) {
    item?.let {
        text = (item.aim * 21f).toString()
    }
}