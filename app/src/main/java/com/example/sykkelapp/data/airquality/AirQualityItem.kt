package com.example.sykkelapp.data.airquality

data class AirQualityItem(
    val area: String,
    val color: String,
    val component: String,
    val eoi: String,
    val fromTime: String,
    val id: Int,
    val index: Int,
    val isValid: Boolean,
    val isVisible: Boolean,
    val latitude: Double,
    val longitude: Double,
    val municipality: String,
    val station: String,
    val timestep: Int,
    val toTime: String,
    val type: String,
    val unit: String,
    val value: Double,
    val zone: String
)