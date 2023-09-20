package com.swu.dimiz.ogg.ui.myact.myactcard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.contents.listset.ActivityListAdapter
import com.swu.dimiz.ogg.databinding.FragmentListsetListItemBinding
import com.swu.dimiz.ogg.databinding.FragmentMyActBinding
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily

class TodayCardItemAdapter(val clickListener: ActivityListener) :
    ListAdapter<ActivitiesDaily, TodayCardItemAdapter.ActivityListViewHolder>(
        ActivityListDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ActivityListViewHolder, position: Int) {

        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityListViewHolder {
        return ActivityListViewHolder.from(parent)
    }

    class ActivityListViewHolder private constructor(val binding: FragmentMyActCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ActivitiesDaily, clickListener: ActivityListener) {

            val res = itemView.context.resources

            binding.todayCardTextTitle.text = item.title
            binding.todayCardTextCo2.text = "탄소감축량 : " + item.co2.toString() + "kg"
            binding.todayCardImage.setImageBitmap(item.image)
            binding.myhandler = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ActivityListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                //fragmentmyactcarditem
                //데이터 변수 선언 (xml)
                val binding = FragmentMyActCardItemBinding.inflate(layoutInflater, parent, false)

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