package com.example.sykkelapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.locationForecast.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _geo = MutableLiveData<String>()
    private val _data = MutableLiveData<Data>()
    private val _air = MutableLiveData<List<AirQualityItem>>()

    val geo : LiveData<String>
        get() = _geo
    val data : LiveData<Data>
        get() = _data
    val air : LiveData<List<AirQualityItem>>
        get() = _air

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _geo.postValue(Datasource().loadGeo())
            _data.postValue(Datasource().loadWheather("59.9578", "11.0508","complete?"))
            _air.postValue(Datasource().loadAir())
        }
    }
}