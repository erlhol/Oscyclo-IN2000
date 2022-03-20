package com.example.sykkelapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.locationForecast.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _geo = MutableLiveData<String>()
    private val _data = MutableLiveData<Data>()
    val geo : LiveData<String>
        get() = _geo
    val data : LiveData<Data>
        get() = _data

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _geo.postValue(Datasource().loadGeo())
            _data.postValue(Datasource().loadWheather("59.9578", "11.0508","complete?"))
        }
    }
}