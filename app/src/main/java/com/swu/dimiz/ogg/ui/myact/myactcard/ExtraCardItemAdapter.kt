package com.swu.dimiz.ogg.ui.myact.myactcard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBigExtraBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.ui.myact.post.PostWindow
import timber.log.Timber

class ExtraCardItemAdapter (val context: Context) :
    ListAdapter<ActivitiesExtra, ExtraCardItemAdapter.ActivityListViewHolder>(ActivityListDiffCallbackextra()
    ) {

    override fun onBindViewHolder(holder: ActivityListViewHolder, position: Int) {

        val activity = getItem(position)

        holder.apply {
            bind(activity) {
                val intent = Intent(context, PostWindow::class.java)
                val activityId = activity.extraId.toString()
                intent.putExtra("activity", activityId)
                Timber.i("어댑터 $activityId")
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityListViewHolder {
        return ActivityListViewHolder.from(parent)
    }

    class ActivityListViewHolder private constructor(val binding: FragmentMyActCardItemBigExtraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ActivitiesExtra, clickListener: View.OnClickListener) {
            binding.activity= item
            binding.buttonMyact.setOnClickListener(clickListener)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ActivityListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentMyActCardItemBigExtraBinding.inflate(layoutInflater, parent, false)

                return ActivityListViewHolder(binding)
            }
        }
    }
}

class ActivityListDiffCallbackextra : DiffUtil.ItemCallback<ActivitiesExtra>() {
    override fun areItemsTheSame(oldItem: ActivitiesExtra, newItem: ActivitiesExtra): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ActivitiesExtra, newItem: ActivitiesExtra): Boolean {
        return oldItem.extraId == newItem.extraId
    }
}