package com.swu.dimiz.ogg.contents.listset.listutils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.contents.listset.ListsetViewModel
import com.swu.dimiz.ogg.databinding.FragmentListsetListItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ListsetAdapter(
    private val automobile: Int,
    private val viewModel: ListsetViewModel,
    private val checkListener: ListClickListener,
    private val detailListener: ListClickListener
) : ListAdapter<ActivitiesDaily, ListsetAdapter.ListsetViewHolder>(ListDiffCallback) {
    var selected = -1

    class ListsetViewHolder(private var binding: FragmentListsetListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val layout = binding.cardViewCar
        fun bind(
            activity: ActivitiesDaily,
            viewModel: ListsetViewModel,
            automobile: Int,
            checkListener: ListClickListener,
            detailListener: ListClickListener
        ) {
            binding.activity = activity
            binding.automobile = automobile
            binding.viewModel = viewModel
            binding.checkListener = checkListener
            binding.detailListener = detailListener
            binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListsetViewHolder {
        return ListsetViewHolder(
            FragmentListsetListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ListsetViewHolder, position: Int) {
        val activity = getItem(position)
        holder.apply {

//            if(selected == position) {
//                holder.layout.setCardBackgroundColor(Color.BLUE)
//            } else {
//                holder.layout.setCardBackgroundColor(Color.WHITE)
//            }
//
//            holder.layout.setOnClickListener {
//                var before = selected
//                selected - position
//                notifyItemChanged(before)
//                notifyItemChanged(selected)
//            }
            if(automobile == 0) {
                when(activity.dailyId) {
                    100010 -> layout.visibility = View.VISIBLE
                    100011 -> layout.visibility = View.VISIBLE
                    else -> layout.visibility = View.GONE
                }
            } else {
                layout.visibility = View.GONE
            }
            bind(activity, viewModel, automobile, checkListener, detailListener)
        }
    }

    companion object ListDiffCallback : DiffUtil.ItemCallback<ActivitiesDaily>() {

        override fun areContentsTheSame(oldItem: ActivitiesDaily, newItem: ActivitiesDaily): Boolean {

            return (oldItem.freq == newItem.freq && oldItem.dailyId == newItem.dailyId)
        }

        override fun areItemsTheSame(oldItem: ActivitiesDaily, newItem: ActivitiesDaily): Boolean {
            return oldItem == newItem
        }
    }

    class ListClickListener(val clickListener: (item: ActivitiesDaily) -> Unit) {
        fun onClick(item: ActivitiesDaily) = clickListener(item)
    }

    class ListsetAdapterDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val count = state.itemCount
            val offset = 20

            outRect.left = offset
        }
    }
}