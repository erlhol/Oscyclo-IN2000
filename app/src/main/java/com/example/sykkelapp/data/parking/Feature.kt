package com.example.sykkelapp.data.parking

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Feature (
    val type : String,
    val id : String,
    val geometry: Geometry,
    val geometry_name : String,
        ):
    ClusterItem {

    override fun getPosition(): LatLng {
        val coords = geometry.coordinates
        return LatLng(coords[1],coords[0])
    }

    override fun getTitle(): String? {
        return id
    }

    override fun getSnippet(): String? {
        return type
    }
}