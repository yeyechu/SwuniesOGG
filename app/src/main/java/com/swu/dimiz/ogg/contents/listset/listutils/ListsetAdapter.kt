package com.swu.dimiz.ogg.contents.listset.listutils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListsetListItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily

class ListsetAdapter(
    private val minusListener: ListClickListener,
    private val plusListener: ListClickListener,
    private val detailListener: ListClickListener
) : ListAdapter<ActivitiesDaily, ListsetAdapter.ListsetViewHolder>(ListDiffCallback) {

    class ListsetViewHolder(private var binding: FragmentListsetListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            activity: ActivitiesDaily,
            minusListener: ListClickListener,
            plusListener: ListClickListener,
            detailListener: ListClickListener
        ) {
            binding.activity = activity
            //binding.cardView.setCardBackgroundColor(R.color.secondary_baby_blue)
            binding.minusListener = minusListener
            binding.plusListener = plusListener
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

        holder.bind(activity, minusListener, plusListener, detailListener)
    }

    companion object ListDiffCallback : DiffUtil.ItemCallback<ActivitiesDaily>() {

        override fun areContentsTheSame(oldItem: ActivitiesDaily, newItem: ActivitiesDaily): Boolean {
            return oldItem.dailyId == newItem.dailyId && oldItem.freq == newItem.freq
        }

        override fun areItemsTheSame(oldItem: ActivitiesDaily, newItem: ActivitiesDaily): Boolean {
            return oldItem == newItem
        }
    }

    class ListClickListener(val clickListener: (item: ActivitiesDaily) -> Unit) {
        fun onClick(item: ActivitiesDaily) = clickListener(item)
    }
}