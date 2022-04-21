package com.example.sykkelapp.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RouteViewModel : ViewModel() {
    private val _geo = MutableLiveData<List<List<Double>>>()
    private val _airquality = MutableLiveData<Pm10Concentration>()
    private val source = Datasource()
    private val _routes = MutableLiveData<List<BysykkelItem>>()

    val routes : LiveData<List<BysykkelItem>>
        get() = _routes

    val airquality : LiveData<Pm10Concentration>
        get() = _airquality

    val geo : MutableLiveData<List<List<Double>>>
        get() = _geo

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _routes.postValue(source.loadBySykkelRoutes())
        }
    }

    fun getAirQ(lat: String, long: String) : Double? {
        viewModelScope.launch(Dispatchers.IO) {
            _airquality.postValue(source.loadAirQualityForecast(lat, long))
        }
        return airquality.value?.value
    }
}