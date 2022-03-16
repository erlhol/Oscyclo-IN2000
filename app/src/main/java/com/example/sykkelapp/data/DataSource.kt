package com.example.sykkelapp.data

import android.util.Log
import com.example.sykkelapp.data.airqualityforecast.Airqualityforecast
import com.example.sykkelapp.data.geoserver.GeoServer
import com.example.sykkelapp.data.locationForecast.Geometry
import com.example.sykkelapp.data.locationForecast.LocationForecast
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

class Datasource { // evt la datasource ta inn path som parameter

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    suspend fun loadWheather() : Geometry {
        // Just a sample URL. Has to be changed later
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/compact?lat=60.10&lon=9.58"
        val response : LocationForecast = client.get(path)
        Log.d("load wheater","Loaded: "+response)
        loadGeo()
        loadAir()
        return response.geometry
    }

    suspend fun loadGeo() {
        val path = "https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Abyruter&outputFormat=application/json"
        val response : GeoServer = client.get(path)
        Log.d("loaded geo","Loaded: "+response)
    }

    suspend fun loadAir() {
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?lat=60&lon=10&areaclass=grunnkrets"
        val response : Airqualityforecast = client.get(path)
        Log.d("loaded air","Loaded: "+response)
    }
}