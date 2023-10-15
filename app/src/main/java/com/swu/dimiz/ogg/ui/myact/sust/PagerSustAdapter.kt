package com.swu.dimiz.ogg.ui.myact.sust

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBigBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.ui.myact.MyActViewModel

class PagerSustAdapter(
    private val viewModel: MyActViewModel,
    private val onClickListener: OnClickListener)
    : ListAdapter<ActivitiesSustainable, PagerSustAdapter.PagerViewHolder>(SustDiffCallback) {

    inner class PagerViewHolder(private var binding: FragmentMyActCardItemBigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sust: ActivitiesSustainable, viewModel: MyActViewModel, clickListener: OnClickListener) {
            binding.activity = sust
            binding.viewModel = viewModel
            binding.clickListner = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            FragmentMyActCardItemBigBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val sust = getItem(position)
        holder.bind(sust, viewModel, onClickListener)
    }

    companion object SustDiffCallback : DiffUtil.ItemCallback<ActivitiesSustainable>() {
        override fun areItemsTheSame(oldItem: ActivitiesSustainable, newItem: ActivitiesSustainable): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: ActivitiesSustainable, newItem: ActivitiesSustainable): Boolean {
            return oldItem.sustId == newItem.sustId
        }
    }

    class OnClickListener(val clickListener: (item: ActivitiesSustainable) -> Unit) {
        fun onClick(item: ActivitiesSustainable) = clickListener(item)
    }
}