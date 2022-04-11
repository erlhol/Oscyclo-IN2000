package com.example.sykkelapp.data

import android.util.Log
import com.example.sykkelapp.data.airquality.AirQuality
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.AirQualityForecast
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.BySykkel
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.locationForecast.LocationForecast
import com.example.sykkelapp.data.parking.Feature
import com.example.sykkelapp.data.parking.Parking
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Datasource {

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data {
        // Just a sample URL. Has to be changed later
        val coordinate = "lat=$lat&lon=$lon"
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/$verbose$coordinate"
        val response : LocationForecast = client.get(path)
        Log.d("load wheater","Loaded: "+response)
        return response.properties.timeseries[0].data // currently only getting the first timeseries
    }

    suspend fun loadGeo() : String {
        val response : HttpResponse = client.request("https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Abyruter&outputFormat=application/json&srsName=EPSG:4326&CQL_FILTER=rute+IS+NOT+Null")
        val data = response.readText()
        Log.d("loaded geo","Loaded: "+response)
        return data
    }

    suspend fun loadParking() : List<Feature> {
        val path = "https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Asykkelparkering&outputFormat=application/json&srsName=EPSG:4326"
        val response : Parking = client.get(path)
        Log.d("loaded parking","Loaded: "+response)
        return response.features.filter {it.geometry.coordinates.size == 2 && it.properties.antall_parkeringsplasser > 20}
    }

    suspend fun loadAir() : List<AirQualityItem> {
        val path = "https://api.nilu.no/aq/utd?areas=oslo&components=pm10"
        val response : AirQuality = client.get(path)
        Log.d("loaded air","Loaded: "+response)
        return response
    }

    suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration {
        val coordinate = "lat=$lat&lon=$lon"
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?$coordinate"
        val response : AirQualityForecast = client.get(path)
        Log.d("loaded airquality","Loaded: "+response)
        return response.data.time[0].variables.pm10_concentration
    }

    suspend fun loadBySykkel() : List<Station> {
        val path = "https://gbfs.urbansharing.com/oslobysykkel.no/station_information.json"
        val response : BySykkel = client.get(path)
        Log.d("loaded station", "Loaded: " + response)
        return response.data.stations
    }

    suspend fun loadBySykkelRoutes() : List<BysykkelItem> {
        val path = "https://data.urbansharing.com/oslobysykkel.no/trips/v1/2022/03.json"
        val response : HttpResponse = client.request(path)
        val jsonText = response.readText()
        val liste = object : TypeToken<List<BysykkelItem>>() {}.type
        return Gson().fromJson(jsonText,liste)
    }
}