package com.swu.dimiz.ogg.ui.env.badges

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListItemBinding
import com.swu.dimiz.ogg.generated.callback.OnClickListener
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.myact.post.PostWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class BadgeListAdapter(val context: Context):
    ListAdapter<DataItem, RecyclerView.ViewHolder>(BadgesDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeader(list: List<Badges>?) {
        adapterScope.launch {
            val items = when(list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.BadgeItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_VIEW_TYPE_HEADER -> TitleViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.BadgeItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> {
                val badgeItem = getItem(position) as DataItem.BadgeItem
                val badgId = badgeItem.badges.badgeId.toString()
                holder.apply {
                    bind(badgeItem.badges) {
                        var intent = Intent(context, PostWindow::class.java)
                        intent.putExtra("badge", badgId)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
    class ViewHolder private constructor(
        val binding: FragmentBadgeListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Badges, clickListener: View.OnClickListener) {
            binding.badge = item
            binding.badgeContainer.setOnClickListener(clickListener)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentBadgeListItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class BadgesDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.filter == newItem.filter
    }
}
sealed class DataItem {
    data class BadgeItem(val badges: Badges) : DataItem() {
        override val filter = badges.filter
    }

    object Header: DataItem() {
        override val filter: String = "뭐냐고"
    }

    abstract val filter: String
}

class TitleViewHolder(textView: View): RecyclerView.ViewHolder(textView) {
    companion object {
        fun from(parent: ViewGroup): TitleViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.fragment_badge_list_title, parent, false)

            return TitleViewHolder(view)
        }
    }
}

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1