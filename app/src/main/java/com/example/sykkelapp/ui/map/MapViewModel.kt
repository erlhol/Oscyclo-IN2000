package com.example.sykkelapp.ui.map

import android.app.Application
import androidx.lifecycle.*
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.Repository
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.ui.map.location.LocationLiveData
import com.example.sykkelapp.ui.map.location.LocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _osloroutes = MutableLiveData<String>()
    private val _weatherforecast = MutableLiveData<Data>()
    private val _airq = MutableLiveData<List<AirQualityItem>>()
    private val _airqualityforecast = MutableLiveData<Pm10Concentration>()
    private val _bysykkelStation = MutableLiveData<List<Station>>()
    private val source = Repository(Datasource())

    private val _locationData = LocationLiveData(application)

    val osloroutes : LiveData<String>
        get() = _osloroutes
    val weatherforecast : LiveData<Data>
        get() = _weatherforecast
    val airq : LiveData<List<AirQualityItem>>
        get() = _airq
    val airqualityforecast : LiveData<Pm10Concentration>
        get() = _airqualityforecast
    val bysykkelStation : LiveData<List<Station>>
        get() = _bysykkelStation
    val locationData : LiveData<LocationModel>
        get() = _locationData

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _osloroutes.postValue(source.loadOsloRoutes())
            _airq.postValue(source.loadNILUAirQ())
            _weatherforecast.postValue(source.loadWeather("59.94410", "10.7185", "complete?"))
            _airqualityforecast.postValue(source.loadAirQualityForecast("59.94410", "10.7185"))
            _bysykkelStation.postValue(source.loadBySykkel())

        }
    }

    fun updateLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val lat = locationData.value?.latitude
            val lon = locationData.value?.longitude
            _weatherforecast.postValue(source.loadWeather(lat.toString(), lon.toString(),"complete?"))
            _airqualityforecast.postValue(source.loadAirQualityForecast(lat.toString(), lon.toString()))
        }
    }
}