package com.swu.dimiz.ogg.ui.myact.postsust

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowPostSustBinding
import com.swu.dimiz.ogg.ui.myact.MyActViewModel

class PostSustWindow : Fragment() {

    private var _binding : WindowPostSustBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyActViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate( inflater, R.layout.window_post_sust, container, false)

        binding.buttonExit.setOnClickListener {
            viewModel.completedPopup()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}