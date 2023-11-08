package com.swu.dimiz.ogg.ui.graph

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
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.databinding.LayerGraphMyactGroupBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyAllAct
import timber.log.Timber

class GraphLayer : Fragment() {
    private var _binding: LayerGraphMyactGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var barChart: BarChart
    private lateinit var barChart2: BarChart
    private lateinit var pieChart: PieChart

    private var pageNumber = 0

    private val viewModel: GraphViewModel by activityViewModels { GraphViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.layer_graph_myact_group, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val labels = listOf(ENERGY, CONSUME, TRANSPORT, RECYCLE)
        val labels2 = listOf("나", "다른사람")

        arguments?.takeIf { it.containsKey(GRAPH_OBJECT) }?.apply {
            pageNumber = getInt(GRAPH_OBJECT)
            viewModel.setCurrentPage(pageNumber)
        }

        viewModel.currentPage.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.fireGetCategory(it)
                viewModel.fireGetCo2(it)
                viewModel.fireGetMostUp(it)
                viewModel.fireGetExtra(it)
                viewModel.fireGetReaction(it)
            }
        }

        //──────────────────────────────────────────────────────────────────────────────────────
        //                                   카테고리별 줄인 탄소량
        barChart = binding.categotyChart

        viewModel.co2ForCategory.observe(viewLifecycleOwner) {
            setBarData(barChart, it, labels)
        }
        //──────────────────────────────────────────────────────────────────────────────────────
        //                                   가장 많은 탄소를
        pieChart = binding.mostReduceCo2ActChart

        viewModel.titles.observe(viewLifecycleOwner){
            it?.let{
                configurePieChart(viewModel.co2Act(),it)
            }
        }

        viewModel.titlesMost.observe(viewLifecycleOwner){
            it?.let{
                mostActTitle(it)
            }
        }

        // 스페셜 차트
        barChart2 = binding.specialChart
        setBarData(barChart2, listOf(70f, 50f), labels2)
    }

    private fun mostActTitle(list: List<String>){
        binding.mostCertifyAct1Name.text = list[0]
        binding.mostCertifyAct2Name.text = list[1]
        binding.mostCertifyAct3Name.text = list[2]
    }

    private fun setBarData(barChart: BarChart, list: List<Float>, labelList: List<String>) {
        initBarChart(barChart, labelList)
        barChart.setScaleEnabled(false)

        val valueList = ArrayList<Int>()
        val entries: ArrayList<BarEntry> = ArrayList()

        valueList.clear()
        entries.clear()

        val yColorMap = mapOf(
            0 to Color.parseColor("#FFCE6E"),
            1 to Color.parseColor("#F5F5F5"),
            2 to Color.parseColor("#FEF5E2"),
            3 to Color.parseColor("#FFE2A8")
        )

        list.forEach {
            valueList.add(it.toInt())
        }

        val barColors = ArrayList<Int>()
        for (i in 0 until valueList.size) {
            val color = yColorMap[i] ?: Color.BLACK
            barColors.add(color)
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setDrawIcons(false)
        barDataSet.setDrawValues(false)
        barDataSet.colors = barColors

        val data = BarData(barDataSet)
        data.barWidth = 0.7f

        barChart.data = data
        barChart.invalidate()
    }

    private fun initBarChart(barChart: BarChart, labelList: List<String>) {

        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawBorders(false)

        barChart.description.isEnabled = false
        barChart.setTouchEnabled(false)
        barChart.legend.isEnabled = false
        barChart.setExtraOffsets(0f, 10f, 0f, 10f)
        barChart.renderer = RoundedBarChart(barChart, barChart.animator, barChart.viewPortHandler)

        barChart.animateY(1000)
        barChart.animateX(1000)

        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.textColor = requireContext().getColor(R.color.primary_gray)
        xAxis.textSize = 12f

        xAxis.gridLineWidth = 0f
        xAxis.gridColor = Color.parseColor("#FFFFFF")
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                if (index >= 0 && index < labelList.size) {
                    return labelList[index]
                }
                return ""
            }
        }

        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawLabels(false)

        val rightAxis: YAxis = barChart.axisRight
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
    }

    //-------------- 원형 차트 -------------------
    private fun configurePieChart(co2ActList: List<MyAllAct>, list: List<String>) {

        pieChart.setUsePercentValues(true)
        val entries: MutableList<PieEntry> = ArrayList()

        entries.add(PieEntry(co2ActList[0].allCo2.toFloat(), list[0]))
        entries.add(PieEntry(co2ActList[1].allCo2.toFloat(), list[1]))
        entries.add(PieEntry(co2ActList[2].allCo2.toFloat(), list[2]))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}
