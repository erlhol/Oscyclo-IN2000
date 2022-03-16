package com.example.sykkelapp.data.geoserver

data class Feature(
    val geometry: Geometry,
    val geometry_name: String,
    val id: String,
    val properties: PropertiesX,
    val type: String
)