package com.example.sykkelapp.data.parking

data class Feature (
    val type : String,
    val id : String,
    val geometry: Geometry,
    val geometry_name : String,
    val properties: Properties
        )