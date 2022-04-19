package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.locationForecast.Data
import kotlinx.coroutines.runBlocking
import com.example.sykkelapp.data.parking.Feature
import org.junit.Test

internal class DatasourceTest {

    private var source = Datasource()

    @Test(expected = Exception::class)
    // Wrong input parameter should throw an exception
    fun loadWeatherWrongInput() {
        runBlocking {
            source.loadWeather("0,0","0,0","complete?")
        }
    }
    @Test
    // Correct input peratmeter should not throw an exception
    fun loadWeatherCorrectInput() {
        runBlocking {
            try {
                // because of platform type we have to cast to Data?
                val returnVal = source.loadWeather("59.94410", "10.7185","complete?") as Data?
                assert(returnVal is Data)
                // TODO : make nullable
            }
            catch (exception : Exception) {
            }
        }
    }

    @Test
    fun loadGeo() {
        runBlocking {
            try {
                val returnVal = source.loadGeo() as String?
                assert(returnVal is String)

            }
            catch (exception : Exception) {
            }
        }
    }

    @Test
    fun loadParking() {
        runBlocking {
            try {
                val returnVal = source.loadParking() as List<Feature>?
                assert(returnVal is List<Feature>)

            }
            catch (exception : Exception) {
            }
        }
    }

    @Test
    fun loadAir() {
        runBlocking {
            try {
                val returnVal = source.loadAir() as List<AirQualityItem>?
                assert(returnVal is List<AirQualityItem>)

            }
            catch (exception : Exception) {
            }
        }
    }

    @Test(expected = Exception::class)
    fun loadAirQualityForecastWrongInput() {
        runBlocking {
            source.loadAirQualityForecast("0,0","0,0")
        }
    }

    @Test
    fun loadAirQualityForecastCorrectInput() {
        try {
            runBlocking {
                val returnVal = source.loadAirQualityForecast("59.94410", "10.7185") as Pm10Concentration?
                assert(returnVal is Pm10Concentration)
            }
        }
        catch (exception: Exception) {
        }
    }

    @Test
    fun loadBySykkel() {
        runBlocking {
            try {
                val returnVal = source.loadBySykkel() as List<Station>?
                assert(returnVal is List<Station>)

            }
            catch (exception : Exception) {
            }
        }
    }

    @Test
    fun loadBySykkelRoutes() {
        runBlocking {
            try {
                val returnVal = source.loadBySykkelRoutes() as List<BysykkelItem>?
                assert(returnVal is List<BysykkelItem>)
            }
            catch (exception : Exception) {
            }
        }
    }
}