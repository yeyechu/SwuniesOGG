package com.swu.dimiz.ogg.ui.myact.daily

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.ListsetViewModel
import com.swu.dimiz.ogg.contents.listset.listutils.ListsetAdapter
import com.swu.dimiz.ogg.databinding.LayerDailyBinding
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import com.swu.dimiz.ogg.ui.myact.MyActFragmentDirections
import com.swu.dimiz.ogg.ui.myact.MyActViewModel
import timber.log.Timber

class DailyLayer : Fragment() {

    private var _binding: LayerDailyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by activityViewModels()
    private val myActiViewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }
    private val listViewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_daily, container, false
        )

        viewModel.userCondition.observe(viewLifecycleOwner) {
            listViewModel.copyUserCondition(it)
            myActiViewModel.copyUserCondition(it)

            if(it.aim > 0f) {
                listViewModel.setCo2(it.aim)
                listViewModel.fireGetDaily()
                listViewModel.getTodayList()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val typeface = Typeface.create(
            ResourcesCompat.getFont(requireContext(), R.font.nanumsquare_b),
            Typeface.BOLD
        )

        viewModel.todayCo2.observe(viewLifecycleOwner) {
            val textDecorator =
                SpannableStringBuilder.valueOf(getString(R.string.myact_text_reduced_co2, it))

            binding.textCo2Zero.text = textDecorator.apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary_blue
                        )
                    ), 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setSpan(TypefaceSpan(typeface), 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val adapter = DailyCardAdapter(listViewModel, ListsetAdapter.ListClickListener {
            myActiViewModel.showDaily(it)
        })

        listViewModel.todayHolder.observe(viewLifecycleOwner) {
            binding.todayCardList.adapter = adapter
            adapter.submitList(it)
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
                    MyActFragmentDirections.actionNavigationMyactToDestinationListset()
                )
                viewModel.onNavigatedToListset()
            }
        }

        viewModel.progressDaily.observe(viewLifecycleOwner) {
            binding.progressDaily.progress = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}