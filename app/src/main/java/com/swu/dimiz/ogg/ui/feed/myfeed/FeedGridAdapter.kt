package com.swu.dimiz.ogg.ui.feed.myfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentFeedItemBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import timber.log.Timber

class FeedGridAdapter(private val onFeedClickListener: OnFeedClickListener):
    ListAdapter<Feed, FeedGridAdapter.FeedViewHolder>(FeedDiffCallback) {
    class FeedViewHolder(private var binding: FragmentFeedItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(feed: Feed) {
            binding.feed = feed
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(FragmentFeedItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed = getItem(position)
        holder.itemView.setOnClickListener {
            onFeedClickListener.onClick(feed)
        }
        holder.bind(feed)
    }

    companion object FeedDiffCallback: DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class OnFeedClickListener(val clickListener: (feed: Feed) -> Unit) {
        fun onClick(feed: Feed) = clickListener(feed)
    }
}