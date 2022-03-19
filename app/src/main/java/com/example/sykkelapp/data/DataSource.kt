package com.example.sykkelapp.data

import android.util.Log
import android.util.Property
import com.example.sykkelapp.data.airqualityforecast.Airqualityforecast
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.locationForecast.Geometry
import com.example.sykkelapp.data.locationForecast.LocationForecast
import com.example.sykkelapp.data.locationForecast.Timesery
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

    suspend fun loadAir() {
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?lat=60&lon=10&areaclass=grunnkrets"
        val response : Airqualityforecast = client.get(path)
        Log.d("loaded air","Loaded: "+response)
    }
}