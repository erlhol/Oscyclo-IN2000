package com.example.sykkelapp.data.parking

data class Parking(
    val crs: Crs,
    val features: List<Feature>,
    val numberMatched: Int,
    val numberReturned: Int,
    val timeStamp: String,
    val totalFeatures: Int,
    val type: String
)