package com.swu.dimiz.ogg.ui.myact.daily

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.ListDetailWindow
import com.swu.dimiz.ogg.contents.listset.ListsetViewModel
import com.swu.dimiz.ogg.databinding.LayerDailyBinding
import com.swu.dimiz.ogg.ui.env.EnvFragmentDirections
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import com.swu.dimiz.ogg.ui.myact.MyActFragmentDirections
import com.swu.dimiz.ogg.ui.myact.myactcard.TodayCardItemAdapter
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity
import timber.log.Timber

class DailyLayer : Fragment() {

    private var _binding: LayerDailyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by activityViewModels()
    private val listViewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }

    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    private lateinit var cameraTitle: String
    private lateinit var cameraCo2: String
    private lateinit var cameraId: String
    private lateinit var cameraFilter: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_daily, container, false)

        val mainActivity = activity as MainActivity
        val typeface = Typeface.create(ResourcesCompat.getFont(requireContext(), R.font.gmarketsans_m), Typeface.BOLD)

        viewModel.fakeToday.observe(viewLifecycleOwner) {
            val textDecorator = SpannableStringBuilder.valueOf(getString(R.string.myact_text_reduced_co2, it))

            binding.textCo2Zero.text = textDecorator.apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_blue)), 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                //setSpan(StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setSpan(TypefaceSpan(typeface), 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }

        viewModel.navigateToStartFromMyAct.observe(viewLifecycleOwner) {
            if (it) {
                navController.navigate(R.id.destination_listaim)
                viewModel.onNavigatedToStartFromMyAct()
            }
        }
        viewModel.navigateToListset.observe(viewLifecycleOwner) {
            if (it) {
                navController.navigate(
                    MyActFragmentDirections.actionNavigationMyactToDestinationListset())
                viewModel.onNavigatedToListset()
            }
        }
        viewModel.progressDaily.observe(viewLifecycleOwner) {
            binding.progressDaily.progress = it
        }


        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터

        val adapter = TodayCardItemAdapter(requireContext())
        binding.todayCardList.adapter = adapter

//        viewModel.getAllData.observe(viewLifecycleOwner) {
//            it?.let {
//                adapter.submitList(it)
//            }
//        }
        viewModel.navigateToDaily.observe(viewLifecycleOwner) {
            if (it == null) {
                mainActivity.hideBottomNavView(false)
            } else {
                mainActivity.hideBottomNavView(true)
                addWindow()
            }
        }

        viewModel.dailyId.observe(viewLifecycleOwner) {
            it?.let {
                cameraTitle = it.title
                cameraCo2 = it.co2.toString()
                cameraId = it.dailyId.toString()
                cameraFilter = it.filter
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateToToCamera.observe(viewLifecycleOwner) {
            if(it) {
                val intent = Intent(context, CameraActivity::class.java)
                intent.putExtra("title", cameraTitle)
                intent.putExtra("co2", cameraCo2)
                intent.putExtra("id", cameraId)
                intent.putExtra("filter", cameraFilter)
                requireContext().startActivity(intent)
                viewModel.onCameraCompleted()
            }
        }

        viewModel.navigateToToChecklist.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(MyActFragmentDirections.actionNavigationMyactToDestinationChecklist())
                viewModel.onChecklistCompleted()
                fragmentManager.popBackStack()
            }
        }

        viewModel.navigateToToGallery.observe(viewLifecycleOwner) {
            if(it) {
                viewModel.onGalleryCompleted()
                fragmentManager.popBackStack()
            }
        }
    }

    private fun addWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_myact, ListDetailWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}