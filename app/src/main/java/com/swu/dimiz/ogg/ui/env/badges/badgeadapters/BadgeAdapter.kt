package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.env.badges.BadgeListViewModel

class BadgeAdapter(private val data: List<String>,
                   private val viewModel: BadgeListViewModel,
                   private val parentPosition: Int,
                   private val badges: List<List<Int>>) :
    RecyclerView.Adapter<BadgeAdapter.BadgesViewHolder>() {

    val images = listOf(R.drawable.settings_button_to_des_password, R.drawable.settings_button_to_des_password, R.drawable.settings_button_to_des_password, R.drawable.settings_button_to_des_password, R.drawable.settings_button_to_des_password, R.drawable.settings_button_to_des_password, R.drawable.settings_button_to_des_password)

    inner class BadgesViewHolder(private var binding: FragmentBadgeListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, viewModel: BadgeListViewModel, badges: List<List<Int>>) {
            binding.badgeId = badges[parentPosition][position]
            binding.viewModel = viewModel
            binding.imageBadgeIcon.setImageResource(images[parentPosition])
            binding.textBadgeTitle.text = data[position]
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgesViewHolder {
        return BadgesViewHolder(FragmentBadgeListItemBinding.inflate(
            LayoutInflater.from(parent.context),parent, false
        ))
    }
    override fun onBindViewHolder(holder: BadgesViewHolder, position: Int) {

        holder.bind(position, viewModel, badges)
    }
    override fun getItemCount(): Int = data.size

}

