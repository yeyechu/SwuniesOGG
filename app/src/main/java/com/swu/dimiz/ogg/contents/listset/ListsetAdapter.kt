package com.swu.dimiz.ogg.contents.listset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentListsetListItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily

class ListsetAdapter(
    private val numberHolder: List<ListData>,
    private val minusListener: ListClickListener,
    private val plusListener: ListClickListener,
    private val detailListener: ListClickListener
) : ListAdapter<ActivitiesDaily, ListsetAdapter.ListsetViewHolder>(ListDiffCallback) {

    class ListsetViewHolder(private var binding: FragmentListsetListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            activity: ActivitiesDaily,
            number: Int,
            minusListener: ListClickListener,
            plusListener: ListClickListener,
            detailListener: ListClickListener
        ) {
            binding.activity = activity
            binding.textNumber.text = number.toString()
            binding.minusListener = minusListener
            binding.plusListener = plusListener
            binding.detailListener = detailListener
            binding.executePendingBindings()
        }
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
        val numberList = numberHolder[position].aNumber
        holder.bind(activity, numberList, minusListener, plusListener, detailListener)
    }

    companion object ListDiffCallback : DiffUtil.ItemCallback<ActivitiesDaily>() {

        override fun areContentsTheSame(oldItem: ActivitiesDaily, newItem: ActivitiesDaily): Boolean {
            return oldItem.dailyId == newItem.dailyId
        }

        override fun areItemsTheSame(oldItem: ActivitiesDaily, newItem: ActivitiesDaily): Boolean {
            return oldItem == newItem
        }
    }

    class ListClickListener(val clickListener: (item: ActivitiesDaily) -> Unit) {
        fun onClick(item: ActivitiesDaily) = clickListener(item)
    }

}