package com.swu.dimiz.ogg.ui.graph

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
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
import kotlin.math.abs

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
                viewModel.fireGetBeforPorject(it)
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

        viewModel.titlesPost.observe(viewLifecycleOwner){
            it?.let{
                initPieChart(pieChart, viewModel.getCo2List(),it)
            }
        }

        viewModel.titlesCo2.observe(viewLifecycleOwner){
            it?.let{
                it.forEach {title ->
                    binding.mostCertifyAct1Name.text = title
                }
            }
        }

        barChart2 = binding.specialChart

        viewModel.rank.observe(viewLifecycleOwner) {
            it?.let {
                setBarData(barChart2, listOf(abs(it - 100), 50f), labels2)
            }
        }
    }

    private fun setBarData(barChart: BarChart, list: List<Float>, labelList: List<String>) {
        initBarChart(barChart, labelList)
        barChart.setScaleEnabled(false)

        val valueList = ArrayList<Int>()
        val entries: ArrayList<BarEntry> = ArrayList()

        valueList.clear()
        entries.clear()

        val yColorMap = mapOf(
            0 to requireContext().getColor(R.color.point_yellow),
            1 to requireContext().getColor(R.color.secondary_baby_gray),
            2 to requireContext().getColor(R.color.point_yellow_baby),
            3 to requireContext().getColor(R.color.point_yellow_light)
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
        xAxis.typeface = Typeface.create(ResourcesCompat.getFont(requireContext(), R.font.nanumsquare_r), Typeface.BOLD)

        xAxis.gridLineWidth = 0f
        xAxis.gridColor = requireContext().getColor(R.color.transparency_transparent)
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
        val axisLeft: YAxis = barChart.axisLeft
        axisLeft.setDrawGridLines(false)
        axisLeft.setDrawAxisLine(false)
        axisLeft.axisMinimum = 0f // 최솟값
        axisLeft.axisMaximum = 100f // 최댓값
        axisLeft.granularity = 1f // 값만큼 라인선 설정
        axisLeft.setDrawLabels(false) // label 삭제

        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawLabels(false)

        val rightAxis: YAxis = barChart.axisRight
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
    }

    private fun initPieChart(pieChart: PieChart, co2List: List<MyAllAct>, titleList: List<String>) {

        pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isRotationEnabled = false
            setDrawEntryLabels(false) // 파이 조각 위의 라벨 숨기기
            setEntryLabelColor(Color.BLACK)
            setExtraOffsets(0f, 5f, 0f, 5f)
            transparentCircleRadius = 20f
            holeRadius = 40f

            animateXY(500, 500)

            legend.apply {
                form = Legend.LegendForm.CIRCLE
                textSize = 13f
                formSize = 10f
                formToTextSpace = 15f
                yEntrySpace = 10f
                typeface = Typeface.create(ResourcesCompat.getFont(requireContext(), R.font.nanumsquare_r), Typeface.BOLD)
                textColor = requireContext().getColor(R.color.primary_gray)

                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // 우측 정렬
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false) // 차트 내부에 범례를 그리지 않음
            }
        }

        val entries: ArrayList<PieEntry> = ArrayList()
        var index = 0

        entries.clear()

        val yColorMap = mapOf(
            0 to requireContext().getColor(R.color.primary_blue),
            1 to requireContext().getColor(R.color.secondary_light_blue),
            2 to requireContext().getColor(R.color.secondary_baby_blue)
        )
        val colors = ArrayList<Int>()

        co2List.forEach {
            entries.add(PieEntry(it.allCo2.toFloat(), titleList[index]))
            colors.add(yColorMap[index] ?: Color.BLACK)
            index++
        }

        Timber.i("원형그래프 초기화: $entries")

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.colors = colors
        pieDataSet.setDrawValues(false) // 숫자 숨기기

        val pieData = PieData(pieDataSet)

        pieChart.apply {
            data = pieData
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }

    companion object {
        fun create(position: Int) = GraphLayer().apply {
            arguments = Bundle().apply {
                putInt(GRAPH_OBJECT, position)
            }
        }
    }
}
