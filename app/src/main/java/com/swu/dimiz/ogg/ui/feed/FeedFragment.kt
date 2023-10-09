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

    lateinit var feedList:ArrayList<Feed>

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
            fireGetFeed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateToSelectedItem.observe(viewLifecycleOwner) {
            it?.let {
                view?.findNavController()
                    ?.navigate(com.swu.dimiz.ogg.R.id.action_navigation_feed_to_destination_feed_detail)
                viewModel.onFeedDetailCompleted()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                             firebase 피드리스트 받기
    // 필터링은 전체/에너지/소비/이동수단/자원순환 + 내가 올린 글
    // 이렇게 총 6가지이고
    // 필터링만 바꿔서 나의 피드로 들어감



    //firestore에서 이미지 url을 받아옴
    private val fireDB = Firebase.firestore
    var gotFeed = Feed()
   fun fireGetFeed(){
       fireDB.collection("Feed")
            .get()
            .addOnSuccessListener {result ->
                for (document in result) {
                    val feed = document.toObject<Feed>()
                    gotFeed.id = document.id.toInt()
                    gotFeed.imageUrl = feed.imageUrl
                    gotFeed.actCode = feed.actCode
                    Timber.i(feed.imageUrl)
                    feedList.add(gotFeed)
                }
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }
}