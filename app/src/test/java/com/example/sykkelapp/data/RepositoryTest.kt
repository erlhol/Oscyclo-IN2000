package com.example.sykkelapp.data

import com.example.sykkelapp.data.source.FakeDataSource
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class RepositoryTest {

    private lateinit var datasource: FakeDataSource

    @Before
    fun setUp() {
        datasource = FakeDataSource()
    }

    @Test
    fun loadWeather() {
    }

    @Test
    fun loadOsloRoutes() {
    }

    @Test
    fun loadParking() {
    }

    @Test
    fun loadNILUAirQ() {
    }

    @Test
    fun loadAirQualityForecast() {
    }

    @Test
    fun loadBySykkel() {
    }

    @Test
    fun loadBySykkelRoutes() {
    }

    @Test
    fun findElevationDiff() {
    }

    @Test
    fun averageAirQuality() {
    }
}