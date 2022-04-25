package com.example.sykkelapp.data.directions

data class Directions(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)