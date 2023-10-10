package com.swu.dimiz.ogg.ui.feed

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.TOGETHER
import com.swu.dimiz.ogg.databinding.FragmentFeedBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.ui.feed.myfeed.FeedGridAdapter
import timber.log.Timber


class FeedFragment : Fragment(){

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_feed, container, false)

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                     카테고리 출력

        viewModel.activityFilter.observe(viewLifecycleOwner) { value ->
            val chipGroup = binding.activityFilter
            val chipInflater = LayoutInflater.from(chipGroup.context)

            val children = value.map { category ->
                val chip = chipInflater.inflate(R.layout.item_chips, chipGroup, false) as Chip
                chip.text = category
                chip.tag = category
                if (category == TOGETHER) {
                    chip.isChecked = true
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                chip.setOnCheckedChangeListener { button, isChecked ->

                    viewModel.onFilterChanged(button.tag as String, isChecked)
                    button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    if (!chip.isChecked) {
                        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_light_gray))
                    }
                }
                chip
            }
            chipGroup.removeAllViews()

            for (chip in children) {
                chipGroup.addView(chip)
            }

            // ──────────────────────────────────────────────────────────────────────────────────────
            //                                  피드 리스트 출력
            viewModel.fireGetFeed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateToSelectedItem.observe(viewLifecycleOwner) {
            it?.let {
                navController.navigate(FeedFragmentDirections.actionNavigationFeedToDestinationFeedDetail())
                viewModel.onFeedDetailCompleted()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}