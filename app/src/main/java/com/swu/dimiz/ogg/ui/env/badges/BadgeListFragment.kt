package com.swu.dimiz.ogg.ui.env.badges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListBinding

class BadgeListFragment : Fragment() {

    private lateinit var binding: FragmentBadgeListBinding

    // 라이브데이터나 뷰모델이 필요없음
    // 단순 어댑터 + 리사이클러뷰
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_badge_list, container, false)

        binding.upButton.setOnClickListener { view: View ->
            view.findNavController().navigateUp()
        }

        return binding.root
    }
}