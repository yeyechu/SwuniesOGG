package com.swu.dimiz.ogg.ui.env.badges

import android.content.res.Resources
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
        val res = holder.itemView.context.resources

        holder.textView.text = translateFilter(item, res)
    }

    override fun getItemCount(): Int = data.size
}

private fun translateFilter(data: String, res: Resources) : String {

    return when(data) {
        "feed" -> res.getString(R.string.badgelist_filter_feed)
        "clear" -> res.getString(R.string.badgelist_filter_clear)
        "part" -> res.getString(R.string.badgelist_filter_part)
        "reaction" -> res.getString(R.string.badgelist_filter_reaction)
        "sust" -> res.getString(R.string.badgelist_filter_sust)
        "co2" -> res.getString(R.string.badgelist_filter_co2)
        "extra" -> res.getString(R.string.badgelist_filter_extra)
        else -> res.getString(R.string.text_invalid)
    }
}

