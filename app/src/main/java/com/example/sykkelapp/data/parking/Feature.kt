package com.example.sykkelapp.data.parking

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Feature (
    val type : String,
    val id : String,
    val geometry: Geometry,
    val geometry_name : String,
    val properties: Properties
        ):
    ClusterItem {

    override fun getPosition(): LatLng {
        val coords = geometry.coordinates
        if (coords.size == 2) {
            return LatLng(coords[1],coords[0])
        }
        return LatLng(0.0,0.0)

    }

    override fun getTitle(): String? {
        return id
    }

    override fun getSnippet(): String? {
        return type
    }
}