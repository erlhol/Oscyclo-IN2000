package com.example.sykkelapp.data.parking

import com.example.sykkelapp.data.geoserver.Geometry
import com.example.sykkelapp.data.geoserver.PropertiesX

data class Feature(
    val geometry: Geometry,
    val geometry_name: String,
    val id: String,
    val properties: PropertiesX,
    val type: String
)