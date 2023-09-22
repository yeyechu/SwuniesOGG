package com.swu.dimiz.ogg.ui.myact.myactcard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBigBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.ui.myact.post.PostWindow
import timber.log.Timber

class SusCardItemAdapter(val context: Context) :
    ListAdapter<ActivitiesSustainable, SusCardItemAdapter.ActivityListViewHolder>(ActivityListDiffCallbacksus()
    ) {

    override fun onBindViewHolder(holder: ActivityListViewHolder, position: Int) {

        val activity = getItem(position)

        holder.apply {
            bind(activity) {
                var intent = Intent(context, PostWindow::class.java)
                val activityId = activity.sustId.toString()
                intent.putExtra("activity", activityId)
                Timber.i("어댑터 $activityId")
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityListViewHolder {
        return ActivityListViewHolder.from(parent)
    }

    class ActivityListViewHolder private constructor(val binding: FragmentMyActCardItemBigBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ActivitiesSustainable, clickListener: View.OnClickListener) {
            binding.activity = item
            binding.buttonMyact.setOnClickListener(clickListener)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ActivityListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentMyActCardItemBigBinding.inflate(layoutInflater, parent, false)

                return ActivityListViewHolder(binding)
            }
        }
    }
}

class ActivityListDiffCallbacksus : DiffUtil.ItemCallback<ActivitiesSustainable>() {
    override fun areItemsTheSame(oldItem: ActivitiesSustainable, newItem: ActivitiesSustainable): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ActivitiesSustainable, newItem: ActivitiesSustainable): Boolean {
        return oldItem.sustId == newItem.sustId
    }
}