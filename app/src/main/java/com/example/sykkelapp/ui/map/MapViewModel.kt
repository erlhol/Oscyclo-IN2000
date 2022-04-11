package com.example.sykkelapp.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.parking.Feature
import com.example.sykkelapp.ui.map.location.LocationLiveData
import com.example.sykkelapp.ui.map.location.LocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _geo = MutableLiveData<String>()
    private val _parking = MutableLiveData<List<Feature>>()
    private val _data = MutableLiveData<Data>()
    private val _air = MutableLiveData<List<AirQualityItem>>()
    private val _airquality = MutableLiveData<Pm10Concentration>()
    private val _station = MutableLiveData<List<Station>>()
    private val source = Datasource()

    private val _locationData = LocationLiveData(application)

    val geo : LiveData<String>
        get() = _geo
    val data : LiveData<Data>
        get() = _data
    val air : LiveData<List<AirQualityItem>>
        get() = _air
    val parking : LiveData<List<Feature>>
        get() = _parking
    val airquality : LiveData<Pm10Concentration>
        get() = _airquality
    val station : LiveData<List<Station>>
      get() = _station
    val locationData : LiveData<LocationModel>
        get() = _locationData

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadGeo()
            loadAir()
            loadParking()
            loadWeather()
            loadAirQualityForecast()
            loadBySykkel()
        }
    }

    private suspend fun loadGeo() {
        try {
            _geo.postValue(source.loadGeo())
        }
        catch(exception: Exception) {
            Log.d("Map Viewmodel","Exception occured loadGeo")
        }
    }

    private suspend fun loadAir() {
        try {
            _air.postValue(source.loadAir())
        }
        catch(exception : Exception) {
            Log.d("Map Viewmodel","Exception occured loadAir")
        }
    }

    private suspend fun loadParking() {
        try {
            _parking.postValue(source.loadParking())
        }
        catch(exception : Exception) {
            Log.d("Map Viewmodel","Exception occured loadParking")
        }
    }

    private suspend fun loadWeather() {
        try {
            _data.postValue(source.loadWeather("59.94410", "10.7185","complete?"))
        }
        catch (exception: Exception) {
            Log.d("Map Viewmodel","Exception occured loadWeather")
        }
    }

    private suspend fun loadAirQualityForecast() {
        try {
            _airquality.postValue(source.loadAirQualityForecast("59.94410", "10.7185"))
        }
        catch (exception: Exception) {
            Log.d("Map Viewmodel","Exception occured loadAirQualityForecast")
        }
    }

    private suspend fun loadBySykkel() {
        try {
            _station.postValue(source.loadBySykkel())
        }
        catch (exception: Exception) {
            Log.d("Map Viewmodel","Exception occured loadAirQualityForecast")
        }
    }

    fun updateLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val lat = locationData.value?.latitude
            val lon = locationData.value?.longitude
            _data.postValue(source.loadWeather(lat.toString(), lon.toString(),"complete?"))
            _airquality.postValue(source.loadAirQualityForecast(lat.toString(), lon.toString()))
        }
    }
}