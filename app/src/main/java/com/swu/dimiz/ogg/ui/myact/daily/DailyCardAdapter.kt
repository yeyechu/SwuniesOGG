package com.swu.dimiz.ogg.ui.myact.daily

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.contents.listset.ListsetViewModel
import com.swu.dimiz.ogg.contents.listset.listutils.ListsetAdapter
import com.swu.dimiz.ogg.databinding.FragmentMyActCardItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily

class DailyCardAdapter(
    private val viewModel: ListsetViewModel,
    private val detailListener: ListsetAdapter.ListClickListener
) : ListAdapter<ActivitiesDaily, DailyCardAdapter.DailyViewHolder>(ListsetAdapter.ListDiffCallback) {

    class DailyViewHolder(private var binding: FragmentMyActCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            activity: ActivitiesDaily,
            viewModel: ListsetViewModel,
            detailListener: ListsetAdapter.ListClickListener
        ) {
            binding.activity = activity
            binding.viewModel = viewModel
            binding.detailListener = detailListener
            binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        return DailyViewHolder(
            FragmentMyActCardItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val activity = getItem(position)

        holder.apply {
            bind(activity, viewModel, detailListener)
        }
    }
}