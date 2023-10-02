package com.swu.dimiz.ogg.contents.listset

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyList
import com.swu.dimiz.ogg.oggdata.remotedatabase.MySustainable
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

    //                                          활동 선택
    // 완료 버튼 : suspend 구현
    // ConditionRecord에 활동시작일/활동목표 저장
    // ListSet 데이터베이스 저장
    // 완료버튼은 여기서 코루틴 함수 써서 레포지토리에 저장하는 함수 호출하고
    // 레포지토리에 서버로 올리는 suspend 함수 구현해주시면 됩니다
    private val _navigateToSave = MutableLiveData<Boolean>()
    val navigateToSave: LiveData<Boolean>
        get() = _navigateToSave

    private val _navigateToDetail = MutableLiveData<ActivitiesDaily?>()
    val navigateToDetail: LiveData<ActivitiesDaily?>
        get() = _navigateToDetail

    private val _dailyId = MutableLiveData<ActivitiesDaily?>()
    val dailyId: LiveData<ActivitiesDaily?>
        get() = _dailyId

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                         필터 적용
    private val category = mutableListOf(ENERGY, CONSUME, TRANSPORT, RECYCLE)

    private val _filteredList = MutableLiveData<List<ActivitiesDaily>>()
    val filteredList: LiveData<List<ActivitiesDaily>>
        get() = _filteredList

    private val _activityFilter = MutableLiveData<List<String>>()
    val activityFilter: LiveData<List<String>>
        get() = _activityFilter

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     유저 정보 초기화

    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    private lateinit var appUser: MyCondition   //사용자 기본 정보 저장

    fun fireInfo(){
        //사용자 기본 정보
        fireDB.collection("User").document(fireUser?.email.toString())
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val gotUser = document.toObject<MyCondition>()
                    if (gotUser != null) {
                        appUser.projectCount = gotUser.projectCount
                        appUser.car = gotUser.car
                        appUser.startDate = gotUser.startDate!!
                        appUser.aim = gotUser.aim
                        Timber.i( "사용자 기본정보 받아오기 성공: ${document.data}")
                    }
                } else {
                    Timber.i("사용자 기본정보 받아오기 실패")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }

    private var sustlist = ArrayList<Int>()     // 등록한 지속가능한 활동
    fun fireGetSust(){
        //이미 한 sust 받아오기
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Sustainable")
            .get()
            .addOnSuccessListener { result  ->
                for (document in result ) {
                    val mysust = document.toObject<MySustainable>()
                    if (mysust != null) {
                        sustlist.add(mysust.sustID!!)
                    }
                    Timber.i( "sustlist result: $sustlist")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                  firebase 리스트 저장
    fun fireSave(){
        //프로젝트 진행상태 업데이트(새로 시작인지 수정인지 구분 추가)
        //projectCount =+ 1

        var act1 = 10001
        var act2 = 10005
        var act3 = 10010

        var actList1 = MyList()
        actList1.setFirstList(act1)
        var actList2 = MyList()
        actList2.setFirstList(act2)
        var actList3 = MyList()
        actList3.setFirstList(act3)


        val db1 = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project${appUser.projectCount}").document("1")
        val db2 = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project${appUser.projectCount}").document("2")
        val db3 =fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project${appUser.projectCount}").document("3")

        fireDB.runBatch { batch ->
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
                .collection("project${appUser.projectCount}").document(i.toString())
                .set(activity)
                .addOnCompleteListener {Timber.i("DocumentSnapshot1 successfully written!")
                }.addOnFailureListener {  e -> Timber.i("Error writing document", e)}
        }*/

    }

    val automobile = 0

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                   필요한 데이터 초기화

    //                                      활동 레벨 선택
    private val _aimCo2 = MutableLiveData<Float>()
    val aimCo2: LiveData<Float>
        get() = _aimCo2

    private val _aimTitle = MutableLiveData<String>()
    val aimTitle: LiveData<String>
        get() = _aimTitle

    private val _aimCotent = MutableLiveData<String>()
    val aimCotent: LiveData<String>
        get() = _aimCotent

    //                                        활동 선택
    private val _listHolder = MutableLiveData<List<ListData>?>()
    val listHolder: LiveData<List<ListData>?>
        get() = _listHolder

    private val _numberHolder = MutableLiveData<List<NumberData>?>()
    val numberHolder: LiveData<List<NumberData>?>
        get() = _numberHolder

    private val _co2Holder = MutableLiveData<Float>()
    val co2Holder: LiveData<Float>
        get() = _co2Holder

    private val _toastVisibility = MutableLiveData<Boolean>()
    val toastVisibility: LiveData<Boolean>
        get() = _toastVisibility

    //                                       활동 디테일
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

        _co2Holder.value = FLOAT_ZERO
        _listHolder.value = null

        getFilters()
        onFilterChanged(ENERGY, true)
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
    fun initCo2Holder() {
        _co2Holder.value = FLOAT_ZERO
        Timber.i("${_co2Holder.value}")
    }

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
        Timber.i("${_co2Holder.value}")
        Timber.i("$item")
    }

    fun co2Minus(item: ActivitiesDaily) {
        if (INTEGER_ZERO < item.freq) {
            if (co2Holder.value!! > FLOAT_ZERO) {
                _co2Holder.value = _co2Holder.value?.minus(item.co2)
            }
            item.freq--
            update(item)
        }
        if(_co2Holder.value!! < FLOAT_ZERO) {
            initCo2Holder()
        }
        Timber.i("${_co2Holder.value}")
        Timber.i("$item")
    }

    fun toastVisible() {
        _toastVisibility.value = true
    }

    fun toastInvisible() {
        _toastVisibility.value = false
    }

//    val haveCar = automobile.map {
//        automobile != 0
//    }

    val minusButtonEnabled = co2Holder.map {
        it <= FLOAT_ZERO
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

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                ListsetViewModel(repository = repository)
            }
        }
    }
}