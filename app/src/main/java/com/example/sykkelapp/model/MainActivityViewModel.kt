package com.example.sykkelapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.locationForecast.Geometry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    // TODO: Fix this class
    private val _geometry = MutableLiveData<Geometry>()
    val geometry : LiveData<Geometry>
        get() = _geometry


    init {
        viewModelScope.launch(Dispatchers.IO) {
            _geometry.postValue(Datasource().loadWheather())
        }
    }
}