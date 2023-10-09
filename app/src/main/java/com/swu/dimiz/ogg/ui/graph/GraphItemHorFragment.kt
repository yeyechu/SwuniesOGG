package com.swu.dimiz.ogg.ui.graph
//
//import android.graphics.Color
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import com.github.mikephil.charting.charts.HorizontalBarChart
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.components.YAxis
//import com.github.mikephil.charting.data.BarData
//import com.github.mikephil.charting.data.BarDataSet
//import com.github.mikephil.charting.data.BarEntry
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//import com.swu.dimiz.ogg.R
//import com.swu.dimiz.ogg.databinding.LayerGraphCertifyGroupBinding
//
//class GraphItemHorFragmentclass  : Fragment() {
//    private lateinit var horChart: HorizontalBarChart
//    private var _binding: LayerGraphCertifyGroupBinding? = null
//    private val binding get() = _binding!!
//
//    // 상수 정의
//    private val MAX_X_VALUE = 3
//    private val MIN_Y_VALUE = 0f
//    private val MAX_Y_VALUE = 50f
//    private val SET_LABEL = "Data Set"
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = DataBindingUtil.inflate(inflater, R.layout.layer_graph_certify_group, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val localBinding = binding // _binding을 지역 변수에 할당
//
//        horChart = localBinding.mostCertifyChart // 가로 바 차트 참조
//
//        configureChartAppearance() // 바 차트 설정
//        val data = createChartData() // 데이터 생성
//        if (data != null) {
//            prepareChartData(data)
//        } // 차트에 데이터 설정
//    }
//
//    private fun configureChartAppearance() {
//        horChart.description.isEnabled = false
//        horChart.setTouchEnabled(false)
//        horChart.legend.isEnabled = false
//        horChart.setExtraOffsets(0f, 50f, 0f, 50f)
//
//
//        // XAxis (수평 막대 기준 왼쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
//        val xAxis: XAxis = horChart.xAxis
//
//        // XAxis 레이블로 사용할 문자열 리스트
//        val labels = listOf(
//            "활동",
//            "활동활",
//            "활동활동"
//        )
//
//        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//
//
//        xAxis.setDrawAxisLine(false)
//        xAxis.granularity = 1f
//        xAxis.textSize = 15f
//        xAxis.gridLineWidth = 0f
//        xAxis.gridColor = Color.parseColor("#FFFFFF")
//        xAxis.position = XAxis.XAxisPosition.BOTTOM // X 축 데이터 표시 위치
//
//        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무
//        val axisLeft: YAxis = horChart.axisLeft
//        axisLeft.setDrawGridLines(false)
//        axisLeft.setDrawAxisLine(false)
//        axisLeft.axisMinimum = 0f // 최솟값
//        axisLeft.axisMaximum = 50f // 최댓값
//        axisLeft.granularity = 1f // 값만큼 라인선 설정
//        axisLeft.setDrawLabels(false) // label 표시 활성화
//
//        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
//        val axisRight: YAxis = horChart.axisRight
//        axisRight.textSize = 15f
//        axisRight.setDrawLabels(false) // label 삭제
//        axisRight.setDrawGridLines(false)
//        axisRight.setDrawAxisLine(false)
//
//
//    }
//
//
//    private fun createChartData(): BarData? {
//        // 1. [BarEntry] BarChart에 표시될 데이터 값 생성
//        val values: ArrayList<BarEntry> = ArrayList()
//        for (i in 0 until MAX_X_VALUE) {
//            val x = i.toFloat()
//            val random = java.util.Random()
//            val y: Float = random.nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE) + MIN_Y_VALUE
//            values.add(BarEntry(x, y))
//        }
//
//        // 값이 높은 순서대로 내림차순으로 정렬
//        values.sortByDescending { it.x }
//
//        // 2. [BarDataSet] 단순 데이터를 막대 모양으로 표시, BarChart의 막대 커스텀
//        val dataSet = BarDataSet(values, SET_LABEL)
//        dataSet.setDrawIcons(false)
//        dataSet.setDrawValues(false) // 각 막대 아래에 라벨 표시
//
//        val colors = intArrayOf(
//            Color.parseColor("#6897F3"),
//            Color.parseColor("#A4C0F8"),
//            Color.parseColor("#E8EFFD"),
//        )
//
//        // 각 막대에 다른 색상 할당
//        dataSet.colors = colors.toList()
//
//
//        // 3. [BarData] 보여질 데이터 구성
//        val data = BarData(dataSet)
//        data.barWidth = 0.5f
//        data.setValueTextSize(15f)
//        horChart.legend.isEnabled = true // 레전드 활성화
//
//        return data
//    }
//
//
//    private fun prepareChartData(data: BarData) {
//        horChart.data = data // BarData 전달
//        horChart.invalidate() // BarChart 갱신해 데이터 표시
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null // 뷰가 파괴될 때 바인딩 객체 해제
//    }
//}
//
