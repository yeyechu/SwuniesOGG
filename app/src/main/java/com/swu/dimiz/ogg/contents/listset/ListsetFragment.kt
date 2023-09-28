package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.chip.Chip
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.common.dialog.SystemWindow
import com.swu.dimiz.ogg.databinding.FragmentListsetBinding
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyList
import com.swu.dimiz.ogg.ui.env.User
import timber.log.Timber

class ListsetFragment : Fragment() {

    private var _binding : FragmentListsetBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ListsetViewModel
    private lateinit var navController: NavController

    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    private var projectCount:Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listset, container, false)

        navController = findNavController()

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ListsetViewModelFactory((application as OggApplication).repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ListsetViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                          이전 프로젝트가 몇회차 프로젝트인지 검색
        val docRef = db.collection("User").document(user?.email.toString())
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Timber.i( "DocumentSnapshot data: ${document.data}")
                val gotUser = document.toObject<MyCondition>()
                projectCount = gotUser!!.projectCount
            } else {
                Timber.i("No such document")
            }
        }.addOnFailureListener { exception ->
            Timber.i(exception.toString())
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                               활동 목표 출력 및 초기화

        val listsetFragmentArgs by navArgs<ListsetFragmentArgs>()
        binding.textAim.text = listsetFragmentArgs.aimCo2Amount.toString()

        viewModel.co2Aim.observe(viewLifecycleOwner, Observer<Float> {
            viewModel.setCo2(listsetFragmentArgs.aimCo2Amount)
        })

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                     카테고리 출력

        viewModel.activityFilter.observe(viewLifecycleOwner, object : Observer<List<String>> {

            override fun onChanged(value: List<String>) {
                value ?: return
                val chipGroup = binding.activityFilter
                val inflater = LayoutInflater.from(chipGroup.context)

                val children = value.map { category ->
                    val chip = inflater.inflate(R.layout.item_chips, chipGroup, false) as Chip
                    chip.text = category
                    chip.tag = category
                    if(category == "energy") {
                        chip.isChecked = true
                    }
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        viewModel.onFilterChanged(button.tag as String, isChecked)
                    }
                    chip
                }
                chipGroup.removeAllViews()

                for(chip in children) {
                    chipGroup.addView(chip)
                }
            }
        })

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val adapter = ActivityListAdapter(requireContext())
        binding.activityList.adapter = adapter

        viewModel.filteredList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                     완료 버튼을 누르면 저장 후 화면을 이동시키는 관찰자
        viewModel.navigateToSave.observe(viewLifecycleOwner, Observer { shouldNavigate ->
            if(shouldNavigate) {
                navController.navigate(R.id.navigation_env)
                viewModel.onNavigatedToSave()
                Timber.i("완료 버튼 클릭")
                // 메모리 누수 확인 필요

                //프로젝트 진행상태 업데이트(새로 시작인지 수정인지 구분 추가)
                //projectCount =+ 1

                // ──────────────────────────────────────────────────────────────────────────────────────
                //                                  firebase 리스트 저장
                var act1 = 10001
                var act2 = 10005
                var act3 = 10010

                var actList1 = MyList()
                actList1.setFirstList(act1)
                var actList2 = MyList()
                actList2.setFirstList(act2)
                var actList3 = MyList()
                actList3.setFirstList(act3)



                val db1 = db.collection("User").document(user?.email.toString())
                    .collection("project$projectCount").document("1")
                val db2 = db.collection("User").document(user?.email.toString())
                    .collection("project$projectCount").document("2")
                val db3 =db.collection("User").document(user?.email.toString())
                    .collection("project$projectCount").document("3")

                db.runBatch { batch ->
                    // Set the value of 'NYC'
                    batch.set(db1, actList1)
                    batch.set(db2, actList2)
                    batch.set(db3, actList3)

                }.addOnCompleteListener {
                    Timber.i("DocumentSnapshot1 successfully written!")
                }.addOnFailureListener {  e -> Timber.i("Error writing document", e)}

                /*체크리스트 받으면 아래처럼 줄일 예정
                var checkCount :Int = 3 //활동할게 몇게인지 가져오기

                for(i in 1 until checkCount){
                    var activity = 100000//체크 항목 i번

                    var actList = MyList()
                    actList.setFirstList(activity)

                    db.collection("User").document(user?.email.toString())
                        .collection("project$projectCount").document(i.toString())
                        .set(activity)
                        .addOnCompleteListener {Timber.i("DocumentSnapshot1 successfully written!")
                        }.addOnFailureListener {  e -> Timber.i("Error writing document", e)}
                }*/
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}