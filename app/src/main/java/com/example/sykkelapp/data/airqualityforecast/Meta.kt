package com.example.sykkelapp.data.airqualityforecast

data class Meta(
    val location: Location,
    val reftime: String,
    val sublocations: List<Any>,
    val superlocation: Superlocation
)