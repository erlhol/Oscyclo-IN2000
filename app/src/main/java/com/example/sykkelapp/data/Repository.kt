package com.example.sykkelapp.data

import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.directions.Leg
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.parking.Feature
import kotlinx.coroutines.*

class Repository (private val datasource: Datasource) {
    suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data? {
        return try {
            datasource.loadWeather(lat, lon, verbose)
        } catch (exception: Exception) {
            null
        }
    }

    suspend fun loadOsloRoutes() : String? {
        return try {
            datasource.loadOsloRoutes()
        } catch (exception: Exception) {
            null
        }
    }

    suspend fun loadParking() : List<Feature>? {
        return try {
            datasource.loadParking()
        } catch (exception: Exception) {
            null
        }
    }

    suspend fun loadNILUAirQ() : List<AirQualityItem>? {
        return try {
            datasource.loadNILUAirQ()
        } catch (exception: Exception) {
            null
        }
    }

    suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration? {
        return try {
            datasource.loadAirQualityForecast(lat, lon)
        } catch (exception: Exception) {
            null
        }
    }

    suspend fun loadBySykkel() : List<Station>? {
        return try {
            return datasource.loadBySykkel()
        } catch (exception: Exception) {
            null
        }
    }

    suspend fun loadBySykkelRoutes() : List<Route>? {
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
                    val air_quality = averageAirQuality(
                        it.start_station_latitude,
                        it.start_station_longitude,
                        it.end_station_latitude,
                        it.end_station_longitude
                    )
                    val airq_unit = datasource.loadAirQualityForecast(
                        it.start_station_latitude.toString(),
                        it.start_station_longitude.toString()
                    )
                    val directions = datasource.getDirection(
                        it.start_station_latitude.toString() + "," + it.start_station_longitude,
                        it.end_station_latitude.toString() + "," + it.end_station_longitude
                    )
                    val elevation = findElevationDiff(
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
                            air_quality,
                            airq_unit.units,
                            directions,
                            it.popularity,
                            elevation,
                            setDifficulty(directions.legs[0])
                        )
                    )
                    1
                }
                jobsList.add(job)
            }
            jobsList.awaitAll()

            return routes.sortedByDescending { it.popularity } // way to sort recyclerview
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
        var popularRoutes = mutableListOf<BysykkelItem>()
        eachCountMap.toList().sortedByDescending { it.second }.take(20).forEach {
            startEndMap[it.first]?.let { it1 -> popularRoutes.add(it1)
                it1.popularity = it.second}
        }
        return popularRoutes
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

    private fun setDifficulty(leg : Leg) : String {
        // TODO: add elevation too?
        val second = leg.duration.value.toDouble()
        val meters = leg.distance.value.toDouble()
        val avgSpeed = meters/second

        return when {
            avgSpeed < 4.16 -> {
                "Easy"
            }
            avgSpeed in 4.16 .. 5.5 -> {
                "Medium"
            }
            avgSpeed > 5.5 -> {
                "Hard"
            }
            else -> {
                "FAILED"
            }
        }
    }
}

data class Key(val startStation: String, val endStation: String)