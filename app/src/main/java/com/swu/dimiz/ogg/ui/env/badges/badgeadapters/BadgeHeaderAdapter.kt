package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListFilterBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges

class BadgeHeaderAdapter: RecyclerView.Adapter<BadgeHeaderAdapter.TitleViewHolder>() {

    var data = listOf<String>()

    inner class TitleViewHolder(private var binding: FragmentBadgeListFilterBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            val res = itemView.context.resources

            binding.textBadgeTitle.text = translateFilter(data.elementAt(position), res)
            binding.filterList.apply {
                adapter = BadgeAdapter(badgeList.elementAt(position), position)
                layoutManager = LinearLayoutManager(binding.filterList.context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val view = FragmentBadgeListFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TitleViewHolder(view)
    }

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = data.size

    private val badgeList = listOf(
        listOf("┌ ('0') ┘", "리액션 장인", "리액션 장모"),
        listOf("지구 지키미", "지구 지키니", "지구 쥬키니"),
        listOf("에너자이저", "제로 웨이스터", "뚜벅뚜벅초", "리사이클러"),
        listOf("코미디 빅리거", "차기 툰베리", "환경 셀러브리티"),
        listOf("윈드 브레이커", "물의 요정", "다이오드 발광자", "일렉트로맨", "에뚜라미", "슈퍼마리오", "에코드라이버", "어른오닉"),
        listOf("지구용사 벡터맨", "환경전사 젠타포스", "탄소용사 하마하마"),
        listOf("미화반장", "I am 나무위키", "I am 그루트", "셀프 엘리베이터", "환경사랑 스컹크", "다먹는 하마", "로컬푸드 파이터")
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
