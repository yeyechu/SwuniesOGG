package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.ListHolderAdapter
import com.swu.dimiz.ogg.databinding.FragmentListsetBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyList
import timber.log.Timber

class ListsetFragment : Fragment() {

    private var _binding : FragmentListsetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }
    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    private var projectCount:Int = 0

    private lateinit var listHolderAdapter: ListHolderAdapter
    private lateinit var numberHolderAdapter: NumberHolderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listset, container, false)

        val listView: GridView = binding.listHolder
        //val numberView: TextView = binding.root.findViewById(R.id.text_number)

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                      버튼 인터랙션
        viewModel.progressBar.observe(viewLifecycleOwner) {
            binding.progressBar.progress = it
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it?.let {
                addWindow()
                viewModel.completedPopup()
            }
        }

        viewModel.listHolder.observe(viewLifecycleOwner) {
            it?.let {
                listHolderAdapter = ListHolderAdapter(requireContext(), it)
                listView.adapter = listHolderAdapter
            }
        }

        viewModel.numberHolder.observe(viewLifecycleOwner) {
            it?.let {
                numberHolderAdapter = NumberHolderAdapter(requireContext(), it)
                //numberView.adapter = numberHolderAdapter
            }
        }

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
        //                     완료 버튼을 누르면 저장 후 화면을 이동시키는 관찰자
        viewModel.navigateToSave.observe(viewLifecycleOwner, Observer { shouldNavigate ->
            if(shouldNavigate) {
                navController.navigate(R.id.navigation_env)
                viewModel.onNavigatedToSave()
                Timber.i("완료 버튼 클릭")
                // 메모리 누수 확인 필요

                //프로젝트 진행상태 업데이트(새로 시작인지 수정인지 구분 추가)
                //projectCount =+ 1


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

        navController = findNavController()
        fragmentManager = childFragmentManager

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun addWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_listset, ListDetailWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}