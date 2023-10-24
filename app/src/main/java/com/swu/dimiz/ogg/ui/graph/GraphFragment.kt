package com.swu.dimiz.ogg.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentGraphBinding
import timber.log.Timber

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    private val fragmentViewModel: GraphViewModel by activityViewModels { GraphViewModel.Factory }
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_graph, container, false)

        fragmentViewModel.fireInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        binding.viewModel = fragmentViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        // ▲ 이건 제가 추가한 건데요
        // xml 파일에 viewModel 선언하고 바인딩 안해주면 아무 데이터도 안떠요
        // _binding = DataBindingUtil.inflate(
        //            inflater, R.layout.fragment_graph, container, false)
        // ▲ 이거 의미가 xml 파일을 binding 변수에 담아서 inflate 하겠다는 말이에요

        // 그래서 binding.viewModel은 xml 파일에 선언한 변수 viewModel을 말하는 거고 ▼
        // <variable
        //            name="viewModel"
        //            type="com.swu.dimiz.ogg.ui.graph.GraphViewModel" />

        // fragmentViewModel은 프래그먼트에서 저 맨 위에 선언해준 뷰모델을 말하는 겁니다
        // private val fragmentViewModel: GraphViewModel by activityViewModels { GraphViewModel.Factory }
        // ▲ 모르시는 거 같아서 일부러 이름 다르게 선언했어요

        // 그리고 라이프사이클도 같이 바인딩 해줘야 프래그먼트 라이프 사이클 따라서 xml 파일도 같이 쫓아다녀요
        // 라이프사이클 바인딩을 안해주면 데이터가 바뀌어도 데이터 갱신을 안해요

        // 혹시 _binding인데 왜 binding을... 한다면,
        // private var _binding: FragmentGraphBinding? = null
        // private val binding get() = _binding!!
        // 이게 뷰모델에 선언하는 변수들처럼 바인딩 변수도 이렇게 대입을 해주었기 때문에 _binding에 담은 걸 binding에서도 불러올 수 있는 거에요
        // 데이터를 대입할 때만 _binding 변수를 이용하고(수정 가능한 var 타입으로 선언됨)
        // 수정되면 안되는 데이터이기 때문에 불러올 때는 binding을 써요(수정 불가한 val 타입으로 선언됨)

        // 그리고 페이지 넘기는 거는 뷰페이저로 구현하면 되는데 인서님 온보딩에서 쓴 PagerAdapter로 구현한 뷰페이저는
        // 치명적인 결함때문에 구글이 버린 라이브러리라 유지보수도 안되고 있어서 뷰페이저2를 사용하셔야 해요
        // 온보딩은 그림 4개 띄우는데 어마어마한 메모리 누수가 있겠나 싶어서 그냥 뒀어요

        // 뷰페이저2는 종류가 2가지가 있어요
        // 1. 프래그먼트의 일부분인 뷰를 페이지처럼 넘기게 하는 RecyclerView.Adapter로 구현하는 뷰페이저(이게 제가 활동 카드 구현한 방식이에요)
        // 2. 프래그먼트로써 페이지 전체를 넘기는 FragmentStateAdapter로 구현하는 뷰페이저
        // 인서님은 2번을 사용해서 통계 구현하시면 됩니다

        // 저는 앞으로 다른 거 할 게 많아서 두 달동안 매일 밤 새워가면서 빡세게 작업해서 일찍 끝낸 거고
        // 인서님은 인서님 사정에 맞게 알아서 작업하시면 되는데 ★11월 둘째주★까지는 완벽하게 끝내주셔야 그 이후에 오류 수정하고 버그 잡을 시간이 될 듯 해요
        // 그래프에 데이터 연결하는 거 절대 어렵지 않아요 쉽고 간단해서 인서님한테 전적으로 맡긴 거에요 겁먹지 마시고 남은 기간 화이팅...!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}