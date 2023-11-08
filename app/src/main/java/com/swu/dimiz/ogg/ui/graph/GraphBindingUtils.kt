package com.swu.dimiz.ogg.ui.graph

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.R

@BindingAdapter("currentPage")
fun TextView.setTitle(item: Int?) {
    text = if(item == null || item == 0) {
        resources.getString(R.string.graph_text_title_page_null)
    } else {
        resources.getString(R.string.graph_text_title_page, item)
    }
}