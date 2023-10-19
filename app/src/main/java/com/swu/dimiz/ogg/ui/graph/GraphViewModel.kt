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
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyAllAct
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyGraph
import timber.log.Timber

class GraphViewModel : ViewModel()
{
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       파이어베이스 변수
    val fireDB = Firebase.firestore
    val fireUser = Firebase.auth.currentUser

    //──────────────────────────────────────────────────────────────────────────────────────
    //
    //myact

    //certify

    //special
    private val _graph = MutableLiveData<MyGraph>()
    val graph: LiveData<MyGraph>
        get() = _graph

    fun getGraph() {
        //_graph.value!!.co21 =
    }
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       전체활동 가져오기
    private val docRef = fireDB.collection("User").document(fireUser?.email.toString()).collection("AllAct")
    fun fireGetCategory(){
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
                    if(act.actCode == "에너지"){
                        energyCo2 += act.allC02
                    }
                    else if(act.actCode == "소비"){
                        consumptionCo2 += act.allC02
                    }
                    else if(act.actCode == "이동수단"){
                        transportCo2 += act.allC02
                    }
                    else if(act.actCode == "자원순환"){
                        resourceCo2 += act.allC02
                    }
                }
            }
            //각 카테고리별 Co2합
            Timber.i("energyCo2 $energyCo2")
            Timber.i("consumptionCo2 $consumptionCo2")
            Timber.i("transportCo2 $transportCo2")
            Timber.i("resourceCo2 $resourceCo2")
        }
    }

    fun fireGetCo2(){
        var co2ActList = arrayListOf<MyAllAct>()
        docRef.orderBy("allC02",  Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        Timber.i("${dc.document.id} => ${dc.document.data}")
                        var act = dc.document.toObject<MyAllAct>()
                        co2ActList.add(act)  //여기에 123위 순서대로 담겨있음
                    }
                }
                //분리한다면 아래 같음
                val firstId = co2ActList[0].ID
                val secondId = co2ActList[1].ID
                val thirdId = co2ActList[2].ID
                val firstCo2 = co2ActList[0].allC02
                val secondCo2 = co2ActList[1].allC02
                val thirdCo2 = co2ActList[2].allC02
            }
    }

    fun fireGetReaction(){
        // 피드에서 아이디 검색해서 리엑션 3개 더한값 가져옴
        val gotMyFeedList = arrayListOf<Feed>()
        val sumReactionList = arrayListOf<Int>()

        fireDB.collection("Feed")
            .orderBy("postTime",  Query.Direction.DESCENDING)
            .whereEqualTo("email", fireUser?.email.toString())
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                gotMyFeedList.clear()
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val feed = dc.document.toObject<Feed>()
                        feed.id = dc.document.id
                        gotMyFeedList.add(feed)
                    }
                }
            }

        // 순위비교
    }
}