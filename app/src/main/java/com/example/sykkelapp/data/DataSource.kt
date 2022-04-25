package com.example.sykkelapp.data

import android.util.Log
import com.example.sykkelapp.BuildConfig
import com.example.sykkelapp.data.airquality.AirQuality
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.AirQualityForecast
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.BySykkel
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.directions.Directions
import com.example.sykkelapp.data.directions.Leg
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.locationForecast.LocationForecast
import com.example.sykkelapp.data.parking.Feature
import com.example.sykkelapp.data.parking.Parking
import com.example.sykkelapp.data.placeid.PlaceName
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

class Datasource : DataSourceInterface {

    private val client = HttpClient {
        install(JsonFeature)
    }

    override suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data {
        // Just a sample URL. Has to be changed later
        val coordinate = "lat=$lat&lon=$lon"
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/$verbose$coordinate"
        val response : LocationForecast = client.get(path)
        return response.properties.timeseries[0].data // currently only getting the first timeseries
    }

    override suspend fun loadGeo() : String {
        val response : HttpResponse = client.request("https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Abyruter&outputFormat=application/json&srsName=EPSG:4326&CQL_FILTER=rute+IS+NOT+Null")
        val data = response.readText()
        return data
    }

    override suspend fun loadParking() : List<Feature> {
        val path = "https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Asykkelparkering&outputFormat=application/json&srsName=EPSG:4326"
        val response : Parking = client.get(path)
        return response.features.filter {it.geometry.coordinates.size == 2 && it.properties.antall_parkeringsplasser > 20}
    }

    override suspend fun loadAir() : List<AirQualityItem> {
        val path = "https://api.nilu.no/aq/utd?areas=oslo&components=pm10"
        val response : AirQuality = client.get(path)
        return response
    }

    override suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration {
        val coordinate = "lat=$lat&lon=$lon"
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?$coordinate"
        val response : AirQualityForecast = client.get(path)
        return response.data.time[0].variables.pm10_concentration
    }

    override suspend fun loadBySykkel() : List<Station> {
        val path = "https://gbfs.urbansharing.com/oslobysykkel.no/station_information.json"
        val response : BySykkel = client.get(path)
        return response.data.stations
    }

    override suspend fun loadBySykkelRoutes() : List<BysykkelItem> {
        val path = "https://data.urbansharing.com/oslobysykkel.no/trips/v1/2022/04.json"
        val response : HttpResponse = client.request(path)
        val jsonText = response.readText()
        val liste = object : TypeToken<List<BysykkelItem>>() {}.type
        var res : List<BysykkelItem> = Gson().fromJson<List<BysykkelItem>?>(jsonText,liste).filter{it.duration > 1500 && it.start_station_id != it.end_station_id}
        // create map of routes: Key = start,end. Value = BysykkelItem
        val startEndMap = res.map {
            Key(it.start_station_id,it.end_station_id) to it
        }.toMap()

        // aggregate the routes with same start_id and end_id and count it
        val eachCountMap = res.groupingBy { Key(it.start_station_id,it.end_station_id) }.eachCount()

        // add the most popular routes in a list. Just the 20 most popular
        var popularRoutes = mutableListOf<BysykkelItem>()
        eachCountMap.toList().sortedByDescending { it.second }.take(20).forEach {
            startEndMap[it.first]?.let { it1 -> popularRoutes.add(it1)
            it1.popularity = it.second}
        }
        val jobsList = mutableListOf<Deferred<Unit>>()

        Log.d("DataSource",res.size.toString())
            popularRoutes.forEach {
                val job = CoroutineScope(Dispatchers.IO).async {
                it.placeid = loadPlaceId(it.start_station_name)
                it.air_quality = averageAirQuality(
                    it.start_station_latitude,
                    it.start_station_longitude,
                    it.end_station_latitude,
                    it.end_station_longitude
                )
                it.directions = getDirection(it.start_station_latitude.toString()+","+it.start_station_longitude,it.end_station_latitude.toString()+","+it.start_station_longitude)
            }
            jobsList.add(job)
        }
        jobsList.awaitAll()
        return popularRoutes
    }

    override suspend fun loadPlaceId(name : String) : String {
        val path = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?fields=place_id&input=$name&inputtype=textquery&key=${BuildConfig.MAPS_API_KEY}"
        val response : PlaceName = client.get(path)
        if (response.candidates.isNotEmpty()) {
            return response.candidates[0].place_id
        }
        return "ChIJOfBn8mFuQUYRmh4j019gkn4"
    }

    override suspend fun averageAirQuality(latStart: Double, lonStart: Double, latEnd: Double, longEnd: Double): Double {
        val start  = loadAirQualityForecast(latStart.toString(), lonStart.toString()).value
        val end = loadAirQualityForecast(latEnd.toString(), longEnd.toString()).value
        return (start + end)/2
    }

    // haandtere exceptions!
    suspend fun getDirection(destlatlon : String, originlatlon: String) : Leg {
        val path = "https://maps.googleapis.com/maps/api/directions/json?avoid=highways&destination=$destlatlon&mode=bicycling&origin=$originlatlon&key=${BuildConfig.MAPS_API_KEY}"
        val response : Directions = client.get(path)
        return response.routes[0].legs[0]
    }
}

data class Key(val startStation: String, val endStation: String)