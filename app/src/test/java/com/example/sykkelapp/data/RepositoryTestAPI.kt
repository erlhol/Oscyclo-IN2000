package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.locationForecast.Data
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RepositoryTestAPI {
    // Test with real API-calls

    private var source = Repository(Datasource())
    // Testing correctness of API-calls and implemented methods

    @Test
    // Wrong input parameter should throw an exception - return null
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
    fun loadAirQualityForecastWrongCountry() {
        // Should result to null after throwing exception
        runBlocking {
            val returnVal = source.loadAirQualityForecast("35.652832", "139.839478")
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

    @Test
    fun averageAirQualityWrongInput() {
        runBlocking {
            val returnVal = source.averageAirQuality(500.0,500.0,500.0,500.0)
            assert(returnVal == -1.0)
        }

    }

    @Test
    fun averageAirQualityCorrectInput() {
        runBlocking {
            val returnVal = source.averageAirQuality(59.94410, 10.7185,59.939666, 10.722463)
            assert(returnVal != -1.0)
        }
    }


}