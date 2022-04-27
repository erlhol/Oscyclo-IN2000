package com.example.sykkelapp.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sykkelapp.data.Datasource
import com.example.sykkelapp.data.Repository
import com.example.sykkelapp.data.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RouteViewModel : ViewModel() {
    private val source = Repository(Datasource())
    private val _routes = MutableLiveData<List<Route>>()

    val routes : LiveData<List<Route>>
        get() = _routes


    init {
        viewModelScope.launch(Dispatchers.IO) {
            _routes.postValue(source.loadBySykkelRoutes())
        }
    }
}