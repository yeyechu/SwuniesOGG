package com.swu.dimiz.ogg.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.DialogFeedReportBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyReport
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
        val userEmail = OggApplication.auth.currentUser!!.email.toString()

        view?.let { OggSnackbar.make(it, getText(R.string.feed_toast_report_completed).toString()).show() }

        fireDB.collection("User").document(userEmail)
            .collection("Report")
            .whereEqualTo("feedId", feedId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Timber.i("이미 신고된 피드")
                    view?.let { OggSnackbar.make(it, getText(R.string.feed_toast_report_already).toString()).show() }

                }
                if (result.isEmpty) {
                    val report = MyReport(feedId)

                    Timber.i("피드 신고 완료")
                    fireDB.collection("User").document(userEmail)
                        .collection("Report").document(feedId)
                        .set(report)
                        .addOnSuccessListener { Timber.i("MyReport 업데이트 완료") }
                        .addOnFailureListener { e -> Timber.i(e) }

                    fireDB.collection("Feed").document(feedId)
                        .update("reactionReport", FieldValue.increment(1))
                        .addOnSuccessListener { Timber.i("신고 반응 올리기 완료") }
                        .addOnFailureListener { e -> Timber.i(e) }

                    fireDB.collection("User").document(userEmail)
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
}