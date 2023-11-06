package com.swu.dimiz.ogg.ui.env.badges

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.BadgeLocation
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyBadge
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import kotlin.collections.ArrayList

class BadgeListViewModel(private val repository: OggRepository) : ViewModel() {
    val fireDB = Firebase.firestore
    val fireUser = Firebase.auth.currentUser

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                        배지 리스트
    private val _badgeFilter = MutableLiveData<List<String>>()
    val badgeFilter: LiveData<List<String>>
        get() = _badgeFilter

    private val _navigateToSelected = MutableLiveData<Badges?>()
    val navigateToSelected: LiveData<Badges?>
        get() = _navigateToSelected

    private val _badgeId = MutableLiveData<Badges?>()
    val badgeId: LiveData<Badges?>
        get() = _badgeId

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                       배지 인벤토리
    val inventory: LiveData<List<Badges>> = repository.getInventory()

    private var inventoryList = ArrayList<Badges>()
    private var displayList = ArrayList<Badges>()

    private val _adapterList = MutableLiveData<List<Badges>?>()
    val adapterList: LiveData<List<Badges>?>
        get() = _adapterList

    private val _detector = MutableLiveData<Boolean>()
    val detector: LiveData<Boolean>
        get() = _detector

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                         배지 위치
    var badgeList = ArrayList<BadgeLocation>()

    private val unknownBadge: Badges = Badges(0, "", "알 수 없는 배지", "", null, null, 0L, 0, 0, "", "")

    init {
        Timber.i("created")
        getFilters()
        _detector.value = false
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                         UI용 매핑
    val inventorySize = inventory.map {
        it.size
    }

    val inventoryNull = inventorySize.map {
        it == 0
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       인벤토리 세팅용
    fun initInventoryList() {
        _adapterList.value = inventory.value
        inventoryList.clear()
        displayList.clear()
        _adapterList.value?.forEach {
            inventoryList.add(it)
        }
    }

    private fun setInventoryList(list: List<Badges>) {
        _adapterList.value = list
    }

    fun inventoryOut(item: Badges) {
        inventoryList.remove(item)
        displayList.add(item)
        setInventoryList(inventoryList)

        Timber.i("inventoryOut() displayList : $displayList")
    }

    fun inventoryIn(item: Badges) {
        inventoryList.add(item)
        displayList.remove(item)
        setInventoryList(inventoryList)

        Timber.i("inventoryIn() displayList : $displayList")
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      배지 위치 저장
    fun initLocationList() {
        badgeList.clear()
        inventoryList.forEach {
            badgeList.add(BadgeLocation(it.badgeId, 0f, 0f))
        }
        Timber.i("배지리스트 초기화: $badgeList")
    }

    fun badgeItem(id: Int): Badges {
        var badge: Badges = unknownBadge

        inventoryList.forEach {
            if(it.badgeId == id) {
                badge = it
            }
        }
        return badge
    }

    fun updateLocationList(id: Int, x: Float, y: Float) {
        badgeList.forEach {
            if(it.bId == id) {
                it.bx = x
                it.by = y
            }
        }
        Timber.i("배지리스트 업데이트: $badgeList")
    }

    fun deleteLocationList(id: Int) {
        badgeList.forEach {
            if(it.bId == id) {
                it.bx = 0f
                it.by = 0f
            }
        }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       인터랙션 감지
    fun onChangeDetected() {
        _detector.value = true
    }

    fun onSaveCompleted() {
        _detector.value = false
    }

    private fun showPopup(badge: Badges) {
        _navigateToSelected.value = badge
        _badgeId.value = badge
    }

    fun completedPopup() {
        _navigateToSelected.value = null
    }

    fun showBadgeDetail(badge: Int) = viewModelScope.launch {
        val badgeItem: Badges = repository.getBadge(badge)
        showPopup(badgeItem)
    }

    fun noClick() {}

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                     룸 데이터베이스
    private fun getFilters() = viewModelScope.launch {
        try {
            _badgeFilter.value = repository.getFilter()
        } catch (e: IOException) {
            _badgeFilter.value = listOf()
        }
    }

    // 파이어베이스에서 오는 배지 -> 룸으로 업데이트 하는 메서드
    private fun updateBadgeFire(badge: MyBadge) = viewModelScope.launch {
        repository.updateBadge(badge.badgeID!!, badge.getDate!!, badge.count)
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      파이어베이스
    //겟데이트가 있는지 카운트 업데이트
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                BadgeListViewModel(repository = repository)
            }
        }
    }

    //파이어베이스에 저장
    fun saveLocationToFirebase(){
        badgeList.forEach{
            var badgeID = 0
            var valueX = 0.0
            var valueY = 0.0
            Timber.i("saveLocationToFirebase: $it")
            badgeID = it.bId
            valueX = it.bx.toDouble()
            valueY = it.by.toDouble()

            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document(badgeID.toString())
                .update("valueX", valueX)
                .addOnSuccessListener { Timber.i("$badgeID valueX 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document(badgeID.toString())
                .update("valueY", valueY)
                .addOnSuccessListener { Timber.i("$badgeID valueY 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
        }
    }

    //획득한 배지 가져오기
    fun getHaveBadge(){
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Badge")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                value!!.forEach {
                    //전체 배지
                    val gotBadge = it.toObject<MyBadge>()
                    gotBadge.count *= 1000
                    if(gotBadge.getDate != null){
                        //획득한 배지만
                        updateBadgeFire(gotBadge)
                    }
                    Timber.i("gotBadge $gotBadge")
                }
            }
    }
}