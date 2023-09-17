package com.swu.dimiz.ogg.ui.myact.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowPostBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase

class PostWindow : Fragment() {

    private var _binding: WindowPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostWindowViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.window_post, container, false)

        navController = findNavController()

        val application = requireNotNull(this.activity).application
        val dataSource = OggDatabase.getInstance(application).myPostDatabaseDao
        val viewModelFactory = PostWindowViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(PostWindowViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        // ──────────────────────────────────────────────────────────────────────────────────────
        //

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}