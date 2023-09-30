package com.swu.dimiz.ogg.contents.listset

import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException

class ListsetViewModel(private val repository: OggRepository) : ViewModel() {

    private var currentJob: Job? = null
    private val disposable: Disposable? = null
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 핸들러

    //                                       활동 목표 선택
    private val _navigateToSelection = MutableLiveData<Boolean>()
    val navigateToSelection: LiveData<Boolean>
        get() = _navigateToSelection

    private val _navigateToDetail = MutableLiveData<ActivitiesDaily?>()
    val navigateToDetail: LiveData<ActivitiesDaily?>
        get() = _navigateToDetail

    private val _dailyId = MutableLiveData<ActivitiesDaily?>()
    val dailyId: LiveData<ActivitiesDaily?>
        get() = _dailyId

    //                                          활동 선택
    // 완료 버튼 : suspend 구현
    // ConditionRecord에 활동시작일/활동목표 저장
    // ListSet 데이터베이스 저장
    // 완료버튼은 여기서 코루틴 함수 써서 레포지토리에 저장하는 함수 호출하고
    // 레포지토리에 서버로 올리는 suspend 함수 구현해주시면 됩니다
    private val _navigateToSave = MutableLiveData<Boolean>()
    val navigateToSave: LiveData<Boolean>
        get() = _navigateToSave

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                         필터 적용
    private val category = mutableListOf("에너지", "소비", "이동수단", "자원순환")

    private val _filteredList = MutableLiveData<List<ActivitiesDaily>>()
    val filteredList: LiveData<List<ActivitiesDaily>>
        get() = _filteredList

    private val _activityFilter = MutableLiveData<List<String>>()
    val activityFilter: LiveData<List<String>>
        get() = _activityFilter

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     유저 정보 초기화

    // 유저 객체를 여기에다가 데려다놔 주세요
    // 차량 등록 정보 필요
    // 활동 날짜 정보 필요
    // 목표 탄소량 정보 필요
    // 등록한 지속가능한 활동 정보 필요

    val automobile = 0

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                   필요한 데이터 초기화

    private val _aimCo2 = MutableLiveData<Float>()
    val aimCo2: LiveData<Float>
        get() = _aimCo2

    private val _aimTitle = MutableLiveData<String>()
    val aimTitle: LiveData<String>
        get() = _aimTitle

    private val _aimCotent = MutableLiveData<String>()
    val aimCotent: LiveData<String>
        get() = _aimCotent

    private val _listHolder = MutableLiveData<List<ListData>?>()
    val listHolder: LiveData<List<ListData>?>
        get() = _listHolder

    private val _numberHolder = MutableLiveData<List<NumberData>?>()
    val numberHolder: LiveData<List<NumberData>?>
        get() = _numberHolder

    private val _co2Holder = MutableLiveData<Float>()
    val co2Holder: LiveData<Float>
        get() = _co2Holder

    private val _textVisible = MutableLiveData<Boolean>()
    val textVisible: LiveData<Boolean>
        get() = _textVisible

    private val _instructions = MutableLiveData<List<Instruction>>()
    val instructions: LiveData<List<Instruction>>
        get() = _instructions

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                      뷰모델 초기화
    init {
        setCo2(AIMCO2_ONE)
        _aimTitle.value = ""
        _aimCotent.value = ""
        _co2Holder.value = 0f
        _listHolder.value = null
        getFilters()
        onFilterChanged("에너지", true)
        Timber.i("created")
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     활동 목표 내용 결정자

    fun setCo2(co2: Float) {
        _aimCo2.value = co2
    }

    fun setAimCo2(button: Int) {
        when (button) {
            1 -> setCo2(AIMCO2_ONE)
            2 -> setCo2(AIMCO2_TWO)
            3 -> setCo2(AIMCO2_THREE)
        }
    }

    fun setAimTitle(co2: Float) {
        when (co2) {
            AIMCO2_ONE -> {
                _aimTitle.value = setOfAimOne.first!!
                _aimCotent.value = setOfAimOne.second!!
            }
            AIMCO2_TWO -> {
                _aimTitle.value = setOfAimTwo.first!!
                _aimCotent.value = setOfAimTwo.second!!
            }
            AIMCO2_THREE -> {
                _aimTitle.value = setOfAimThree.first!!
                _aimCotent.value = setOfAimThree.second!!
            }
            else -> _aimTitle.value = "오류쓰레기"
        }
    }
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     활동 선택 내용 결정자

    fun setListHolder(data: List<ListData>) {
        _listHolder.postValue(data)
    }

    fun setNumberHolder(data: List<NumberData>) {
        _numberHolder.postValue(data)
    }

    fun co2Plus(item: ActivitiesDaily) {
        if (item.limit > item.freq) {
            _co2Holder.value = _co2Holder.value?.plus(item.co2)
            item.freq++
            update(item)
        }
    }

    fun co2Minus(item: ActivitiesDaily) {
        if (0 < item.freq) {
            if (co2Holder.value!! > 0f) {
                _co2Holder.value = _co2Holder.value?.minus(item.co2)
            }
            item.freq--
            update(item)
        }
    }

//    val haveCar = automobile.map {
//        automobile == 0
//    }

    val minusButtonEnabled = co2Holder.map {
        it <= 0f
    }

    val saveButtonEnabled = co2Holder.map {
        it >= _aimCo2.value!!
    }

    val progressBar = co2Holder.map {
        it.div(_aimCo2.value!!).times(100).toInt()
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 리스너

    //                              활동 목표 : 다음으로> 버튼 클릭
    fun onSelectionButtonClicked() {
        _navigateToSelection.value = true
    }

    fun onNavigatedToSelection() {
        _navigateToSelection.value = false
    }

    //                                        활동 리스트

    fun onSaveButtonClicked() {
        _navigateToSave.value = true
    }

    fun onNavigatedToSave() {
        _navigateToSave.value = false
    }

    fun showPopup(act: ActivitiesDaily) {
        _navigateToDetail.value = act
        _dailyId.value = act
        val getInstructions: LiveData<List<Instruction>> = repository.getInstructions(act.dailyId, act.limit)
        _instructions.value = getInstructions.value
        val textVision = getInstructions.map {
            it.isNotEmpty()
        }
        _textVisible.value = textVision.value
    }

    fun completedPopup() {
        _navigateToDetail.value = null
    }

    fun update(act: ActivitiesDaily) = viewModelScope.launch {
        repository.updateFreq(act)
        Timber.i("$act")
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                          필터
    private fun getFilters() {
        category.let {
            if (it != _activityFilter.value) {
                _activityFilter.value = it
            }
        }
    }

    private fun onFilterUpdated(filter: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {

            try {
                _filteredList.value = repository.getFiltered(filter)
            } catch (e: IOException) {
                _filteredList.value = listOf()
            }
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (isChecked) {
            onFilterUpdated(filter)
        }
    }

    override fun onCleared() {
        if (disposable?.isDisposed == false) {
            disposable.dispose()
        }
        super.onCleared()
        Timber.i("destroyed")
    }

    private val setOfAimOne = "초보" to "3위인 바다거북"
    private val setOfAimTwo = "중수" to "2위인 바다표범"
    private val setOfAimThree = "고수" to "1위인 바키타 돌고래"

    companion object {

        const val AIMCO2_ONE = 1.4f
        const val AIMCO2_TWO = 2.78f
        const val AIMCO2_THREE = 5.22f

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                ListsetViewModel(repository = repository)
            }
        }
    }
}