package com.swu.dimiz.ogg.ui.env.myenv

//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import kotlinx.coroutines.Job

// 아직 DB가 없음

//class MyEnvLayerViewModel(valdatabase: Dao,
//                          application: Application
//) : AndroidViewModel(application) {
//
//    private var viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
//
//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }
//}

//class MyEnvLayerViewModelFactory(private val data: Dao, private val application: Application) : ViewModelProvider.Factory {
//    @Suppress("unchecked_cast")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MyEnvLayerViewModel::class.java)) {
//            return MyEnvLayerViewModel(data, application) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}