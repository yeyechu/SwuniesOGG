package com.swu.dimiz.ogg.ui.graph

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.R

@BindingAdapter("projectSize", "currentPage")
fun TextView.setTitle(size: Int, item: Int?) {
    text = when (item) {
        null, 0 -> resources.getString(R.string.graph_text_title_page_null)
        size -> resources.getString(R.string.graph_text_title_page_current)
        else -> resources.getString(R.string.graph_text_title_page, item)
    }
}