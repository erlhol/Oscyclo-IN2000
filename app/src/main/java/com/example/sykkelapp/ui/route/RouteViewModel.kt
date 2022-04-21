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
    private val source = Datasource()
    private val _routes = MutableLiveData<List<BysykkelItem>>()

    val routes : LiveData<List<BysykkelItem>>
        get() = _routes


    init {
        viewModelScope.launch(Dispatchers.IO) {
            _routes.postValue(source.loadBySykkelRoutes())
        }
    }
}