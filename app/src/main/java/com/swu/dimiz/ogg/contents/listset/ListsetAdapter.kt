package com.swu.dimiz.ogg.contents.listset

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentListsetListItemBinding
import com.swu.dimiz.ogg.generated.callback.OnClickListener
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
            //onMinusClickListener: View.OnClickListener,
            //onPlusClickListener: View.OnClickListener,
            minusListener: ListClickListener,
            plusListener: ListClickListener,
            detailListener: ListClickListener
        ) {
            binding.activity = activity
            //binding.buttonMinus.setOnClickListener(onMinusClickListener)
            //binding.buttonPlus.setOnClickListener(onPlusClickListener)
            //binding.textNumber.text = activity.freq.toString()
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
        //val numberList = numberHolder[position].aNumber
        //val adapter = this

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }

        holder.apply {
//            bind(activity, {
//                adapter.notifyItemChanged(position)
//            }, {
//                adapter.notifyItemChanged(position)
//            }, minusListener, plusListener, detailListener)
            bind(activity, minusListener, plusListener, detailListener)
        }
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

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener
    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setPlusClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

}