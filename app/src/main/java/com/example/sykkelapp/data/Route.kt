package com.example.sykkelapp.data

import com.example.sykkelapp.data.directions.Route

data class Route(
    val end_station_description: String,
    val end_station_id: String,
    val end_station_latitude: Double,
    val end_station_longitude: Double,
    val end_station_name: String,
    val start_station_description: String,
    val start_station_id: String,
    val start_station_latitude: Double,
    val start_station_longitude: Double,
    val start_station_name: String,
    val placeid: String,
    val air_quality: Double,
    val airq_unit: String,
    val directions: Route,
    val popularity: Int,
    val elevation: Double,
    val difficulty: String,
    var bookmarked : Boolean
)