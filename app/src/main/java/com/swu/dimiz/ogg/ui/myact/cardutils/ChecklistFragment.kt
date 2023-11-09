package com.swu.dimiz.ogg.ui.myact.cardutils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.convertToDuration
import com.swu.dimiz.ogg.databinding.FragmentChecklistBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyBadge
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyDaily
import timber.log.Timber
import java.util.ArrayList

class ChecklistFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val contactsList : List<Checklist> = listOf(
        Checklist("급제동, 급출발 하지 않기", "나무 4.0그루만큼을 살릴 수 있어요"),
        Checklist("불필요한 엔진 공회전 하지 않기", "나무 6.2그루만큼을 살릴 수 있어요"),
        Checklist("경제속도(60~80km/h) 준수하기", "나무 10.0그루만큼을 살릴 수 있어요"),
        Checklist("불필요한 짐 싣고 다니지 않기", "나무 8.5그루만큼을 살릴 수 있어요"),
        Checklist("내리막길 운전 시 가속패달 밝지 않기", "나무 7.3그루만큼을 살릴 수 있어요"),
        Checklist("신호대기 시 기어를 중립으로 놓기", "나무 2.0그루만큼을 살릴 수 있어요")
    )

    val fireDB = Firebase.firestore
    val fireUser = Firebase.auth.currentUser

    private val userEmail = fireUser?.email.toString()

    private var startDate = 0L
    var today = 0
    private var projectCount = 0

    var getDate : Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView = binding.mRecyclerView
        val adapter = ChecklistAdapter(contactsList)

        // 리사이클러뷰에 레이아웃 관리자 연결
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        //체크리스트 파이어베이스 올리기
        binding.envButtonStart.setOnClickListener {
            //기본정보 받기
            fireDB.collection("User").document(userEmail)
                .get().addOnSuccessListener { document ->
                    if (document != null) {
                        val gotUser = document.toObject<MyCondition>()
                        gotUser?.let {
                            startDate = gotUser.startDate
                            today = convertToDuration(startDate)
                            projectCount = gotUser.projectCount

                            fireUpdateAll()
                            fireUpdateBadgeDate()
                            updateBadgeDateCo2()
                        }
                    } else { Timber.i("사용자 기본정보 받아오기 실패") }
                }.addOnFailureListener { exception -> Timber.i(exception.toString()) }
        }

        return view
    }
    private fun fireUpdateAll(){
        //Daily
        getDate = System.currentTimeMillis()

        val daily = MyDaily(
            dailyID = 10011,
            upDate = getDate
        )
        fireDB.collection("User").document(userEmail)
            .collection("Project${projectCount}").document("Daily")
            .collection(today.toString()).document(getDate.toString())
            .set(daily)
            .addOnSuccessListener { Timber.i("Daily firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        //AllAct
        val washingtonRef = fireDB.collection("User").document(userEmail)
            .collection("Project${projectCount}").document("Entire")
            .collection("AllAct").document("10011")
        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        washingtonRef
            .update("allCo2", FieldValue.increment(0.28))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        //스탬프
        fireDB.collection("User").document(userEmail)
            .collection("Project${projectCount}").document("Entire")
            .collection("Stamp").document(today.toString())
            .update("dayCo2", FieldValue.increment(0.28))
            .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        //배지
        //이동수단
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40009")
            .update("count", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("40009 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        //Co2
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40022")
            .update("count", FieldValue.increment(280))
            .addOnSuccessListener { Timber.i("40022 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40023")
            .update("count", FieldValue.increment(280))
            .addOnSuccessListener { Timber.i("40023 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40024")
            .update("count", FieldValue.increment(280))
            .addOnSuccessListener { Timber.i("40024 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    private val counts = ArrayList<MyBadge>()
    private fun fireUpdateBadgeDate(){
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
                    if(counts[8].count == 100 && counts[8].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40009")
                            .update("getDate", getDate)
                            .addOnSuccessListener { Timber.i("40009 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

    private fun updateBadgeDateCo2(){
        fireDB.collection("User").document(userEmail)
            .collection("Badge")
            .whereEqualTo("badgeID", "40022")
            .whereEqualTo("badgeID", "40023")
            .whereEqualTo("badgeID", "40024")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (doc in value!!) {
                    val gotBadge = doc.toObject<MyBadge>()

                    if(gotBadge.badgeID == 40022 && gotBadge.getDate == null){
                        if(gotBadge.count >= 100000){
                            getDate = System.currentTimeMillis()
                            fireDB.collection("User").document(userEmail)
                                .collection("Badge").document("40022")
                                .update("getDate", getDate)
                                .addOnSuccessListener { Timber.i("40022 획득 완료") }
                                .addOnFailureListener { exeption -> Timber.i(exeption) }
                        }
                    }
                    else if(gotBadge.badgeID == 40023 && gotBadge.getDate == null){
                        if(gotBadge.count >= 500000){
                            getDate = System.currentTimeMillis()
                            fireDB.collection("User").document(userEmail)
                                .collection("Badge").document("40023")
                                .update("getDate", getDate)
                                .addOnSuccessListener { Timber.i("40023 획득 완료") }
                                .addOnFailureListener { exeption -> Timber.i(exeption) }
                        }
                    }
                    else if(gotBadge.badgeID == 40024 && gotBadge.getDate == null){
                        if(gotBadge.count >= 1000000){
                            getDate = System.currentTimeMillis()
                            fireDB.collection("User").document(userEmail)
                                .collection("Badge").document("40024")
                                .update("getDate", getDate)
                                .addOnSuccessListener { Timber.i("40024 획득 완료") }
                                .addOnFailureListener { exeption -> Timber.i(exeption) }
                        }
                    }
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}