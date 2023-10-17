package com.swu.dimiz.ogg.ui.graph

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyGraph

class GraphViewModel : ViewModel()
{
    //myact

    //certify

    //special
    private val _graph = MutableLiveData<MyGraph>()
    val graph: LiveData<MyGraph>
        get() = _graph

    fun getGraph() {
        //_graph.value!!.co21 =
    }

}