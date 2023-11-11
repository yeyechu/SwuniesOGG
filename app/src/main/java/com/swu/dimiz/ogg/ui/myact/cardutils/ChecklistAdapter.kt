package com.swu.dimiz.ogg.ui.myact.cardutils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R

class ChecklistAdapter : RecyclerView.Adapter<ChecklistViewHolder>()  {

    var id = 0
    private var itemList = listOf<Checklist>()

    override fun getItemCount(): Int {
        itemList = if(id == 10011) {
            drivingList
        } else {
            tireList
        }
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.layer_checklist_item, parent, false)
        return ChecklistViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val item = itemList[position]
//        holder.itemView.setOnClickListener {
//            itemClickListener.onClick(it, position)
//        }
        holder.apply {
            bind(item)
        }
    }

    private val drivingList : List<Checklist> = listOf(
        Checklist("급제동, 급출발 하지 않기", "나무 4.0그루만큼을 살릴 수 있어요"),
        Checklist("불필요한 엔진 공회전 하지 않기", "나무 6.2그루만큼을 살릴 수 있어요"),
        Checklist("경제속도(60~80km/h) 준수하기", "나무 10.0그루만큼을 살릴 수 있어요"),
        Checklist("불필요한 짐 싣고 다니지 않기", "나무 8.5그루만큼을 살릴 수 있어요"),
        Checklist("내리막길 운전 시 가속패달 밟지 않기", "나무 7.3그루만큼을 살릴 수 있어요"),
        Checklist("신호대기 시 기어를 중립으로 놓기", "나무 2.0그루만큼을 살릴 수 있어요")
    )

    private val tireList: List<Checklist> = listOf(
        Checklist("타이어 공기압 기준 확인하기", "ㅇ"),
        Checklist("타어 공기압 점검하기", "ㅇㅇ"),
        Checklist("타이어 손상 체크하고 교체하기", "ㅇㅇㅇ"),
        Checklist("휠 밸런스 및 손상도 체크하기", "0000"),
        Checklist("최소 1개월마다 점검하기", "ㅇㅇㅇ")
    )

}