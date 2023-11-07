package com.swu.dimiz.ogg.ui.env.badges

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListBinding
import com.swu.dimiz.ogg.ui.env.badges.badgeadapters.BadgeHeaderAdapter

class BadgeListFragment : Fragment() {

    private var _binding: FragmentBadgeListBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragmentManager: FragmentManager

    private val viewModel: BadgeListViewModel by activityViewModels { BadgeListViewModel.Factory }
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                  화면 및 뷰모델 초기화
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_badge_list, container, false
        )

        viewModel.inventorySize.observe(viewLifecycleOwner) {
            val textDecorator =
                SpannableStringBuilder.valueOf(getString(R.string.badgelist_text_badge_count, it))
            binding.textBadgeCount.text = textDecorator.apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary_blue
                        )
                    ), 7, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(StyleSpan(Typeface.BOLD), 7, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val headerAdapter = BadgeHeaderAdapter(viewModel)

        binding.badgeHeader.apply {

            adapter = headerAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        viewModel.badgeFilter.observe(viewLifecycleOwner) {
            it?.let {
                headerAdapter.data = it
            }
        }

        viewModel.inventory.observe(viewLifecycleOwner) {
            it?.let {
                headerAdapter.badges = it
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                      이동 정의
        viewModel.navigateToSelected.observe(viewLifecycleOwner) {
            it?.let {
                addWindow()
                viewModel.completedPopup()
            }
        }
        viewModel.getAllBadge()
    }

    private fun addWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout, BadgeDetailWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}