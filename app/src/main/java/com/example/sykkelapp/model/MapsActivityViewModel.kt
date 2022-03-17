package com.example.sykkelapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.locationForecast.Geometry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsActivityViewModel : ViewModel() {

    private val _geo = MutableLiveData<String>()
    val geo : LiveData<String>
        get() = _geo


    init {
        viewModelScope.launch(Dispatchers.IO) {
            _geo.postValue(Datasource().loadGeo())
        }
    }
}