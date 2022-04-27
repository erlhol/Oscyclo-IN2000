package com.example.sykkelapp.data

import android.util.Log
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.parking.Feature
import kotlinx.coroutines.*

class Repository (private val datasource: Datasource) {
    suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data? {
        return try {
            datasource.loadWeather("59.94410", "10.7185", "complete?")
        } catch (exception: Exception) {
            Log.d("Map Viewmodel", "Exception occured loadWeather")
            null
        }
    }

    suspend fun loadGeo() : String? {
        return try {
            datasource.loadGeo()
        } catch (exception: Exception) {
            Log.d("Map Viewmodel", "Exception occured loadGeo")
            null
        }
    }

    suspend fun loadParking() : List<Feature>? {
        return try {
            datasource.loadParking()
        } catch (exception: Exception) {
            Log.d("Map Viewmodel", "Exception occured loadParking")
            null
        }
    }

    suspend fun loadAir() : List<AirQualityItem>? {
        return try {
            datasource.loadAir()
        } catch (exception: Exception) {
            Log.d("Map Viewmodel", "Exception occured loadAir")
            null
        }
    }

    suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration? {
        return try {
            datasource.loadAirQualityForecast("59.94410", "10.7185")
        } catch (exception: Exception) {
            Log.d("Map Viewmodel", "Exception occured loadAirQualityForecast")
            null
        }
    }

    suspend fun loadBySykkel() : List<Station>? {
        return try {
            return datasource.loadBySykkel()
        } catch (exception: Exception) {
            Log.d("Map Viewmodel", "Exception occured loadAirQualityForecast")
            null
        }
    }

    suspend fun loadBySykkelRoutes() : List<Route>? {
        val res = datasource.loadBySykkelRoutes()
        // create map of routes: Key = start,end. Value = BysykkelItem
        val startEndMap = res.map {
            Key(it.start_station_id,it.end_station_id) to it
        }.toMap()

        // aggregate the routes with same start_id and end_id and count it
        val eachCountMap = res.groupingBy { Key(it.start_station_id,it.end_station_id) }.eachCount()

        // add the most popular routes in a list. Just the 20 most popular
        var popularRoutes = mutableListOf<BysykkelItem>()
        val routes = mutableListOf<Route>()
        eachCountMap.toList().sortedByDescending { it.second }.take(20).forEach {
            startEndMap[it.first]?.let { it1 -> popularRoutes.add(it1)
                it1.popularity = it.second}
        }
        val jobsList = mutableListOf<Deferred<Boolean>>()

        popularRoutes.forEach {
            val job = CoroutineScope(Dispatchers.IO).async {
                val placeid = datasource.loadPlaceId(it.start_station_name,(it.start_station_latitude.toString()+","+it.start_station_longitude))
                val air_quality = averageAirQuality(
                    it.start_station_latitude,
                    it.start_station_longitude,
                    it.end_station_latitude,
                    it.end_station_longitude)
                val airq_unit = datasource.loadAirQualityForecast(it.start_station_latitude.toString(),it.start_station_longitude.toString())
                val directions = datasource.getDirection(it.start_station_latitude.toString()+","+it.start_station_longitude,it.end_station_latitude.toString()+","+it.end_station_longitude)
                val elevation = findElevationDiff(it.start_station_latitude.toString()+","+it.start_station_longitude,it.end_station_latitude.toString()+","+it.end_station_longitude)
                routes.add(
                    Route(
                    it.end_station_description,
                    it.end_station_id,
                    it.end_station_latitude,
                    it.end_station_longitude,
                    it.end_station_name,
                    it.start_station_description,
                    it.start_station_id,
                    it.start_station_latitude,
                    it.start_station_longitude,
                    it.start_station_name,
                    placeid,
                    air_quality,
                    airq_unit.units,
                    directions,
                    it.popularity,
                    elevation)
                )
            }
            jobsList.add(job)
        }
        jobsList.awaitAll()
        return routes.sortedByDescending { it.popularity } // way to sort recyclerview
    }

    suspend fun findElevationDiff(destlatlon: String, originlatlon: String): Double {
        val res = datasource.getElevation(destlatlon, originlatlon)
        val destlatlonElevation = res[0].elevation
        val originlatlonElevation = res[1].elevation
        // if elevation is positive - it means that you are cycling downwards
        // else - cycling upwards
        return originlatlonElevation - destlatlonElevation
    }

    suspend fun averageAirQuality(latStart: Double, lonStart: Double, latEnd: Double, longEnd: Double): Double {
        val start  = loadAirQualityForecast(latStart.toString(), lonStart.toString())?.value ?: -1.0
        val end = loadAirQualityForecast(latEnd.toString(), longEnd.toString())?.value ?: -1.0
        return (start + end)/2
    }
}

data class Key(val startStation: String, val endStation: String)