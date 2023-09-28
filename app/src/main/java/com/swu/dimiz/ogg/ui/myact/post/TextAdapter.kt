package com.swu.dimiz.ogg.ui.myact.post

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction


class TextAdapter : RecyclerView.Adapter<TextViewHolder>() {

    var data = listOf<Instruction>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.window_post_item_text, parent, false) as TextView

        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {

        val item = data[position]

        holder.textView.text = item.detail
    }

    override fun getItemCount(): Int = data.size
}

class TextViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)