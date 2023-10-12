package com.swu.dimiz.ogg.ui.graph.myact

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerGraphMyactGroupBinding
import com.swu.dimiz.ogg.ui.graph.GraphViewModel
import timber.log.Timber

class GraphMyActLayer: Fragment() {

    private var _binding: LayerGraphMyactGroupBinding? = null
    private lateinit var pieChart: PieChart
    private lateinit var barChart2: BarChart
    // 상수 정의
    private val MAX_X_VALUE = 4
    private val MIN_Y_VALUE = 0f
    private val MAX_Y_VALUE = 50f
    private val SET_LABEL = "Data Set"

    private val binding get() = _binding!!

    private val viewModel: GraphViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.layer_graph_myact_group, container, false)



        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 세로바 관련
        barChart2 = binding.categotyChart // 바인딩을 통해 바 차트 참조

        configureChartAppearance() // 바 차트 설정
        val data = createChartData() // 데이터 생성
        prepareChartData(data) // 차트에 데이터 설정

        // 원형 관련
        pieChart = binding.mostReduceCo2ActChart // 바인딩을 통해 바 차트 참조
        configurePieChart()// 차트 설정

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
            "에너지",
            "소비",
            "자원순환",
            "이동수단"
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
            Color.parseColor("#FFCE6E"), Color.parseColor("#FFE2A8"), Color.parseColor("#FEF5E2"),
            Color.parseColor("#F5F5F5"))



        // 3. [BarData] 보여질 데이터 구성
        val data = BarData(dataSet)
        data.barWidth = 0.9f
        return data
    }

    // 차트에 데이터 설정 및 갱신
    private fun prepareChartData(data: BarData) {
        barChart2.data = data // BarData 전달
        barChart2.invalidate() // BarChart 갱신해 데이터 표시
    }
    //원형 차트 설정
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


//    override fun onDestroyView() {
//        super.onDestroyView()
//        Timber.i("onDestroyView()")
//    }
//}