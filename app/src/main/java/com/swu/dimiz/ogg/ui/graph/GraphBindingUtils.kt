package com.swu.dimiz.ogg.ui.graph

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.R

@BindingAdapter("projectSize", "currentPage", "startDate")
fun TextView.setTitle(size: Int, item: Int?, date: Long?) {
    text = when (item) {
        null, 10000 -> resources.getString(R.string.graph_text_title_page_null)
        size -> {
            if(date!! > 0L) {
                resources.getString(R.string.graph_text_title_page_current)
            } else {
                resources.getString(R.string.graph_text_title_page, item)
            }
        }
        else -> resources.getString(R.string.graph_text_title_page, item)
    }
}