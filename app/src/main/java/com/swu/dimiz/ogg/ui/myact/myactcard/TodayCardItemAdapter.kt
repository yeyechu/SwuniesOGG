package com.swu.dimiz.ogg.ui.myact.myactcard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.ui.myact.post.PostWindow
import timber.log.Timber

private const val PATH_VALUE = "TODAY_ADAPTER"
class TodayCardItemAdapter(val context: Context) :
    ListAdapter<ActivitiesDaily, TodayCardItemAdapter.ActivityListViewHolder>(ActivityListDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ActivityListViewHolder, position: Int) {

        val activity = getItem(position)

        holder.apply {
            bind(activity) {
                var intent = Intent(context, PostWindow::class.java)

                val activityId = activity.dailyId.toString()
                val activityValid = activity.instructionCount.toString()
                val pathValue = PATH_VALUE.toString()
                val gallaryButton = activity.waytoPost.toString()

                intent.putExtra("activity", activityId)
                intent.putExtra("activityValid", activityValid)
                intent.putExtra("pathValue", pathValue)
                intent.putExtra("gallaryButton", gallaryButton)

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityListViewHolder {
        return ActivityListViewHolder.from(parent)
    }

    class ActivityListViewHolder private constructor(val binding: FragmentMyActCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ActivitiesDaily, clickListener: View.OnClickListener) {
            binding.activity = item
            binding.buttonMyact.setOnClickListener(clickListener)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ActivityListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
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