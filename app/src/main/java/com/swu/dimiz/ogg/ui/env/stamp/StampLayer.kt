package com.swu.dimiz.ogg.ui.env.stamp

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerStampBinding
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import timber.log.Timber

class StampLayer : Fragment() {

    private var _binding: LayerStampBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by activityViewModels()

    private lateinit var stampHolderAdapter: StampAdapter
    private var aim = 0f

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_stamp, container, false)

        viewModel.fireGetStamp()
        //val typeface = Typeface.create(ResourcesCompat.getFont(requireContext(), R.font.gmarketsans_m), Typeface.BOLD)

        viewModel.leftHolder.observe(viewLifecycleOwner) {
            val textDecorator = SpannableStringBuilder.valueOf(getString(R.string.stamplayout_text_body, it))

            binding.textCo2Left.text = textDecorator.apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_blue)), 9, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                setSpan(StyleSpan(Typeface.BOLD), 9, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    setSpan(TypefaceSpan(typeface), 9, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                }
            }
        }

        viewModel.userCondition.observe(viewLifecycleOwner) {
            aim = it.aim
            viewModel.setCo2(it.aim)
            Timber.i("${it.aim}")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.condition = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val stampView: GridView = binding.stampGrid

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    스탬프

        viewModel.stampHolder.observe(viewLifecycleOwner) {
            it?.let {
                stampHolderAdapter = StampAdapter(aim, it)
                stampView.adapter = stampHolderAdapter
            }
        }

        viewModel.progressWhole.observe(viewLifecycleOwner) {
            binding.progressEnv.progress = it
        }

        viewModel.co2Holder.observe(viewLifecycleOwner) {
            viewModel.leftCo2()
        }

        viewModel.fakeToday.observe(viewLifecycleOwner) {
            when(it / aim * 100) {
                0f -> binding.imageStampToday.setImageResource(R.drawable.env_image_stamp_000)
                in 1f..99f -> binding.imageStampToday.setImageResource(R.drawable.env_image_stamp_050)
                else -> binding.imageStampToday.setImageResource(R.drawable.env_image_stamp_100)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}