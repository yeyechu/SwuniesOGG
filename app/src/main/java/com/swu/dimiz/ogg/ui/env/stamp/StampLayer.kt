package com.swu.dimiz.ogg.ui.env.stamp

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.buildSpannedString
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.DATE_WHOLE
import com.swu.dimiz.ogg.contents.listset.StampData
import com.swu.dimiz.ogg.databinding.LayerStampBinding
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import timber.log.Timber

class StampLayer : Fragment() {

    private var _binding: LayerStampBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by viewModels({ requireParentFragment() })

    private lateinit var stampHolderAdapter: StampAdapter
    private var stampHolder = ArrayList<StampData>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_stamp, container, false)

        val stampView: GridView = binding.stampGrid
        val typeface = Typeface.create(ResourcesCompat.getFont(requireContext(), R.font.gmarketsans_m), Typeface.BOLD)

        stampInitialize()
        viewModel.setStampHolder(stampHolder)

        viewModel.fakeDate.observe(viewLifecycleOwner) {
            val textDecorator = SpannableStringBuilder.valueOf(getString(R.string.stamplayout_text_title, it))
            binding.textDateOf21.text = textDecorator.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setSpan(TypefaceSpan(typeface), 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    setSpan(TypefaceSpan(typeface), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }

        viewModel.leftCo2.observe(viewLifecycleOwner) {
            val textDecorator = SpannableStringBuilder.valueOf(getString(R.string.stamplayout_text_body, it))

            //val startIndex = textDecorator.indexOfFirst { char -> Character.isDefined(char) }
            //val endIndex = textDecorator.indexOfLast { char -> Character.isDefined(char) }

            binding.textCo2Left.text = textDecorator.apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_blue)), 9, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                setSpan(StyleSpan(Typeface.BOLD), 9, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    setSpan(TypefaceSpan(typeface), 9, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                }
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    d
        viewModel.stampHolder.observe(viewLifecycleOwner) {
            it?.let {
                stampHolderAdapter = StampAdapter(requireContext(), it)
                stampView.adapter = stampHolderAdapter
            }
        }

        viewModel.progressBar.observe(viewLifecycleOwner) {
            binding.progressEnv.progress = it
        }

        return binding.root
    }

    private fun stampInitialize() {
        stampHolder.clear()
        var index = 1

        for (i in 1..DATE_WHOLE) {
            stampHolder.add(StampData(index++, 0f))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.condition = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}