package com.example.sykkelapp.data.bysykkelroutes

data class BysykkelItem(
    val duration: Int,
    val end_station_description: String,
    val end_station_id: String,
    val end_station_latitude: Double,
    val end_station_longitude: Double,
    val end_station_name: String,
    val ended_at: String,
    val start_station_description: String,
    val start_station_id: String,
    val start_station_latitude: Double,
    val start_station_longitude: Double,
    val start_station_name: String,
    val started_at: String,
    var placeid: String?,
    var air_quality: Double
)