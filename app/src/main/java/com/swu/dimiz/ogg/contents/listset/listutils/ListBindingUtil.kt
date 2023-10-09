package com.swu.dimiz.ogg.contents.listset.listutils

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
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
@BindingAdapter("setBackground")
fun setBackground(view: View, data: Int) {
    when(data) {
        1 -> view.setBackgroundResource(R.drawable.listaim_balloon_left)
        2 -> view.setBackgroundResource(R.drawable.listaim_balloon_center)
        3 -> view.setBackgroundResource(R.drawable.listaim_balloon_right)
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

@BindingAdapter("setCardBackground", "automobileData")
fun setCardBackground(view: CardView, item: ActivitiesDaily, bool: Boolean) {
    if(bool) {
        when(item.dailyId) {
            100010 -> view.setBackgroundResource(R.color.transparency_dimmed)
            100011 -> view.setBackgroundResource(R.color.transparency_dimmed)
            else -> view.setBackgroundResource(R.color.white)
        }
    } else {
        view.setBackgroundResource(R.color.white)
    }
}
@BindingAdapter("automobileData","activity", requireAll = true)
fun setAutomobile(view: View, bool: Boolean, item: ActivitiesDaily) {
    view.visibility = if(bool) {
        when(item.dailyId) {
            100010 -> View.VISIBLE
            100011 -> View.VISIBLE
            else -> View.GONE
        }
    } else {
        View.GONE
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
        adapter.data = data
    }
}
