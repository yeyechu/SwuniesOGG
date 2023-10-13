package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListItemBinding
import com.swu.dimiz.ogg.ui.env.badges.BadgeListViewModel

class BadgeAdapter(private val data: List<String>,
                   private val viewModel: BadgeListViewModel,
                   private val parentPosition: Int,
                   private val badgeId: List<List<Int>>) :
    RecyclerView.Adapter<BadgeAdapter.BadgesViewHolder>() {

    inner class BadgesViewHolder(private var binding: FragmentBadgeListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, viewModel: BadgeListViewModel, badges: List<List<Int>>) {
            binding.badgeId = badgeId[parentPosition][position]
            binding.viewModel = viewModel
            binding.imageBadgeIcon.setImageResource(badgeImages[parentPosition][position])
            //binding.imageBadgeIcon.setImageResource(images)
            binding.textBadgeTitle.text = data[position]
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgesViewHolder {
        return BadgesViewHolder(FragmentBadgeListItemBinding.inflate(
            LayoutInflater.from(parent.context),parent, false
        ))
    }
    override fun onBindViewHolder(holder: BadgesViewHolder, position: Int) {

        holder.apply {

            bind(position, viewModel, badgeId)
        }
    }
    override fun getItemCount(): Int = data.size

    val images = R.drawable.badge_40000

    val badgeImages = listOf(
        listOf(R.drawable.badge_40001, R.drawable.badge_40002, R.drawable.badge_40003),
        listOf(R.drawable.badge_40004, R.drawable.badge_40005, R.drawable.badge_40006),
        listOf(R.drawable.badge_40007, R.drawable.badge_40008, R.drawable.badge_40009, R.drawable.badge_40010),
        listOf(R.drawable.badge_40011, R.drawable.badge_40012, R.drawable.badge_40013),
        listOf(R.drawable.badge_40014, R.drawable.badge_40015, R.drawable.badge_40016, R.drawable.badge_40017, R.drawable.badge_40018, R.drawable.badge_40019, R.drawable.badge_40020, R.drawable.badge_40021),
        listOf(R.drawable.badge_40022, R.drawable.badge_40023, R.drawable.badge_40024),
        listOf(R.drawable.badge_40025, R.drawable.badge_40026, R.drawable.badge_40027, R.drawable.badge_40028, R.drawable.badge_40029, R.drawable.badge_40030, R.drawable.badge_40031)
    )
}

