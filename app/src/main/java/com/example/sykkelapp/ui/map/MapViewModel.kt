package com.example.sykkelapp.ui.map

import android.app.Application
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
            _geo.postValue(source.loadGeo())
            _air.postValue(source.loadAir())
            _parking.postValue(source.loadParking())
            _data.postValue(source.loadWeather("59.94410", "10.7185","complete?"))
            _airquality.postValue(source.loadAirQualityForecast("59.94410", "10.7185"))
            _station.postValue(source.loadBySykkel())
        }
    }

    fun updateLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val lat = locationData.value?.latitude
            val lon = locationData.value?.longitude
            _data.postValue(source.loadWeather("%.4f".format(lat), "%.4f".format(lon),"complete?"))
            _airquality.postValue(source.loadAirQualityForecast("%.4f".format(lat), "%.4f".format(lon)))
        }

    }
}