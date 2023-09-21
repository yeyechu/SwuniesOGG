package com.swu.dimiz.ogg.ui.myact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.skydoves.balloon.*
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentMyActBinding
import com.swu.dimiz.ogg.ui.myact.myactcard.*

import timber.log.Timber

class MyActFragment : Fragment() {

    private var _binding: FragmentMyActBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MyActViewModel
    private lateinit var navController: NavController

    private var balloon_sus: Balloon? = null // Balloon 변수 추가
    private var balloon_extra: Balloon? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_act, container, false)

        navController = findNavController()

        val application = requireNotNull(this.activity).application
        val viewModelFactory = MyActViewModelFactory((application as OggApplication).repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MyActViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner




        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                   btnEdit 이동

        binding.buttonTodayActEdit.setOnClickListener { view: View ->
            view.findNavController().navigate(
                MyActFragmentDirections
                    .actionNavigationMyactToDestinationListchanger()
            )
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                   tooltip
        //tooltip 버튼
        binding.sustainableActInfo.setOnClickListener {
            balloon_sus?.showAlignBottom(binding.sustainableActInfo)

        }
        binding.extraActInfo.setOnClickListener {
            balloon_extra?.showAlignBottom(binding.extraActInfo)

        }


        // balloon 객체 생성
        balloon_sus = createBalloon(requireContext()) {
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
        balloon_extra = createBalloon(requireContext()) {
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

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val adapter = TodayCardItemAdapter(requireContext())

        binding.todayCardList.adapter = adapter

        viewModel.getAllData.observe(viewLifecycleOwner, Observer {
            Timber.i("$it")
            it?.let {
                adapter.submitList(it)
            }
        })

        val adaptersus = SusCardItemAdapter(requireContext())
        binding.sustainableActList.adapter = adaptersus

        viewModel.getSusData.observe(viewLifecycleOwner, Observer {
            Timber.i("$it")
            it?.let {
                adaptersus.submitList(it)
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




        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}
