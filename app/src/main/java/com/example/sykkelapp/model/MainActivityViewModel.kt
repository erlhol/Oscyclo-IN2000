package com.example.sykkelapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.locationForecast.Geometry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    // TODO: Fix this class
    private val _data = MutableLiveData<Data>()
    val data : LiveData<Data>
        get() = _data


    init {
        viewModelScope.launch(Dispatchers.IO) {
            _data.postValue(Datasource().loadWheather("59.9578", "11.0508","complete?"))
        }
    }
}