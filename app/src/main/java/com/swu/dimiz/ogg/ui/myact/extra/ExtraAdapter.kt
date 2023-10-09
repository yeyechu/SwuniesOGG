package com.swu.dimiz.ogg.ui.myact.extra

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBigExtraBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra

class ExtraAdapter(private val onClickListener: OnClickListener)
    : ListAdapter<ActivitiesExtra, ExtraAdapter.ExtraViewHolder>(SustDiffCallback) {

    class ExtraViewHolder(private var binding: FragmentMyActCardItemBigExtraBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(extra: ActivitiesExtra, clickListener: OnClickListener) {
            binding.activity = extra
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtraViewHolder {
        return ExtraViewHolder(
            FragmentMyActCardItemBigExtraBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ExtraViewHolder, position: Int) {
        val sust = getItem(position)
        holder.bind(sust, onClickListener)
    }

    companion object SustDiffCallback : DiffUtil.ItemCallback<ActivitiesExtra>() {
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