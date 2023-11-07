package com.swu.dimiz.ogg.ui.env

import android.graphics.Matrix
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.BADGE_SIZE
import com.swu.dimiz.ogg.databinding.FragmentEnvBinding
import timber.log.Timber

class EnvFragment : Fragment() {

    private var _binding: FragmentEnvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_env, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        Timber.i("프래그먼트 onViewCreated()")

        // ────────────────────────────────────────────────────────────────────────────────────────
        //                                      배지 위치 저장
        viewModel.initLocationFromFirebase()
        viewModel.badgeHolder.observe(viewLifecycleOwner) { list ->
            list?.let { list ->
                list.forEach {
                    if(it.bx > 0f && it.by > 0f) {
                        setMyEnv(it.bId, it.bx, it.by)
                    }
                }
            }
        }

        viewModel.setToast.observe(viewLifecycleOwner) {
            when(it) {
                1 -> {
                    OggSnackbar.make(view, getString(R.string.envlayer_button_saved)).show()
                    viewModel.onToastCompleted()
                }
                2 -> {
                    OggSnackbar.make(view, getString(R.string.env_toast_badge)).show()
                    viewModel.onToastCompleted()
                }
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                               환경 수정 플로팅 버튼 정의

        binding.badgeEditButton.apply {
            setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
            foreground = ContextCompat.getDrawable(requireContext(), R.drawable.env_button_edit_badge_floating)
        }

        viewModel.navigateToMyEnv.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationMyenv())
                //viewModel.onNavigatedMyEnv()
            }
        }
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                프로젝트 시작 버튼 정의
        viewModel.navigateToStart.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationListaim())
                viewModel.onNavigatedToStart()
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                            오늘 날짜, 스탬프 정보 업데이트
        viewModel.co2Holder.observe(viewLifecycleOwner) {
            viewModel.leftCo2()
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                  툴바 이동 정의
        val envToolbar = binding.envToolbar
        envToolbar.inflateMenu(R.menu.env_menu)

        envToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_badges -> {
                    navController.navigate(
                        EnvFragmentDirections.actionNavigationEnvToDestinationBadgeList()
                    )
                    true
                }
                R.id.action_my_page -> {
                    navController.navigate(
                        EnvFragmentDirections.actionNavigationEnvToDestinationMember()
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun setMyEnv(id: Int, valueX: Float, valueY: Float) {
        val badge = ImageView(context)
        val frameLayout = FrameLayout(requireContext())

        val matrix = Matrix().apply {
            postTranslate(valueX, valueY)
        }

        val resource = requireContext().resources?.getIdentifier("badge_gif_$id", "drawable", requireContext().packageName)
        //val resource = requireContext().resources?.getIdentifier("badge_gif_40007", "drawable", requireContext().packageName)

        badge.apply {
            layoutParams = ViewGroup.LayoutParams(
                BADGE_SIZE,
                BADGE_SIZE
            )

            Glide.with(context)
                .load(resource)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.feed_animation_loading)
                        .error(R.drawable.myenv_image_empty)
                ).into(this)
            scaleType = ImageView.ScaleType.MATRIX
            imageMatrix = matrix
        }

        frameLayout.apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        binding.displayLayout.apply {
            addView(badge, frameLayout.layoutParams)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}