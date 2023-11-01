package com.swu.dimiz.ogg.ui.myact.cardutils

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentChecklistBinding
import timber.log.Timber

class ChecklistFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    val contactsList : List<Checklist> = listOf(
        Checklist("급제동, 급출발 하지 않기", "나무 4.0그루만큼을 살릴 수 있어요"),
        Checklist("불필요한 엔진 공회전 하지 않기", "나무 6.2그루만큼을 살릴 수 있어요"),
        Checklist("경제속도(60~80km/h) 준수하기", "나무 10.0그루만큼을 살릴 수 있어요"),
        Checklist("불필요한 짐 싣고 다니지 않기", "나무 8.5그루만큼을 살릴 수 있어요"),
        Checklist("내리막길 운전 시 가속패달 밝지 않기", "나무 7.3그루만큼을 살릴 수 있어요"),
        Checklist("신호대기 시 기어를 중립으로 놓기", "나무 2.0그루만큼을 살릴 수 있어요")
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView = binding.mRecyclerView
        val adapter = ChecklistAdapter(contactsList)

        // 리사이클러뷰에 레이아웃 관리자 연결
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}