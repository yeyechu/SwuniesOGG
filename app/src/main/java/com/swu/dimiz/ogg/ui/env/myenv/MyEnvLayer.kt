package com.swu.dimiz.ogg.ui.env.myenv

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerEnvBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import com.swu.dimiz.ogg.ui.env.badges.BadgeListViewModel
import com.swu.dimiz.ogg.ui.env.badges.badgeadapters.BadgeInventoryAdapter
import com.swu.dimiz.ogg.ui.env.badges.badgeadapters.BadgeListAdapter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class MyEnvLayer : Fragment() {

    private var _binding: LayerEnvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BadgeListViewModel by activityViewModels { BadgeListViewModel.Factory }
    private val envViewModel: EnvViewModel by activityViewModels()

    private var widgetInitialX: Float = 0f
    private var widgetDX: Float = 0f
    private var widgetInitialY: Float = 0f
    private var widgetDY: Float = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate( inflater, R.layout.layer_env, container, false)

        // ────────────────────────────────────────────────────────────────────────────────────────
        //                                    BottomSheet
        val bottomBehavior = BottomSheetBehavior.from(binding.bottomLayout)
        bottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomBehavior.apply {
            addBottomSheetCallback(object  : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when(newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {}
                        BottomSheetBehavior.STATE_EXPANDED -> {}
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
                        BottomSheetBehavior.STATE_HIDDEN -> {}
                        BottomSheetBehavior.STATE_DRAGGING -> {}
                        BottomSheetBehavior.STATE_SETTLING -> {}
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.bottomLayout.apply {
                    }
                }
            })
        }

        //                                 BottomSheet 핸들러
        binding.handler.setOnClickListener {
            bottomBehavior.state = if(bottomBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.envViewModel = envViewModel

        binding.buttonExit.setOnClickListener {
            envViewModel.onNavigatedMyEnv()
            it.findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            envViewModel.onNavigatedMyEnv()
            // 파이어베이스 저장할 곳
            it.findNavController().navigateUp()
        }

        // ────────────────────────────────────────────────────────────────────────────────────────
        //                                    어댑터 정의
        val manager = GridLayoutManager(activity, 3)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = 1
        }
        binding.badgeList.layoutManager = manager

        val badgeAdapter = InventoryAdapter(BadgeListAdapter.BadgeClickListener {
            badgeView(it)
        })

        val badgeInventoryAdapter = BadgeInventoryAdapter(BadgeListAdapter.BadgeClickListener {
            badgeView(it)
        })

        //binding.badgeList.adapter = badgeAdapter
        binding.badgeList.adapter = badgeInventoryAdapter

        viewModel.inventory.observe(viewLifecycleOwner) {
            badgeInventoryAdapter.inventory = it
        }

        // ────────────────────────────────────────────────────────────────────────────────────────
        //                                    ㅇ


    }

    private fun badgeView(item: Badges) {
        val badge = ImageView(context)
        val button = ImageView(context)

        button.apply {
            id = item.badgeId
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setImageResource(R.drawable.myenv_button_decoration_delete)
            adjustViewBounds = true

        }
        badge.apply {
            id = item.badgeId

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setImageBitmap(item.imageDeco)
            adjustViewBounds = true
            setBackgroundResource(R.drawable.myenv_shape_imageholder_dash)
            setOnTouchListener { view, event ->

                val viewParent = view.parent as View

                val parentHeight = viewParent.height
                val parentWidth = viewParent.width

                val xMax = parentWidth - view.width
                val yMax = parentHeight - view.height

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        widgetDX = view.x - event.rawX
                        widgetDY = view.y - event.rawY
                        widgetInitialX = view.x
                        widgetInitialY = view.y
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {

                        var newX = event.rawX + widgetDX
                        newX = max(0F, newX)
                        newX = min(xMax.toFloat(), newX)
                        view.x = newX

                        var newY = event.rawY + widgetDY
                        newY = max(0F, newY)
                        newY = min(yMax.toFloat(), newY)
                        view.y = newY

                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (abs(view.x - widgetInitialX) <= 16 && abs(view.y - widgetInitialY) <= 16)
                            view.performClick()
                        true
                    }
                    else -> false
                }
            }

        }

        binding.canvasLayout.apply {
            addView(badge)
            //addView(button)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}