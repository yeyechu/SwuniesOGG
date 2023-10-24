package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.env.badges.BadgeListViewModel

class BadgeAdapter(private val data: List<String>,
                   private val viewModel: BadgeListViewModel,
                   private val parentPosition: Int,
                   private val inventory: List<Badges>) :
    RecyclerView.Adapter<BadgeAdapter.BadgesViewHolder>() {

    inner class BadgesViewHolder(private var binding: FragmentBadgeListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int,
            viewModel: BadgeListViewModel
        ) {
            var bool = false

            binding.badgeId = badges[parentPosition][position]
            binding.viewModel = viewModel
            binding.textBadgeTitle.text = data[position]

            binding.imageBadgeIcon.apply {
                for(i in inventory) {
                    if(i.badgeId == badges[parentPosition][position]) {
                        bool = true
                        break
                    }
                }
                if(bool) {
                    setImageResource(badgeImages[parentPosition][position])
                } else {
                    setImageResource(images)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgesViewHolder {
        return BadgesViewHolder(FragmentBadgeListItemBinding.inflate(
            LayoutInflater.from(parent.context)
        ))
    }
    override fun onBindViewHolder(holder: BadgesViewHolder, position: Int) {

        holder.apply {
            bind(position, viewModel)
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

    var badges = listOf(
        listOf(40001, 40002, 40003),
        listOf(40004, 40005, 40006),
        listOf(40007, 40008, 40009, 40010),
        listOf(40011, 40012, 40013),
        listOf(40014, 40015, 40016, 40017, 40018, 40019, 40020, 40021),
        listOf(40022, 40023, 40024),
        listOf(40025, 40026, 40027, 40028, 40029, 40030, 40031)
    )
}

