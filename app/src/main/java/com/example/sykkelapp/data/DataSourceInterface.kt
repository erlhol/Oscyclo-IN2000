package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.directions.Route
import com.example.sykkelapp.data.elevation.Result
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.parking.Feature

interface DataSourceInterface {
    suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data

    suspend fun loadOsloRoutes() : String

    suspend fun loadParking() : List<Feature>

    suspend fun loadNILUAirQ() : List<AirQualityItem>

    suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration

    suspend fun loadBySykkel() : List<Station>

    suspend fun loadBySykkelRoutes() : List<BysykkelItem>

    suspend fun loadPlaceId(name : String, startPoint: String) : String

    suspend fun getDirection(destlatlon : String, originlatlon: String) : Route

    suspend fun getElevation(destlatlon : String, originlatlon: String): List<Result>

}