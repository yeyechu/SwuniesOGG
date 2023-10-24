package com.swu.dimiz.ogg.ui.graph

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.convertDurationToInt
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyAllAct
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyGraph
import timber.log.Timber

class GraphViewModel : ViewModel()
{
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       파이어베이스 변수
    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    var projectCount = 0
    var startDate = 0L
    //──────────────────────────────────────────────────────────────────────────────────────
    //
    //myact
    private val _energyCo2 = MutableLiveData<Float>()
    val energyCo2: LiveData<Float>
        get() = _energyCo2

    private val _consumptionCo2 = MutableLiveData<Float>()
    val consumptionCo2: LiveData<Float>
        get() = _consumptionCo2

    private val _transportCo2 = MutableLiveData<Float>()
    val transportCo2: LiveData<Float>
        get() = _transportCo2

    private val _resourceCo2 = MutableLiveData<Float>()
    val resourceCo2: LiveData<Float>
        get() = _resourceCo2


    private val _co2ActList = MutableLiveData<List<MyAllAct>>()
    val co2ActList: LiveData<List<MyAllAct>>
        get() = _co2ActList

    //certify
    private val _gotMyFeedList = MutableLiveData<List<Feed>>()
    val gotMyFeedList: LiveData<List<Feed>>
        get() = _gotMyFeedList

    private val _sumReactionList = MutableLiveData<List<Int>>()
    val sumReactionList: LiveData<List<Int>>
        get() = _sumReactionList

    private val _myPost = MutableLiveData<MyGraph?>()
    val myPost: LiveData<MyGraph?>
        get() = _myPost


    //special
    private val _extraRank = MutableLiveData<Float>()
    val extraRank: LiveData<Float>
        get() = _extraRank



    //certify

    //special
    private val _graph = MutableLiveData<MyGraph>()
    val graph: LiveData<MyGraph>
        get() = _graph

    fun getGraph() {
        //_graph.value!!.co21 =
    }

    fun fireInfo(){
        fireDB.collection("User").document(fireUser?.email.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i( e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    var gotUser = snapshot.toObject<MyCondition>()!!
                    projectCount = gotUser.projectCount
                    startDate = gotUser.startDate
                    Timber.i("projectCount $projectCount")
                    fireGetCategory()
                    fireGetCo2()
                    fireGetReaction()
                    fireGetMostUp()
                } else {
                    Timber.i("Current data: null")
                }
            }
    }
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       전체활동 가져오기
    //todo sust 한번만 들어가는 문제
    fun fireGetCategory(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")
        //에너지
        var energyCo2 = 0.0
        var consumptionCo2 = 0.0
        var transportCo2 = 0.0
        var resourceCo2 = 0.0

        docRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Timber.i(e)
                return@addSnapshotListener
            }
            for (dc in snapshots!!.documentChanges) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    var act = dc.document.toObject<MyAllAct>()
                    if (act.actCode == "에너지") {
                        energyCo2 += act.allCo2.toFloat() * 1000
                    } else if (act.actCode == "소비") {
                        consumptionCo2 += act.allCo2.toFloat() * 1000
                    } else if (act.actCode == "이동수단") {
                        transportCo2 += act.allCo2.toFloat() * 1000
                    } else if (act.actCode == "자원순환") {
                        resourceCo2 += act.allCo2.toFloat() * 1000
                    }
                }
            }
            //각 카테고리별 Co2합
            Timber.i("energyCo2 $energyCo2")
            Timber.i("consumptionCo2 $consumptionCo2")
            Timber.i("transportCo2 $transportCo2")
            Timber.i("resourceCo2 $resourceCo2")

            // LiveData를 업데이트
            _energyCo2.value = energyCo2.toFloat()
            _consumptionCo2.value = consumptionCo2.toFloat()
            _transportCo2.value = transportCo2.toFloat()
            _resourceCo2.value = resourceCo2.toFloat()
        }
    }

    fun fireGetCo2(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")

        var co2ActList = arrayListOf<MyAllAct>()
        docRef.orderBy("allCo2",  Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        var act = dc.document.toObject<MyAllAct>()
                        co2ActList.add(act)  //여기에 123위 순서대로 담겨있음
                    }
                }
                //분리한다면 아래 같음
                val firstId = co2ActList[0].ID
                val secondId = co2ActList[1].ID
                val thirdId = co2ActList[2].ID
                val firstCo2 = co2ActList[0].allCo2
                val secondCo2 = co2ActList[1].allCo2
                val thirdCo2 = co2ActList[2].allCo2

                Timber.i("co2ActList $co2ActList")

                _co2ActList.value = co2ActList

            }
    }

    data class feedReact(var id : String, var reactionSum : Int)
    var reactionList = arrayListOf<feedReact>()

    var resultId = ""
   /* var resultFun = 0
    var resultGreat = 0
    var resultLike = 0*/

    fun fireGetReaction(){
        reactionList.clear()
        val less =  startDate + 21000000
        fireDB.collection("Feed")
            .whereEqualTo("email", fireUser?.email.toString())
            .whereGreaterThan("postTime", startDate)
            .whereLessThan("postTime", less)
            .orderBy("postTime",  Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val feed = document.toObject<Feed>()
                    feed.id = document.id

                    var reactFun = feed.reactionFun
                    var reactGreat = feed.reactionGreat
                    var reactLike = feed.reactionLike

                    var reaccTotal = reactFun + reactGreat + reactLike

                    reactionList.add(feedReact(feed.id, reaccTotal))
                }
                //순서대로 정렬
                reactionList.sortByDescending { it.reactionSum }

                resultId = reactionList[1].id
                Timber.i("resultId $resultId")
                //todo 이미지 가져오는 쪽에서 firebase 사용해서 값 가져오기
            }
            .addOnFailureListener { exception ->
                Timber.i( exception)
            }
    }

    fun fireGetMostUp(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")

        var mostUpList = arrayListOf<Int>()

        docRef.orderBy("upCount",  Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        Timber.i("${dc.document.id} => ${dc.document.data}")
                        var act = dc.document.toObject<MyAllAct>()
                        mostUpList.add(act.ID)  //여기에 123위 순서대로 담겨있음
                    }
                }
                //분리한다면 아래 같음
                val upFirstId = mostUpList[0]
                val upSecondId = mostUpList[1]
                val upThirdId = mostUpList[2]

                Timber.i("mostUpList $mostUpList")
            }
    }


    fun updateEnergyCo2(value: Float) {
        _energyCo2.value = value
    }

    fun updateConsumptionCo2(value: Float) {
        _consumptionCo2.value = value
    }

    fun updateTransportCo2(value: Float) {
        _transportCo2.value = value
    }

    fun updateResourceCo2(value: Float) {
        _resourceCo2.value = value
    }


    fun updateCo2ActList(data: List<MyAllAct>) {
        _co2ActList.value = data
    }
}