package com.example.sykkelapp.data

import android.util.Log
import com.example.sykkelapp.data.airquality.AirQuality
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.AirQualityForecast
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.BySykkel
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.locationForecast.LocationForecast
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Datasource { // evt la datasource ta inn path som parameter
    // burde hvert api vaere hver sin datasource?

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    suspend fun loadWheather(lat : String, lon : String, verbose: String) : Data {
        // Just a sample URL. Has to be changed later
        val coordinate = "lat=$lat&lon=$lon"
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/$verbose$coordinate"
        val response : LocationForecast = client.get(path)
        Log.d("load wheater","Loaded: "+response)
        return response.properties.timeseries[0].data // currently only getting the first timeseries
    }

    suspend fun loadGeo() : String {
        val response : HttpResponse = client.request("https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Abyruter&outputFormat=application/json&srsName=EPSG:4326")
        val data = response.readText()
        Log.d("loaded geo","Loaded: "+response)
        return data
    }

    suspend fun loadParking() : String {
        val response : HttpResponse = client.request("https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Asykkelparkering&outputFormat=application/json&srsName=EPSG:4326")
        val data = response.readText()
        Log.d("loaded parking","Loaded: "+response)
        return data
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
}