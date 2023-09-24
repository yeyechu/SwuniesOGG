package com.swu.dimiz.ogg.ui.myact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.skydoves.balloon.*
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentMyActBinding
import com.swu.dimiz.ogg.ui.myact.myactcard.*
import com.swu.dimiz.ogg.ui.myact.postsust.PostSustWindow

import timber.log.Timber

class MyActFragment : Fragment() {

    private var _binding: FragmentMyActBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MyActViewModel
    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    private var balloonSus: Balloon? = null // Balloon 변수 추가
    private var balloonExtra: Balloon? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_act, container, false)

        navController = findNavController()
        fragmentManager = childFragmentManager

        val application = requireNotNull(this.activity).application
        val viewModelFactory = MyActViewModelFactory((application as OggApplication).repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MyActViewModel::class.java)

        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel


        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                   btnEdit 이동

        binding.buttonTodayActEdit.setOnClickListener { view: View ->
            view.findNavController().navigate(
                MyActFragmentDirections
                    .actionNavigationMyactToDestinationListchanger()
            )
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val adapter = TodayCardItemAdapter(requireContext())

        binding.todayCardList.adapter = adapter

        viewModel.getAllData.observe(viewLifecycleOwner, Observer {

            it?.let {
                adapter.submitList(it)
            }
        })


        binding.sustainableActList.adapter = SustCardItemAdapter(SustCardItemAdapter.OnClickListener {
            viewModel.showPopup(it)
            Timber.i("버튼 클릭 리스너 : $it")
        })

        viewModel.navigateToSelected.observe(viewLifecycleOwner, Observer {
            Timber.i("버튼 클릭 관찰자 : $it")
            if (it == null) {
                binding.frameLayout.visibility = View.GONE
            } else {
                binding.frameLayout.visibility = View.VISIBLE
            }
        })

        val adapterextra = ExtraCardItemAdapter(requireContext())
        binding.extraActList.adapter = adapterextra

        viewModel.getExtraData.observe(viewLifecycleOwner, Observer {
            Timber.i("$it")
            it?.let {
                adapterextra.submitList(it)
            }
        })


        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                   tooltip
        //tooltip 버튼
        binding.buttonTooltipSust.setOnClickListener {
            balloonSus?.showAlignBottom(binding.buttonTooltipSust)

        }
        binding.buttonTooltipExtra.setOnClickListener {
            balloonExtra?.showAlignBottom(binding.buttonTooltipExtra)

        }

        // balloon 객체 생성
        balloonSus = createBalloon(requireContext()) {
            setHeight(BalloonSizeSpec.WRAP)
            setWidth(BalloonSizeSpec.WRAP)
            setText(getString(R.string.myact_tooltip_sustainable))
            setTextColorResource(R.color.primary_blue)
            setTextSize(15f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setArrowSize(10)
            setArrowPosition(0.5f)
            setPadding(12)
            setCornerRadius(8f)
            setBackgroundColorResource(R.color.white)
            setBalloonAnimation(BalloonAnimation.FADE)
            build()
        }
        balloonExtra = createBalloon(requireContext()) {
            setHeight(BalloonSizeSpec.WRAP)
            setWidth(BalloonSizeSpec.WRAP)
            setText(getString(R.string.myact_tooltip_extra))
            setTextColorResource(R.color.primary_blue)
            setTextSize(15f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setArrowSize(10)
            setArrowPosition(0.5f)
            setPadding(12)
            setCornerRadius(8f)
            setBackgroundColorResource(R.color.white)
            setBalloonAnimation(BalloonAnimation.FADE)
            build()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.sust.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.frameLayout.visibility = View.VISIBLE
            }
        })
    }

    private fun addWindow() {
        Timber.i("트랜잭션1")
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout, PostSustWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
        Timber.i("트랜잭션2")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}
