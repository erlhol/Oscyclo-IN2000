package com.example.sykkelapp.data.parking

import android.content.Context
import android.graphics.Color
import com.example.sykkelapp.R
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.ui.map.BitmapHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * A custom cluster renderer for Place objects.
 */
class FeatureRenderer(
    private val context: Context?,
    map: GoogleMap,
    clusterManager: ClusterManager<Feature>
) : DefaultClusterRenderer<Feature>(context, map, clusterManager) {

    /**
     * The icon to use for each cluster item
     */
    private val parkeringsPlass: BitmapDescriptor by lazy {
        val color = Color.parseColor("#0035BA")
        BitmapHelper.vectorToBitmap(
            context,
            R.drawable.ic_baseline_local_parking_24,
            color
        )
    }

    /**
     * Method called before the cluster item (the marker) is rendered.
     * This is where marker options should be set.
     */
    override fun onBeforeClusterItemRendered(
        item: Feature,
        markerOptions: MarkerOptions
    ) {
        markerOptions.title(item.id)
            .position(item.position)
            .icon(parkeringsPlass)
    }

    /**
     * Method called right after the cluster item (the marker) is rendered.
     * This is where properties for the Marker object should be set.
     */
    override fun onClusterItemRendered(clusterItem: Feature, marker: Marker) {
        marker.tag = clusterItem
        marker.snippet = "Capicity:"+clusterItem.properties.antall_parkeringsplasser.toString()
    }
}