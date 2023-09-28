package com.swu.dimiz.ogg.ui.env.myenv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.LayerEnvItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.env.badges.BadgeListAdapter


class InventoryAdapter(private val onClickListener: BadgeListAdapter.BadgeClickListener) :
    ListAdapter<Badges, InventoryAdapter.BadgesViewHolder>(BadgeListAdapter) {

    class BadgesViewHolder(private var binding: LayerEnvItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(badges: Badges, clickListener: BadgeListAdapter.BadgeClickListener) {
            binding.badge = badges
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgesViewHolder {
        return BadgesViewHolder(LayerEnvItemBinding.inflate(
            LayoutInflater.from(parent.context)
        ))
    }
    override fun onBindViewHolder(holder: BadgesViewHolder, position: Int) {
        val badges = getItem(position)

        holder.bind(badges, onClickListener)
    }

}

