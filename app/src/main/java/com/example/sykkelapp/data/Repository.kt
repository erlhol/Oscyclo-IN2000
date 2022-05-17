package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.directions.Leg
import com.example.sykkelapp.data.locationForecast.Data
import kotlinx.coroutines.*

class Repository (private val datasource: DataSourceInterface) : RepositoryInterface {
    override suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data? {
        return try {
            datasource.loadWeather(lat, lon, verbose)
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun loadOsloRoutes() : String? {
        return try {
            datasource.loadOsloRoutes()
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun loadNILUAirQ() : List<AirQualityItem>? {
        return try {
            datasource.loadNILUAirQ()
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration? {
        return try {
            datasource.loadAirQualityForecast(lat, lon)
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun loadBySykkel() : List<Station>? {
        return try {
            return datasource.loadBySykkel()
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun loadBySykkelRoutes() : List<Route>? {
        try {
            val res = datasource.loadBySykkelRoutes()
            // create map of routes: Key = start,end. Value = BysykkelItem
            val popularRoutes = findPopularRoutes(res)
            val jobsList = mutableListOf<Deferred<Int>>()
            val routes = mutableListOf<Route>()

            popularRoutes.forEach {
                val job = CoroutineScope(Dispatchers.IO).async {
                    val placeid = datasource.loadPlaceId(
                        it.start_station_name,
                        (it.start_station_latitude.toString() + "," + it.start_station_longitude)
                    )
                    val airQuality = averageAirQuality(
                        it.start_station_latitude,
                        it.start_station_longitude,
                        it.end_station_latitude,
                        it.end_station_longitude
                    )
                    val airqUnit = datasource.loadAirQualityForecast(
                        it.start_station_latitude.toString(),
                        it.start_station_longitude.toString()
                    )
                    val directions = datasource.getDirection(
                        it.start_station_latitude.toString() + "," + it.start_station_longitude,
                        it.end_station_latitude.toString() + "," + it.end_station_longitude
                    )
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
                            airQuality,
                            airqUnit.units,
                            directions,
                            it.popularity,
                            setDifficulty(directions.legs[0]),
                            false
                        )
                    )
                    1
                }
                jobsList.add(job)
            }
            jobsList.awaitAll()
            return routes.sortedBy { it.air_quality } // way to sort recyclerview
        }
        catch (exception: Exception) {
            return null
        }
    }

    private fun findPopularRoutes(bysykkelItems: List<BysykkelItem>) : List<BysykkelItem> {
        val startEndMap = bysykkelItems.associateBy {
            Key(it.start_station_id, it.end_station_id)
        }

        // aggregate the routes with same start_id and end_id and count it
        val eachCountMap = bysykkelItems.groupingBy { Key(it.start_station_id,it.end_station_id) }.eachCount()

        // add the most popular routes in a list. Just the 20 most popular
        val popularRoutes = mutableListOf<BysykkelItem>()
        eachCountMap.toList().sortedByDescending { it.second }.take(15).forEach {
            startEndMap[it.first]?.let { it1 -> popularRoutes.add(it1)
                it1.popularity = it.second}
        }
        return popularRoutes
    }

    override suspend fun averageAirQuality(latStart: Double, lonStart: Double, latEnd: Double, longEnd: Double): Double {
        val start  = loadAirQualityForecast(latStart.toString(), lonStart.toString())?.value ?: -1.0
        val end = loadAirQualityForecast(latEnd.toString(), longEnd.toString())?.value ?: -1.0
        if (start == -1.0 || end == -1.0) {
            return -1.0
        }
        return (start + end)/2
    }

    private fun setDifficulty(leg: Leg): Double {
        val second = leg.duration.value.toDouble()
        val meters = leg.distance.value.toDouble()
        return meters / second
    }
}

data class Key(val startStation: String, val endStation: String)