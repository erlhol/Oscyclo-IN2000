package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQuality
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.locationForecast.Data
import kotlinx.coroutines.runBlocking
import com.example.sykkelapp.data.parking.Feature
import org.junit.Test

internal class RepositoryTestAPI {

    private var source = Repository(Datasource())
    // Testing correctness of API-calls and implemented methods

    @Test
    // Wrong input parameter should throw an exception
    fun loadWeatherWrongInput() {
        runBlocking {
            val returnVal = source.loadWeather("0,0","0,0","complete?")
            assert(returnVal == null)
        }
    }
    @Test
    // Correct input peratmeter should not throw an exception
    fun loadWeatherCorrectInput() {
        runBlocking {
            // because of platform type we have to cast to Data?
            val returnVal = source.loadWeather("59.94410", "10.7185","complete?")
            assert(returnVal is Data)
        }
    }

    @Test
    fun loadOsloRoutes() {
        runBlocking {
            val returnVal = source.loadOsloRoutes()
            assert(returnVal is String)
        }
    }

    @Test
    fun loadParking() {
        runBlocking {
            val returnVal = source.loadParking()
            assert(returnVal is List<Feature>)
        }
    }

    @Test
    fun loadNILUAirQ() {
        runBlocking {
            val returnVal = source.loadNILUAirQ()
            assert(returnVal is List<AirQualityItem>)
        }
    }

    @Test
    fun loadAirQualityForecastWrongInput() {
        runBlocking {
            val returnVal = source.loadAirQualityForecast("0,0","0,0")
            assert(returnVal == null)
        }
    }

    @Test
    fun loadAirQualityForecastCorrectInput() {
        runBlocking {
            val returnVal = source.loadAirQualityForecast("59.94410", "10.7185")
            assert(returnVal is Pm10Concentration)
        }
    }

    @Test
    fun loadBySykkel() {
        runBlocking {
            val returnVal = source.loadBySykkel()
            assert(returnVal is List<Station>)
        }
    }

    @Test
    fun loadBySykkelRoutes() {
        runBlocking {
            val returnVal = source.loadBySykkelRoutes()
            assert(returnVal is List<Route>)
        }
    }


}