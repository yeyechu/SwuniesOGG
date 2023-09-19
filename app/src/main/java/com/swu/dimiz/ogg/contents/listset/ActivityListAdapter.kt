package com.swu.dimiz.ogg.contents.listset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentListsetListItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily

class ActivityListAdapter(val clickListener: ActivityListener) :
    ListAdapter<ActivitiesDaily, ActivityListAdapter.ActivityListViewHolder>(
        ActivityListDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ActivityListViewHolder, position: Int) {

        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityListViewHolder {
        return ActivityListViewHolder.from(parent)
    }

    class ActivityListViewHolder private constructor(val binding: FragmentListsetListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ActivitiesDaily, clickListener: ActivityListener) {

            val res = itemView.context.resources

            binding.textTitle.text = item.title
            binding.textCo2.text = "탄소감축량 : " + item.co2.toString() + "kg"
            binding.imageActivity.setImageBitmap(item.image)
            binding.myhandler = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ActivityListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentListsetListItemBinding.inflate(layoutInflater, parent, false)

                return ActivityListViewHolder(binding)
            }
        }
    }
}

class ActivityListDiffCallback : DiffUtil.ItemCallback<ActivitiesDaily>() {
    override fun areItemsTheSame(oldItem: ActivitiesDaily, newItem: ActivitiesDaily): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ActivitiesDaily, newItem: ActivitiesDaily): Boolean {
        return oldItem.dailyId == newItem.dailyId
    }
}

class ActivityListener(val clickListener: (dailyId: Int) -> Unit) {
    fun onClick(item: ActivitiesDaily) = clickListener(item.dailyId)
}