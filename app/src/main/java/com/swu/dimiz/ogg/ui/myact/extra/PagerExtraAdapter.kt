package com.swu.dimiz.ogg.ui.myact.extra

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBigExtraBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.ui.myact.MyActViewModel

class PagerExtraAdapter(
    private val viewModel: MyActViewModel,
    private val onClickListener: OnClickListener)
    : ListAdapter<ActivitiesExtra, PagerExtraAdapter.ExtraViewHolder>(ExtraDiffCallback) {

    inner class ExtraViewHolder(private var binding: FragmentMyActCardItemBigExtraBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(extra: ActivitiesExtra, viewModel: MyActViewModel, clickListener: OnClickListener) {
            binding.activity = extra
            binding.viewModel = viewModel
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtraViewHolder {
        return ExtraViewHolder(
            FragmentMyActCardItemBigExtraBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ExtraViewHolder, position: Int) {
        val extra = getItem(position)
        holder.bind(extra, viewModel, onClickListener)
    }

    companion object ExtraDiffCallback : DiffUtil.ItemCallback<ActivitiesExtra>() {
        override fun areItemsTheSame(oldItem: ActivitiesExtra, newItem: ActivitiesExtra): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: ActivitiesExtra, newItem: ActivitiesExtra): Boolean {
            return oldItem.extraId == newItem.extraId
        }
    }

    class OnClickListener(val clickListener: (item: ActivitiesExtra) -> Unit) {
        fun onClick(item: ActivitiesExtra) = clickListener(item)
    }
}