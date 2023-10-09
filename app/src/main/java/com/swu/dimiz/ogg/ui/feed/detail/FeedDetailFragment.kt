package com.swu.dimiz.ogg.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.play.integrity.internal.f
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.common.dialog.SystemWindow
import com.swu.dimiz.ogg.contents.listset.ListDetailWindow
import com.swu.dimiz.ogg.databinding.FragmentFeedDetailBinding
import com.swu.dimiz.ogg.databinding.FragmentGraphBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.ui.feed.FeedViewModel

class FeedDetailFragment : Fragment() {

    private var _binding: FragmentFeedDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: FeedViewModel by activityViewModels()

    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_feed_detail, container, false)

        //val feed = FeedDetailFragmentArgs.fromBundle(requireArguments()).selectedItem

        val firesDB= Firebase.firestore
        val fireUser = Firebase.auth.currentUser

        var id = requireArguments().getString("id")

        if(id!=null){  //todo 여기 다시보기
            firesDB.collection("Feed").document(id).get().addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    var feed=task.result?.toObject(Feed::class.java)
                    Glide.with(this).load(feed?.imageUrl).into(binding.imageFeedDetail)
                    //binding.textTitleFeedDetail.text =  변환
                    //활동 코드
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonExit.setOnClickListener { item: View ->
            item.findNavController().navigateUp()
        }

        viewModel.navigateToReport.observe(viewLifecycleOwner) {
            it?.let {
                addWindow()
                viewModel.onReportCompleted()
            }
        }
    }

    private fun addWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_feed, SystemWindow(
                getString(R.string.feed_text_report_title),
                getString(R.string.feed_text_report_body),
                getString(R.string.feed_text_report_left_button),
                getString(R.string.feed_text_report_right_button)))
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}