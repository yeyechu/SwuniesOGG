package com.swu.dimiz.ogg.ui.env.badges

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.ui.myact.post.TextViewHolder

class HeaderAdapter: RecyclerView.Adapter<TextViewHolder>() {

    var data = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.fragment_badge_list_title, parent, false) as TextView

        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {

        val item = data[position]

        holder.textView.text = item
    }

    override fun getItemCount(): Int = data.size
}