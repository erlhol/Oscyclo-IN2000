package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.locationForecast.Data

interface RepositoryInterface {

        suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data?

        suspend fun loadOsloRoutes() : String?

        suspend fun loadNILUAirQ() : List<AirQualityItem>?

        suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration?

        suspend fun loadBySykkel() : List<Station>?

        suspend fun loadBySykkelRoutes() : List<Route>?

        suspend fun averageAirQuality(latStart: Double, lonStart: Double, latEnd: Double, longEnd: Double): Double
}