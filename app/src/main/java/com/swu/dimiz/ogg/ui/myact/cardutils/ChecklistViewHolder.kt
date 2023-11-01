package com.swu.dimiz.ogg.ui.myact.cardutils

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R

class ChecklistViewHolder (v: View) : RecyclerView.ViewHolder(v) {
    var view : View = v

    fun bind(item: Checklist) {
        view.findViewById<TextView>(R.id.checkBoxTitle).text = item.checkBoxTitle
        view.findViewById<TextView>(R.id.checkBoxSubtitle).text = item.checkBoxSubtitle

    }
}