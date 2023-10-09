package com.swu.dimiz.ogg.ui.env.stamp

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.CO2_WHOLE
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition

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
fun TextView.setCo2(item: MyCondition?) {
    item?.let {
        text = (item.aim * CO2_WHOLE).toString()
    }
}

@BindingAdapter("todayStamp")
fun ImageView.setTodayImage(data: Float) {
    when(data) {
        0f -> setBackgroundResource(R.drawable.env_image_stamp_000)
        in 0.001f..0.99f -> setBackgroundResource(R.drawable.env_image_stamp_050)
        else -> setBackgroundResource(R.drawable.env_image_stamp_100)
    }
}
