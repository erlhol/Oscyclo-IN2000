package com.example.sykkelapp.data.bysykkel

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Station(
    val address: String,
    val capacity: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val rental_uris: RentalUris,
    val station_id: String
) : ClusterItem {

    override fun getPosition(): LatLng {
        return LatLng(lat,lon)
    }

    override fun getTitle(): String? {
        return name
    }

    override fun getSnippet(): String? {
        return address
    }
}