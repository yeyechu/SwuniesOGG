package com.swu.dimiz.ogg.ui.graph.myact

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerGraphMyactGroupBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyAllAct
import com.swu.dimiz.ogg.ui.graph.GraphViewModel
import timber.log.Timber

class GraphMyActLayer : Fragment() {
    private var _binding: LayerGraphMyactGroupBinding? = null
    private lateinit var barChart2: BarChart
    private lateinit var pieChart: PieChart

    private val binding get() = _binding!!
    private val viewModel: GraphViewModel by activityViewModels { GraphViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.layer_graph_myact_group, container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 데이터 가져오기
        viewModel.fireInfo()

        // 세로바 관련
        barChart2 = binding.categotyChart

        configureChartAppearance() // 바 차트 설정
        val data = createChartData() // 데이터 생성
        prepareChartData(data) // 차트에 데이터 설정

        // 원형 관련
        pieChart = binding.mostReduceCo2ActChart
        viewModel.co2ActList.observe(viewLifecycleOwner, Observer { co2ActList ->
            if (co2ActList != null && co2ActList.size >= 3) {
                configurePieChart(co2ActList)

            }
        })


    }

    // 바 차트 설정
    private fun configureChartAppearance() {
        barChart2.description.isEnabled = false // chart 밑에 description 표시 유무
        barChart2.setTouchEnabled(false) // 터치 유무
        barChart2.legend.isEnabled = false // Legend는 차트의 범례
        barChart2.setExtraOffsets(0f, 50f, 0f, 50f)

        // ----- XAxis  - 선 유무, 사이즈, 색상, 축 위치 설정
        // XAxis 설정과 값 레이블을 LiveData에 따라 동적으로 설정
        val xAxis: XAxis = barChart2.xAxis
        val labels = listOf("에너지", "소비", "자원순환", "이동수단")

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
                return ""
            }
        }
    }
    private fun createChartData(): BarData {
        val values: ArrayList<BarEntry> = ArrayList()

        val xLabels = listOf("에너지", "소비", "이동수단", "자원순환")

        val viewModelList = listOf(
            viewModel.energyCo2,
            viewModel.consumptionCo2,
            viewModel.transportCo2,
            viewModel.resourceCo2
        )

        // LiveData를 각각 관찰하여 데이터를 추가
        viewModelList.forEachIndexed { index, liveData ->
            liveData.observe(viewLifecycleOwner) { value ->
                values.add(BarEntry(index.toFloat(), value))
                if (values.size == viewModelList.size) {
                    // 모든 LiveData 값이 업데이트되었을 때 그래프를 그립니다.
                    val data = prepareChartData(values)
                    barChart2.data = data
                    barChart2.invalidate()
                }
            }
        }

        // 빈 데이터를 리턴합니다. 실제 데이터는 LiveData를 통해 업데이트됩니다.
        return BarData()
    }
    private fun prepareChartData(values: ArrayList<BarEntry>): BarData {
        val dataSet = BarDataSet(values, "Data Set")
        dataSet.setDrawIcons(false)
        dataSet.setDrawValues(false)

        // 각 y 값과 색상을 매핑하는 데이터 맵
        val yColorMap = mapOf(
            0 to Color.parseColor("#FFCE6E"),
            1 to Color.parseColor("#FFE2A8"),
            2 to Color.parseColor("#FEF5E2"),
            3 to Color.parseColor("#F5F5F5")
        )

        // y 값의 크기 순으로 정렬
        values.sortByDescending { it.y }

        // 정렬된 순서에 따라 막대 색상을 설정
        val barColors = ArrayList<Int>()
        for (i in 0 until values.size) {
            val color = yColorMap[i] ?: Color.BLACK // 디폴트 색상
            barColors.add(color)
        }
        dataSet.setColors(barColors)

        val data = BarData(dataSet)
        data.barWidth = 0.9f
        return data
    }
    private fun prepareChartData(data: BarData) {
        barChart2.data = data
        barChart2.invalidate()
    }


    //원형 차트 설정
    private fun configurePieChart(co2ActList: List<MyAllAct>) {

        pieChart.setUsePercentValues(true)
        val entries: MutableList<PieEntry> = ArrayList()

        entries.add(PieEntry(co2ActList[0].allCo2.toFloat(), co2ActList[0].ID.toString()))
        entries.add(PieEntry(co2ActList[1].allCo2.toFloat(), co2ActList[1].ID.toString()))
        entries.add(PieEntry(co2ActList[2].allCo2.toFloat(), co2ActList[2].ID.toString()))
        Timber.i("원형 그래프 관찰")

        // 데이터 항목에 사용할 색상 배열 (원하는 색상으로 지정)
        val colors = intArrayOf(
            Color.parseColor("#6897F3"),
            Color.parseColor("#A4C0F8"),
            Color.parseColor("#E8EFFD")


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


    //해초랑 빙하 관련 차트 설정

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("onDestroyView()")
    }
}





