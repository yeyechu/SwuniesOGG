package com.swu.dimiz.ogg.ui.graph.myact

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerGraphMyactGroupBinding


class GraphItemMyActReduceFragment : Fragment() {
    private lateinit var binding: LayerGraphMyactGroupBinding
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.layer_graph_myact_group, container, false)
        return binding.root


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = binding.mostReduceCo2ActChart // 바인딩을 통해 바 차트 참조

        // 차트 설정
        configurePieChart()

    }
    // 차트 설정
    private fun configurePieChart() {
        pieChart.setUsePercentValues(true)

        val entries: MutableList<PieEntry> = ArrayList()

        entries.add(PieEntry(20f, "활동활동활동활동1"))
        entries.add(PieEntry(30f, "활동2"))
        entries.add(PieEntry(50f, "활동3"))


// 데이터 항목에 사용할 색상 배열 (원하는 색상으로 지정)
        val colors = intArrayOf(
            Color.parseColor("#E8EFFD"),
            Color.parseColor("#A4C0F8"),
            Color.parseColor("#6897F3")

        )

// PieDataSet을 생성하고 설정
        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.colors = colors.toList() // List<Int>로 변환하여 설정

        // 라벨 및 숫자 숨기기
        pieDataSet.setDrawValues(false) // 숫자 숨기기


// PieData를 생성하고 설정
        val pieData = PieData(pieDataSet)


// PieChart 설정
        pieChart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            setDrawEntryLabels(false) // 파이 조각 위의 라벨 숨기기
            setEntryLabelColor(Color.BLACK)

            // 범례 위치 설정
            legend.apply {
                form = Legend.LegendForm.CIRCLE
                textSize = 12f
                formSize = 20f
                formToTextSpace = 10f
                yEntrySpace = 15f

                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // 우측 정렬
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false) // 차트 내부에 범례를 그리지 않음
            }
        }


    }
}
