package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import kotlinx.coroutines.runBlocking
import com.example.sykkelapp.data.parking.Feature
import org.junit.Test

internal class RepositoryTest {

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
            assert(returnVal != null)
        }
    }

    @Test
    fun loadOsloRoutes() {
        runBlocking {
            val returnVal = source.loadOsloRoutes()
            assert(returnVal != null)
        }
    }

    @Test
    fun loadParking() {
        runBlocking {
            val returnVal = source.loadParking()
            assert(returnVal != null)
        }
    }

    @Test
    fun loadNILUAirQ() {
        runBlocking {
            val returnVal = source.loadNILUAirQ()
            assert(returnVal != null)
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
            assert(returnVal != null)
        }
    }

    @Test
    fun loadBySykkel() {
        runBlocking {
            val returnVal = source.loadBySykkel()
            assert(returnVal != null)
        }
    }

    @Test
    fun loadBySykkelRoutes() {
        runBlocking {
            val returnVal = source.loadBySykkelRoutes()
            assert(returnVal != null)
        }
    }


}