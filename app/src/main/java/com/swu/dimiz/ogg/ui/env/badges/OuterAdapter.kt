package com.swu.dimiz.ogg.ui.env.badges
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.swu.dimiz.ogg.databinding.FragmentBadgeListItemBinding
//import com.swu.dimiz.ogg.databinding.FragmentBadgeListOverlapBinding
//import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class OuterAdapter: RecyclerView.Adapter<OuterAdapter.BadgeHeader>() {
//    var tag = listOf<String>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//    var badge = listOf<Badges>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    private val data = mapOf(
//        tag[0] to badge,
//        tag[1] to badge,
//        tag[2] to badge,
//        tag[3] to badge,
//        tag[4] to badge,
//        tag[5] to badge,
//    )
//
//
//    inner class BadgeHeader(private val binding: FragmentBadgeListOverlapBinding): RecyclerView.ViewHolder(binding.root) {
//        fun bind(position: Int) {
//            binding.textBadgeTitleOverlap.text = data.keys.elementAt(position)
//            binding.badgeListOverlap.apply {
//                adapter = InnerAdapter(BadgeListAdapter.BadgeClickListener {
//                }, data.values.elementAt(position), position)
//                layoutManager = LinearLayoutManager(binding.badgeListOverlap.context, LinearLayoutManager.HORIZONTAL, false)
//                setHasFixedSize(true)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeHeader {
//        val view = FragmentBadgeListOverlapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return BadgeHeader(view)
//    }
//
//    override fun onBindViewHolder(holder: BadgeHeader, position: Int) {
//        holder.bind(position)
//    }
//
//    override fun getItemCount(): Int {
//        return data.size
//    }
//}
//
//class InnerAdapter(private val onClickListener: BadgeListAdapter.BadgeClickListener,
//                   private val data: List<Badges>,
//                   private val parentPos: Int)
//    : ListAdapter<Badges, InnerAdapter.BadgeContent>(BadgeListAdapter.BadgesDiffCallback) {
//
//    inner class BadgeContent(private val binding: FragmentBadgeListItemBinding)
//        : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(badges: Badges,
//                 clickListener: BadgeListAdapter.BadgeClickListener,
//                 position: Int) {
//            binding.badge = badges
//            binding.clickListener = clickListener
//            binding.executePendingBindings()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeContent {
//        val view = FragmentBadgeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return BadgeContent(view)
//    }
//
//    override fun onBindViewHolder(holder: BadgeContent, position: Int) {
//        val badges = getItem(position)
//
//        holder.bind(badges, onClickListener, position)
//    }
//
//    override fun getItemCount(): Int {
//        return data.size
//    }
//}
