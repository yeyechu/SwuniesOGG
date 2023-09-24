package com.swu.dimiz.ogg.ui.myact.myactcard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.contents.common.dialog.SystemWindow
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBigBinding
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBigExtraBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import timber.log.Timber

class SustCardItemAdapter(private val onClickListener: OnClickListener)
    : ListAdapter<ActivitiesSustainable, SustCardItemAdapter.SustViewHolder>(SustDiffCallback) {

    class SustViewHolder(private var binding: FragmentMyActCardItemBigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sust: ActivitiesSustainable, clickListener: OnClickListener) {
            binding.activity = sust
            binding.clickListner = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SustViewHolder {

        return SustViewHolder(
            FragmentMyActCardItemBigBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: SustViewHolder, position: Int) {
        val sust = getItem(position)

        holder.bind(sust, onClickListener)
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