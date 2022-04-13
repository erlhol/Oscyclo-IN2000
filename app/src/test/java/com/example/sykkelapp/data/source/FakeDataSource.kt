package com.example.sykkelapp.data.source

import com.example.sykkelapp.data.DataSourceInterface
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.locationForecast.Data
import com.example.sykkelapp.data.parking.Feature

class FakeDataSource : DataSourceInterface {
    override suspend fun loadWeather(lat: String, lon: String, verbose: String): Data {
        TODO("Not yet implemented")
    }

    override suspend fun loadGeo(): String {
        TODO("Not yet implemented")
    }

    override suspend fun loadParking(): List<Feature> {
        TODO("Not yet implemented")
    }

    override suspend fun loadAir(): List<AirQualityItem> {
        TODO("Not yet implemented")
    }

    override suspend fun loadAirQualityForecast(lat: String, lon: String): Pm10Concentration {
        TODO("Not yet implemented")
    }

    override suspend fun loadBySykkel(): List<Station> {
        TODO("Not yet implemented")
    }

    override suspend fun loadBySykkelRoutes(): List<BysykkelItem> {
        TODO("Not yet implemented")
    }


}