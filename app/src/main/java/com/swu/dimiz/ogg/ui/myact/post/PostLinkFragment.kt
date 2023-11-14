package com.swu.dimiz.ogg.ui.myact.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentPostLinkBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyBadge
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyExtra
import timber.log.Timber
import java.util.ArrayList

class PostLinkFragment : Fragment() {

    private var _binding: FragmentPostLinkBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    val fireDB = Firebase.firestore
    val fireUser = Firebase.auth.currentUser

    private val userEmail = fireUser?.email.toString()

    private var startDate = 0L
    private var projectCount = 0

    var getDate : Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_post_link, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.buttonPost1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.forest.go.kr/"))
            startActivity(intent)
        }
        binding.buttonPostLink.setOnClickListener {
            // 산림 링크 파이어베이스 올리기
            // 기본정보 받기
            fireDB.collection("User").document(userEmail)
                .get().addOnSuccessListener { document ->
                    if (document != null) {
                        val gotUser = document.toObject<MyCondition>()
                        gotUser?.let {
                            startDate = gotUser.startDate
                            projectCount = gotUser.projectCount

                            fireUpdateAll()
                            fireUpdateBadgeDate()
                        }
                    } else { Timber.i("사용자 기본정보 받아오기 실패") }
                }.addOnFailureListener { exception -> Timber.i(exception.toString()) }

            it.findNavController().navigateUp()
        }
    }

    private fun fireUpdateAll(){
        getDate = System.currentTimeMillis()
        //Extra

        val extra = MyExtra(
            extraID = 30002,
            strDay = getDate,
        )
        fireDB.collection("User").document(userEmail)
            .collection("Extra").document("30002")
            .set(extra)
            .addOnSuccessListener { Timber.i("Extra firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        //AllAct
        val washingtonRef = fireDB.collection("User").document(userEmail)
            .collection("Project${projectCount}").document("Entire")
            .collection("AllAct").document("30002")
        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        //extra  특별활동 순위 비교 위함
        fireDB.collection("User").document(userEmail)
            .update("extraPost", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("extraPost 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        //배지
        //자원순환
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40010")
            .update("count", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("40010 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    private val counts = ArrayList<MyBadge>()
    private fun fireUpdateBadgeDate() {
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40026")
            .update("count", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("40026 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        //카테고리
        fireDB.collection("User").document(userEmail)
            .collection("Badge")
            .orderBy("badgeID")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Timber.i("${document.id} => ${document.data}")
                    val gotBadge = document.toObject<MyBadge>()
                    counts.add(gotBadge)
                }
                for(i in 0 until counts.size){
                    getDate = System.currentTimeMillis()
                    //카테고리
                    if(counts[9].count == 100 && counts[9].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40010")
                            .update("getDate", getDate)
                            .addOnSuccessListener { Timber.i("40010 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    if(counts[25].count == 1 && counts[25].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40026")
                            .update("getDate", getDate)
                            .addOnSuccessListener { Timber.i("40026 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}