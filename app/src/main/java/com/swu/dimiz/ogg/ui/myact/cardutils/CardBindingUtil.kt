package com.swu.dimiz.ogg.ui.myact.cardutils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.Checklist
import com.swu.dimiz.ogg.convertLongToDateString
import com.swu.dimiz.ogg.convertToDuration
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

@BindingAdapter("postDate")
fun TextView.setPost(item: ActivitiesSustainable?) {
    item?.let {
        val duration = item.limit - convertToDuration(item.postDate)
        text = resources.getString(R.string.myact_card_text_post_date, convertLongToDateString(item.postDate), duration)
    }
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
        text = resources.getString(R.string.post_text_co2, item.co2)
    }
}

@BindingAdapter("activityMessage")
fun TextView.setMessage(item: ActivitiesExtra?) {
    item?.let {
        text = item.message
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

@BindingAdapter("postDate")
fun TextView.setExtraPost(item: ActivitiesExtra?) {
    item?.let {
        val duration = item.limit - convertToDuration(item.postDate) + 1
        text = resources.getString(R.string.myact_card_text_post_date_left, convertLongToDateString(item.postDate), duration)
    }
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

@BindingAdapter("checklistHeader")
fun TextView.setChecklistHeader(id: Int?) {
    id?.let {
        text = when(id) {
            10011 -> resources.getString(R.string.destination_title_checklist_driving)
            20007 -> resources.getString(R.string.destination_title_checklist_tire)
            else -> resources.getString(R.string.app_name_korean)
        }
    }
}

@BindingAdapter("checklistTitle")
fun TextView.setChecklistTitle(id: Int?) {
    id?.let {
        text = when(id) {
            10011 -> resources.getString(R.string.post_text_title_10011)
            20007 -> resources.getString(R.string.post_text_title_20007)
            else -> resources.getString(R.string.app_name_korean)
        }
    }
}

@BindingAdapter("checklistBody")
fun TextView.setChecklistBody(id: Int?) {
    id?.let {
        text = when(id) {
            10011 -> resources.getString(R.string.post_text_body_10011)
            20007 -> resources.getString(R.string.post_text_body_20007)
            else -> resources.getString(R.string.app_name_korean)
        }
    }
}

@BindingAdapter("checkItemTitle")
fun TextView.setCheckItemTitle(item: Checklist?) {
    item?.let {
        text = item.checkBoxTitle
    }
}

@BindingAdapter("checkItemBody")
fun TextView.setCheckItemBody(item: Checklist?) {
    item?.let {
        text = item.checkBoxSubtitle
    }
}