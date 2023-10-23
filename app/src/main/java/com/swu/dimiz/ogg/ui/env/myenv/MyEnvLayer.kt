package com.swu.dimiz.ogg.ui.env.myenv

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
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
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class MyEnvLayer : Fragment() {

    private var _binding: LayerEnvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BadgeListViewModel by activityViewModels { BadgeListViewModel.Factory }
    private val envViewModel: EnvViewModel by activityViewModels()

    private var initX: Float = 0f
    private var initY: Float = 0f

    private var dx: Float = 0f
    private var dy: Float = 0f

    private var focusedItem = 0

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
            viewModel.onSaveCompleted()
            Toast.makeText(context, getString(R.string.envlayer_button_saved), Toast.LENGTH_LONG).show()
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

        val badgeInventoryAdapter = BadgeInventoryAdapter(BadgeListAdapter.BadgeClickListener {
            addBadge(it)
        })

        viewModel.inventory.observe(viewLifecycleOwner) {
            viewModel.initInventoryList()
        }

        viewModel.adapterList.observe(viewLifecycleOwner) {
            it?.let {
                binding.badgeList.adapter = badgeInventoryAdapter
                badgeInventoryAdapter.inventory = it
            }
        }
        // ────────────────────────────────────────────────────────────────────────────────────────
        //                                    ㅇ
    }

    private fun addBadge(item: Badges) {
        val badge = ImageView(context)
        val button = ImageView(context)
        val frameLayout = FrameLayout(requireContext())

        button.apply {
            id = item.badgeId + 100_000
            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            this.layoutParams = layoutParams
            layoutParams.gravity = Gravity.END

            setImageResource(R.drawable.myenv_button_decoration_delete)
            //setImageResource(R.drawable.myenv_decoration_delete_selector)
            adjustViewBounds = true

            setOnClickListener {
                removeBadge(item)
            }

        }
        badge.apply {
            id = item.badgeId
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            this.layoutParams = layoutParams

            layoutParams.setMargins(0, 29, 29, 0)

            setImageBitmap(item.imageDeco)
            adjustViewBounds = true
            setBackgroundResource(R.drawable.myenv_decoration_selector)

        }
        frameLayout.apply {
            id = item.badgeId + 200_000
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            addView(badge)
            addView(button)

            setOnTouchListener { view, event ->
                viewModel.onChangeDetected()

                val viewParent = view.parent as View

                val parentHeight = viewParent.height
                val parentWidth = viewParent.width

                val xMax = parentWidth - view.width
                val yMax = parentHeight - view.height

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        findFocus()?.let {
                            Timber.i("포커스 1 : ${findFocus().id}")
                            clearFocus()
                        }

                        badge.isFocusableInTouchMode = true
                        badge.requestFocus()
                        button.visibility = View.VISIBLE

                        Timber.i("포커스 2 : ${findFocus().id}")

                        dx = view.x - event.rawX
                        dy = view.y - event.rawY
                        initX = view.x
                        initY = view.y
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {

                        var motionTouchEventX = event.rawX + dx
                        motionTouchEventX = max(0f, motionTouchEventX)
                        motionTouchEventX = min(xMax.toFloat(), motionTouchEventX)
                        view.x = motionTouchEventX

                        var motionTouchEventY = event.rawY + dy
                        motionTouchEventY = max(0f, motionTouchEventY)
                        motionTouchEventY = min(yMax.toFloat(), motionTouchEventY)
                        view.y = motionTouchEventY
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        Timber.i("배지 아이디 ${badge.id} 마지막 위치 : ${view.x}, ${view.y}")
                        if (abs(view.x - initX) <= 16 && abs(view.y - initY) <= 16)
                            view.performClick()
                        true
                    }

                    else -> false
                }
            }
        }

        binding.canvasLayout.apply {
            addView(frameLayout)
        }
        viewModel.inventoryOut(item)
    }

    private fun removeBadge(item: Badges) {
        val badgeId = item.badgeId
        val badge = requireView().findViewById<ImageView>(badgeId)
        val button = requireView().findViewById<ImageView>(badgeId + 100_000)
        val frame = requireView().findViewById<FrameLayout>(badgeId + 200_000)

        frame.apply {
            removeView(badge)
            removeView(button)
        }

        binding.canvasLayout.apply {
            removeView(frame)
        }
        viewModel.inventoryIn(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}