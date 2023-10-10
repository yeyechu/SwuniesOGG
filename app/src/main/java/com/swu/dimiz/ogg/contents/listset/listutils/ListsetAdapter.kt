package com.swu.dimiz.ogg.contents.listset.listutils

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
    private val viewModel: ListsetViewModel,
    private val checkListener: ListClickListener,
    private val detailListener: ListClickListener
) : ListAdapter<ActivitiesDaily, ListsetAdapter.ListsetViewHolder>(ListDiffCallback) {

    //private val adapterScope = CoroutineScope(Dispatchers.Default)
    class ListsetViewHolder(private var binding: FragmentListsetListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            activity: ActivitiesDaily,
            viewModel: ListsetViewModel,
            checkListener: ListClickListener,
            detailListener: ListClickListener
        ) {
            binding.activity = activity
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

            bind(activity, viewModel, checkListener, detailListener)
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