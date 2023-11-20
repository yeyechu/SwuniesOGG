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
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import kotlin.collections.ArrayList

class BadgeListViewModel(private val repository: OggRepository) : ViewModel() {

    val fireDB = Firebase.firestore
    private val email = OggApplication.auth.currentUser?.email.toString()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    private val _getBadgeNotification = MutableLiveData<Boolean>()
    val getBadgeNotification: LiveData<Boolean>
        get() = _getBadgeNotification
    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                         배지 위치
    var badgeList = ArrayList<BadgeLocation>()

    private val unknownBadge: Badges = Badges(0, "", "알 수 없는 배지", "", null, null, 0L, 0, 0, "", "")

    init {
        Timber.i("created")
        getFilters()
        _detector.value = false

        if(email != "") {
            getAllBadge()
        }
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

    fun setDuration(duration: Int) {
        val badges = ArrayList<Badges>()

        uiScope.launch {
            getBadges("clear")?.let { badgesList ->
                badgesList.forEach {
                    badges.add(it)
                }
            }
            badges.forEach {
                if(it.getDate == null) {
                    if(duration == it.baseValue) {
                        updateBadgeToFire(it.badgeId, true, duration)
                        onBadgeCleared()
                    } else if(duration > it.count) {
                        updateBadgeToFire(it.badgeId, false, duration)
                    }
                }
            }
        }
    }

    private fun onBadgeCleared() {
        _getBadgeNotification.value = true
    }

    fun onNotificationCompleted() {
        _getBadgeNotification.value = false
    }

    private suspend fun getBadges(filter: String): List<Badges>? {
        return withContext(Dispatchers.IO) {
            repository.getFilteredBadgeList(filter)
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

    private fun updateBadgeCountFromFirebase(badge: MyBadge) = viewModelScope.launch {
        repository.updateBadgeCount(badge.badgeID!!, badge.count)
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      파이어베이스
    private fun updateBadgeToFire(id: Int, bool: Boolean, duration: Int) = viewModelScope.launch {
        Timber.i("전달된 $id, bool: $bool")

        if( id == 40004 || id ==  40005 || id ==  40006){
            if(bool){ // bool == true일 때, 날짜 생성
                fireDB.collection("User").document(email)
                    .collection("Badge").document(id.toString())
                    .update("count", duration)
                    .addOnSuccessListener { Timber.i("$id 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }

                val getDate = System.currentTimeMillis()
                fireDB.collection("User").document(email)
                    .collection("Badge").document(id.toString())
                    .update("getDate", getDate)
                    .addOnSuccessListener { Timber.i("$id 획득 완료") }
                    .addOnFailureListener { exeption -> Timber.i(exeption) }
            }
            else{   // bool == false일 때, count만 업데이트
                fireDB.collection("User").document(email)
                    .collection("Badge").document(id.toString())
                    .update("count", duration)
                    .addOnSuccessListener { Timber.i("$id 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
        }
    }
    //파이어베이스에 저장
    fun saveLocationToFirebase(){
        badgeList.forEach{
            Timber.i("saveLocationToFirebase: $it")
            val badgeID: Int = it.bId
            val valueX: Double = it.bx.toDouble()
            val valueY: Double = it.by.toDouble()

            fireDB.collection("User").document(email)
                .collection("Badge").document(badgeID.toString())
                .update("valueX", valueX)
                .addOnSuccessListener { Timber.i("$badgeID valueX 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            fireDB.collection("User").document(email)
                .collection("Badge").document(badgeID.toString())
                .update("valueY", valueY)
                .addOnSuccessListener { Timber.i("$badgeID valueY 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
        }
    }

    //배지 전체 리스트
    fun getAllBadge() = viewModelScope.launch {
        fireDB.collection("User").document(email)
            .collection("Badge")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                value!!.forEach {
                    //전체 배지
                    val gotBadge = it.toObject<MyBadge>()

                    if(gotBadge.getDate == null) {
                        updateBadgeCountFromFirebase(gotBadge)
                    } else {
                        updateBadgeFire(gotBadge)
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("destroyed")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                BadgeListViewModel(repository = repository)
            }
        }
    }
}