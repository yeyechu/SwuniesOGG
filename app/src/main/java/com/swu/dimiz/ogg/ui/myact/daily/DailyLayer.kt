package com.swu.dimiz.ogg.ui.myact.daily

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.ListsetViewModel
import com.swu.dimiz.ogg.databinding.LayerDailyBinding
import com.swu.dimiz.ogg.ui.env.EnvFragmentDirections
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import com.swu.dimiz.ogg.ui.myact.MyActFragmentDirections
import com.swu.dimiz.ogg.ui.myact.myactcard.TodayCardItemAdapter
import timber.log.Timber

class DailyLayer : Fragment() {

    private var _binding: LayerDailyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by activityViewModels()
    private val listViewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_daily, container, false)

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}