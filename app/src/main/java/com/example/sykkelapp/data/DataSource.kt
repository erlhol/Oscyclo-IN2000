package com.example.sykkelapp.data

import com.example.sykkelapp.BuildConfig
import com.example.sykkelapp.data.airquality.AirQuality
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.AirQualityForecast
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.BySykkel
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.directions.Directions
import com.example.sykkelapp.data.directions.Route
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.locationForecast.LocationForecast
import com.example.sykkelapp.data.placeid.PlaceName
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Datasource : DataSourceInterface {

    private val client = HttpClient {
        install(JsonFeature)
    }

    override suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data {
        val coordinate = "lat=$lat&lon=$lon"
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/$verbose$coordinate"
        val response : LocationForecast = client.get(path)
        return response.properties.timeseries[0].data
    }

    override suspend fun loadOsloRoutes(): String {
        val response: HttpResponse =
            client.request("https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Abyruter&outputFormat=application/json&srsName=EPSG:4326&CQL_FILTER=rute+IS+NOT+Null")
        return response.readText()
    }

    override suspend fun loadNILUAirQ(): List<AirQualityItem> {
        val path = "https://api.nilu.no/aq/utd?areas=oslo&components=pm10"
        return client.get<AirQuality>(path)
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
        val path = "https://data.urbansharing.com/oslobysykkel.no/trips/v1/2022/05.json"
        val response : HttpResponse = client.request(path)
        val jsonText = response.readText()
        val liste = object : TypeToken<List<BysykkelItem>>() {}.type
        return Gson().fromJson<List<BysykkelItem>?>(jsonText,liste).filter{it.duration > 1500 && it.start_station_id != it.end_station_id}
    }

    override suspend fun loadPlaceId(name : String, startPoint: String) : String {
        val path = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?fields=place_id&input=$name&inputtype=textquery&locationbias=point:$startPoint&key=${BuildConfig.MAPS_API_KEY}"
        val response : PlaceName = client.get(path)
        if (response.candidates.isNotEmpty()) {
            return response.candidates[0].place_id
        }
        return "ChIJOfBn8mFuQUYRmh4j019gkn4"
    }
    
    override suspend fun getDirection(destlatlon : String, originlatlon: String) : Route  {
        val path = "https://maps.googleapis.com/maps/api/directions/json?avoid=highways&destination=$destlatlon&mode=bicycling&origin=$originlatlon&key=${BuildConfig.MAPS_API_KEY}"
        val response : Directions = client.get(path)
        return response.routes[0]
    }
}