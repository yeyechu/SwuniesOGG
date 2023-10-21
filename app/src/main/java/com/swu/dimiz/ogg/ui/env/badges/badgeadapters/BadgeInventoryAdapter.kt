package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.databinding.LayerEnvItemBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges

class BadgeInventoryAdapter(
                            private val onClickListener: BadgeListAdapter.BadgeClickListener
)
    : RecyclerView.Adapter<BadgeInventoryAdapter.InventoryViewHolder>() {

        var inventory = listOf<Badges>()

    inner class InventoryViewHolder(private var binding: LayerEnvItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(
                badges: Badges,

                clickListener: BadgeListAdapter.BadgeClickListener
            ) {
                binding.badge = badges
                binding.clickListener = clickListener
                //binding.viewModel = viewModel

            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayerEnvItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(inventory[position], onClickListener)
    }

    override fun getItemCount(): Int = inventory.size
}