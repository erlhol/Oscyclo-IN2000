package com.example.sykkelapp.data.airqualityforecast

data class Time(
    val from: String,
    val reason: Reason,
    val to: String,
    val variables: Variables
)