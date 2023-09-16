package com.swu.dimiz.ogg.contents.listset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

class ListaimViewModel : ViewModel() {

    private val setOfAimOne = "초보거북" to "50%인 500kg"
    private val setOfAimTwo = "중수물개" to "100%인 1ton"
    private val setOfAimThree = "고수돌고래" to "150%인 1.5ton"

    private val _aimCo2 = MutableLiveData<Float>()
    val aimCo2: LiveData<Float>
        get() = _aimCo2

    private val _aimTitle = MutableLiveData<String>()
    val aimTitle: LiveData<String>
        get() = _aimTitle

    private val _aimAmount = MutableLiveData<String>()
    val aimAmount: LiveData<String>
        get() = _aimAmount

    private val _navigateToSelection = MutableLiveData<Boolean>()
    val navigateToSelection: LiveData<Boolean>
        get() = _navigateToSelection

    private val _navigateToBack = MutableLiveData<Boolean>()
    val navigateToBack: LiveData<Boolean>
        get() = _navigateToBack



    init {
        _aimCo2.value = AIMCO2_ONE
        _aimTitle.value = ""
        _aimAmount.value = ""
        Timber.i("created")
    }

    fun setAimCo2(button: Int) {
        when(button) {
            1 -> _aimCo2.value = AIMCO2_ONE
            2 -> _aimCo2.value = AIMCO2_TWO
            3 -> _aimCo2.value = AIMCO2_THREE
        }
    }

    fun setAimTitle(co2: Float) {
        when(co2) {
            AIMCO2_ONE -> {
                _aimTitle.value = setOfAimOne.first!!
                _aimAmount.value = setOfAimOne.second!!
            }
            AIMCO2_TWO -> {
                _aimTitle.value = setOfAimTwo.first!!
                _aimAmount.value = setOfAimTwo.second!!
            }
            AIMCO2_THREE -> {
                _aimTitle.value = setOfAimThree.first!!
                _aimAmount.value = setOfAimThree.second!!
            }
            else -> _aimTitle.value = "오류쓰레기"
        }
    }

    fun onSelectionButtonClicked() {
        _navigateToSelection.value = true
    }
    fun onNavigatedToSelection() {
        _navigateToSelection.value = false
    }



    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }

    companion object {
        const val AIMCO2_ONE = 1.4f
        const val AIMCO2_TWO = 2.78f
        const val AIMCO2_THREE = 5.22f
    }
}