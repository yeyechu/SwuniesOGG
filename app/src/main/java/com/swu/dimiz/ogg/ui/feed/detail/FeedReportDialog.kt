package com.swu.dimiz.ogg.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.DialogFeedReportBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyReaction
import timber.log.Timber

class FeedReportDialog(
    private val feedId: String,
    private val email: String
) : DialogFragment() {

    private var _binding: DialogFeedReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = DialogFeedReportBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.i("다이얼로그 피드 아이디: $feedId, 이메일: $email")

        binding.buttonLeft.setOnClickListener {
            dismiss()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.buttonRight.setOnClickListener {
            updateReport()
            dismiss()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.feedReportLayout.setOnClickListener { }
    }

    private fun updateReport() {
        // 파이어베이스 함수 정의
        val fireDB = Firebase.firestore
        val fireUser = Firebase.auth.currentUser


        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Reation")
            .whereEqualTo("feedId", feedId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Timber.i("이미 신고된 피드")
                }
                if (result.isEmpty) {
                    val react = MyReaction(feedId)

                    Timber.i("피드 신고 완료")
                    fireDB.collection("User").document(fireUser?.email.toString())
                        .collection("Report").document(feedId)
                        .set(react)
                        .addOnSuccessListener { Timber.i("MyReport 업데이트 완료") }
                        .addOnFailureListener { e -> Timber.i(e) }

                    fireDB.collection("Feed").document(feedId)
                        .update("reactionReport", FieldValue.increment(1))
                        .addOnSuccessListener { Timber.i("신고 반응 올리기 완료") }
                        .addOnFailureListener { e -> Timber.i(e) }

                    fireDB.collection("User").document(fireUser?.email.toString())
                        .update("report", FieldValue.increment(1))
                        .addOnSuccessListener { Timber.i("유저 신고 올리기 완료") }
                        .addOnFailureListener { e -> Timber.i(e) }
                }

            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FeedReportDialog"
    }
}