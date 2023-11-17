package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListFilterBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.env.badges.BadgeListViewModel

class BadgeHeaderAdapter(private val viewModel: BadgeListViewModel)
    : RecyclerView.Adapter<BadgeHeaderAdapter.TitleViewHolder>() {

    var data = listOf<String>()
    var badges = listOf<Badges>()

    inner class TitleViewHolder(private var binding: FragmentBadgeListFilterBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            val res = itemView.context.resources

            binding.textBadgeTitle.text = translateFilter(data.elementAt(position), res)
            binding.filterList.apply {

                adapter = BadgeAdapter(
                    badgeList.elementAt(position),
                    viewModel,
                    position,
                    badges
                )
                layoutManager = LinearLayoutManager(binding.filterList.context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val view = FragmentBadgeListFilterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TitleViewHolder(view)
    }

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = data.size

    private val badgeList = listOf(
        listOf("┌ ('0') ┘", "리액션 장인", "리액션 장모"),
        listOf("지구 지키미", "지구 지키니", "지구 쥬키니"),
        listOf("에너자이저", "제로웨이스터", "뚜벅뚜벅초", "리사이클러"),
        listOf("코미디빅리거", "차기 툰베리", "환경셀럽"),
        listOf("윈드브레이커", "물의 요정", "다이오드발광", "일렉트로맨", "에뚜라미", "슈퍼마리오", "에코드라이버", "어른오닉"),
        listOf("지구용사", "환경전사", "탄소용사"),
        listOf("미화반장", "나무위키", "그루트", "엘리베이터", "환경스컹크", "다먹는 하마", "푸드파이터")
    )
}

private fun translateFilter(data: String, res: Resources) : String {

    return when(data) {
        "feed" -> res.getString(R.string.badgelist_filter_feed)
        "clear" -> res.getString(R.string.badgelist_filter_clear)
        "part" -> res.getString(R.string.badgelist_filter_part)
        "reaction" -> res.getString(R.string.badgelist_filter_reaction)
        "sust" -> res.getString(R.string.badgelist_filter_sust)
        "co2" -> res.getString(R.string.badgelist_filter_co2)
        "extra" -> res.getString(R.string.badgelist_filter_extra)
        else -> res.getString(R.string.text_invalid)
    }
}
