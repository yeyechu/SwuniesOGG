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
import androidx.core.content.res.ResourcesCompat
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
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.env.badges.badgeadapters.BadgeHeaderAdapter
import timber.log.Timber

class BadgeListFragment : Fragment() {

    private var _binding: FragmentBadgeListBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragmentManager: FragmentManager

    private val viewModel: BadgeListViewModel by activityViewModels { BadgeListViewModel.Factory }
    private lateinit var navController: NavController

    var badges = listOf(
        listOf(40001, 40002, 40003),
        listOf(40004, 40005, 40006),
        listOf(40007, 40008, 40009, 40010),
        listOf(40011, 40012, 40013),
        listOf(40014, 40015, 40016, 40017, 40018, 40019, 40020, 40021),
        listOf(40022, 40023, 40024),
        listOf(40025, 40026, 40027, 40028, 40029, 40030, 40031)
        )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                  화면 및 뷰모델 초기화
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_badge_list, container, false
        )
        val typeface = Typeface.create(
            ResourcesCompat.getFont(requireContext(), R.font.gmarketsans_m),
            Typeface.BOLD
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
                    ), 7, 8, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(StyleSpan(Typeface.BOLD), 7, 8, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    setSpan(TypefaceSpan(typeface), 7, 8, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                }
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val headerAdapter = BadgeHeaderAdapter(viewModel, badges)

        binding.badgeHeader.apply {

            adapter = headerAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        viewModel.badgeFilter.observe(viewLifecycleOwner) {
            it?.let {
                Timber.i("$it")
                headerAdapter.data = it
            }
        }

//        val manager = GridLayoutManager(activity, 3)
//        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int = when(position) {
//                0 -> 1
//                else -> 1
//            }
//        }
//        binding.badgeList.layoutManager = manager
//
//        val badgeAdapter = BadgeListAdapter(BadgeListAdapter.BadgeClickListener { id ->
//            viewModel.showPopup(id)
//        })
//        binding.badgeList.adapter = badgeAdapter
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                      이동 정의
        viewModel.navigateToSelected.observe(viewLifecycleOwner) {
            it?.let {
                addWindow()
                viewModel.completedPopup()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
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