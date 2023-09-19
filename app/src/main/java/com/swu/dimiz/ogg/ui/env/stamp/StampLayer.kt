package com.swu.dimiz.ogg.ui.env.stamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerStampBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase

class StampLayer : Fragment() {

    private lateinit var binding : LayerStampBinding
    private lateinit var viewModel: StampViewModel

    // 라이브데이터 + 뷰모델 필요

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_stamp, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = OggDatabase.getInstance(application)
        val viewModelFactory = StampViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(StampViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                  활동 날짜 불러오기
        viewModel.date.observe(viewLifecycleOwner) {
            if (it == 0) {
                binding.beforeLayer.visibility = View.VISIBLE
                binding.afterLayer.visibility = View.GONE
            } else {
                binding.afterLayer.visibility = View.VISIBLE
                binding.beforeLayer.visibility = View.GONE
            }
        }
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                  오늘 스탬프 설정

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                     스탬프 판

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                     프로그레스바

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                 레이아웃 확장 버튼
        viewModel.expandLayout.observe(viewLifecycleOwner) {
            if(it) {
                binding.layoutStamp.visibility = View.VISIBLE
                binding.buttonExpand.setImageResource(R.drawable.common_button_arrow_up)
            } else {
                binding.layoutStamp.visibility = View.GONE
                binding.buttonExpand.setImageResource(R.drawable.common_button_arrow_down)
            }
        }

        return binding.root
    }
}