package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges

class BadgeAdapter(private val data: List<String>,
                   private val parentPosition: Int,
                   /*private val onClickListener: BadgeClickListener*/) :
    ListAdapter<Badges, BadgeAdapter.BadgesViewHolder>(BadgeListAdapter.BadgesDiffCallback) {

    val images = listOf(R.drawable.daily_10001, R.drawable.daily_10002, R.drawable.daily_10003, R.drawable.daily_10004, R.drawable.daily_10005, R.drawable.daily_10006, R.drawable.daily_10007)

    inner class BadgesViewHolder(private var binding: FragmentBadgeListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(badges: Badges, position: Int/*, clickListener: BadgeClickListener*/) {
            //binding.badge = badges
            binding.imageBadgeIcon.setImageResource(images[parentPosition])
            binding.textBadgeTitle.text = data[position]
            //binding.clickListener2 = clickListener
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
        holder.bind(badges, position/*, onClickListener*/)
    }

    class BadgeClickListener(val clickListener: (item: Badges) -> Unit) {
        fun onClick(item: Badges) = clickListener(item)
    }
}

