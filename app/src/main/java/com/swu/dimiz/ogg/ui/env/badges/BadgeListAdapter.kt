package com.swu.dimiz.ogg.ui.env.badges

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.FragmentBadgeListItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges


class BadgeListAdapter(private val onClickListener: BadgeClickListener) :
    ListAdapter<Badges, BadgeListAdapter.BadgesViewHolder>(BadgesDiffCallback) {

    class BadgesViewHolder(private var binding: FragmentBadgeListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(badges: Badges, clickListener: BadgeClickListener) {
            binding.badge = badges
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgesViewHolder {
        return BadgesViewHolder(FragmentBadgeListItemBinding.inflate(
            LayoutInflater.from(parent.context)
        ))
    }
    override fun onBindViewHolder(holder: BadgesViewHolder, position: Int) {
        val badges = getItem(position)

        holder.bind(badges, onClickListener)
    }
    companion object BadgesDiffCallback : DiffUtil.ItemCallback<Badges>() {
        override fun areContentsTheSame(oldItem: Badges, newItem: Badges): Boolean {
            return oldItem.badgeId == newItem.badgeId
        }

        override fun areItemsTheSame(oldItem: Badges, newItem: Badges): Boolean {
            return oldItem == newItem
        }
    }

    class BadgeClickListener(val clickListener: (item: Badges) -> Unit) {
        fun onClick(item: Badges) = clickListener(item)
    }

    private var badgeCount: Int = 0

    fun updateBadgeCount(updatedCount: Int) {
        badgeCount = updatedCount
        notifyDataSetChanged()
    }
}
