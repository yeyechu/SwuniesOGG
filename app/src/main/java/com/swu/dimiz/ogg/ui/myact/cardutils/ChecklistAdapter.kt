package com.swu.dimiz.ogg.ui.myact.cardutils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R


class ChecklistAdapter (private val itemList : List<Checklist>) : RecyclerView.Adapter<ChecklistViewHolder>()  {

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.layer_checklist_item, parent, false)
        return ChecklistViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val item = itemList[position]
//        holder.itemView.setOnClickListener {
//            itemClickListener.onClick(it, position)
//        }
        holder.apply {
            bind(item)
        }
    }

}