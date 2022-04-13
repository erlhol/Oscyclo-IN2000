package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.parking.Feature

interface DataSourceInterface {
    suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data

    suspend fun loadGeo() : String

    suspend fun loadParking() : List<Feature>

    suspend fun loadAir() : List<AirQualityItem>

    suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration

    suspend fun loadBySykkel() : List<Station>

    suspend fun loadBySykkelRoutes() : List<BysykkelItem>
}