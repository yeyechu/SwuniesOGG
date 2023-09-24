package com.swu.dimiz.ogg.ui.myact


import android.app.Application
import androidx.lifecycle.*
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber

class MyActViewModel (private val repository: OggRepository) : ViewModel() {
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 핸들러

    // 완료 버튼 : suspend 구현
    // ConditionRecord에 활동시작일/활동목표 저장
    private val _navigateToSelected = MutableLiveData<ActivitiesSustainable?>()
    val navigateToSelected: LiveData<ActivitiesSustainable?>
        get() = _navigateToSelected

    // 활동 체크 버튼
    // co2 받아오기, progress바 움직이기
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     데이터베이스 초기화
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val getAllData: LiveData<List<ActivitiesDaily>> = repository.getAlldata.asLiveData()
    val getSustData: LiveData<List<ActivitiesSustainable>> = repository.getAllSusts.asLiveData()
    val getExtraData: LiveData<List<ActivitiesExtra>> = repository.getAllextras.asLiveData()

    private val _sust = MutableLiveData<ActivitiesSustainable>()
    val sust: LiveData<ActivitiesSustainable>
        get() = _sust

    // 1. ConditionRecord에서 차량정보 가져오기
    // -> init{}에서 호출 후 suspend로 구현

    val automobile = 0

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                   필요한 데이터 초기화


    // ───────────────────────────────────────────────────────────────────────────────────
    //                                      뷰모델 초기화
    init {
        Timber.i("created")

    }


    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 리스너

    fun showPopup(sust: ActivitiesSustainable) {
        _navigateToSelected.value = sust
        _sust.value = sust

    }

    fun completedPopup() {
        _navigateToSelected.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("destroyed")
    }
}