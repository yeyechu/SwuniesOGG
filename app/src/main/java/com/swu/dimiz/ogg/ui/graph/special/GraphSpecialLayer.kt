package com.swu.dimiz.ogg.ui.graph.special

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerGraphSpecialCardBinding
import com.swu.dimiz.ogg.ui.graph.GraphViewModel
import timber.log.Timber

class GraphSpecialLayer: Fragment() {

    private var _binding: LayerGraphSpecialCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var barChart2: BarChart
    // 상수 정의
    private val MAX_X_VALUE = 2
    private val MIN_Y_VALUE = 0f
    private val MAX_Y_VALUE = 50f
    private val SET_LABEL = "Data Set"

    private val viewModel: GraphViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.layer_graph_special_card, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        barChart2 = binding.specialChart // 바인딩을 통해 바 차트 참조

        configureChartAppearance() // 바 차트 설정
        val data = createChartData() // 데이터 생성
        prepareChartData(data) // 차트에 데이터 설정
    }
    // 바 차트 설정
    private fun configureChartAppearance() {
        barChart2.description.isEnabled = false // chart 밑에 description 표시 유무
        barChart2.setTouchEnabled(false) // 터치 유무
        barChart2.legend.isEnabled = false // Legend는 차트의 범례
        barChart2.setExtraOffsets(0f, 50f, 0f, 50f)

        // ----- XAxis  - 선 유무, 사이즈, 색상, 축 위치 설정
        val xAxis: XAxis = barChart2.xAxis
        val labels = listOf(
            "나",
            "다른회원"

        ) // XAxis 레이블로 사용할 문자열 리스트
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        xAxis.textSize = 15f
        xAxis.gridLineWidth = 0f
        xAxis.gridColor = Color.parseColor("#FFFFFF")
        xAxis.position = XAxis.XAxisPosition.BOTTOM // X 축 데이터 표시 위치

        // ----- YAxis(Left)  - 선 유무, 데이터 최솟값/최댓값, label 유무
        val axisLeft: YAxis = barChart2.axisLeft
        axisLeft.setDrawGridLines(false)
        axisLeft.setDrawAxisLine(false)
        axisLeft.axisMinimum = 0f // 최솟값
        axisLeft.axisMaximum = 50f // 최댓값
        axisLeft.granularity = 1f // 값만큼 라인선 설정
        axisLeft.setDrawLabels(false) // label 삭제

        // ----- YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        val axisRight: YAxis = barChart2.axisRight
        axisRight.textSize = 15f
        axisRight.setDrawLabels(false) // label 삭제
        axisRight.setDrawGridLines(false)
        axisRight.setDrawAxisLine(false)

        // ----- XAxis에 원하는 String 설정하기
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                if (index >= 0 && index < labels.size) {
                    return labels[index]
                }
                return "" // 범위를 벗어나면 빈 문자열 반환
            }
        }
    }

    // 차트 데이터 생성
    private fun createChartData(): BarData {

        // (여기)

        // 1. [BarEntry] BarChart에 표시될 데이터 값 생성
        val values: ArrayList<BarEntry> = ArrayList()
        val random = java.util.Random()

        for (i in 0 until MAX_X_VALUE) {
            val x = i.toFloat()
            val random = java.util.Random()
            val y: Float = random.nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE) + MIN_Y_VALUE
            values.add(BarEntry(x, y))
        }

        // 값이 높은 순서대로 정렬
        values.sortByDescending { it.y }

        // 2. [BarDataSet] 단순 데이터를 막대 모양으로 표시, BarChart의 막대 커스텀
        val dataSet = BarDataSet(values, SET_LABEL)
        dataSet.setDrawIcons(false)
        dataSet.setDrawValues(false)

        // 바 색상 설정
        dataSet.setColors(
            Color.parseColor("#FFCE6E"),
            Color.parseColor("#F5F5F5"))



        // 3. [BarData] 보여질 데이터 구성
        val data = BarData(dataSet)
        data.barWidth = 1f
        return data
    }

    // 차트에 데이터 설정 및 갱신
    private fun prepareChartData(data: BarData) {
        barChart2.data = data // BarData 전달
        barChart2.invalidate() // BarChart 갱신해 데이터 표시
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}