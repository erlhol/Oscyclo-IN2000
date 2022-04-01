package com.example.sykkelapp.data.bysykkel

data class Station(
    val address: String,
    val capacity: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val rental_uris: RentalUris,
    val station_id: String
)