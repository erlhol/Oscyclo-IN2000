package com.example.sykkelapp.data

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.sykkelapp.R
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.ui.map.BitmapHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
//import com.google.codelabs.buildyourfirstmap.BitmapHelper
//import com.google.codelabs.buildyourfirstmap.R
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.ClusterRenderer
import com.google.maps.android.clustering.view.DefaultClusterRenderer

    /**
     * A custom cluster renderer for Place objects.
     */
    class StationRenderer(
        private val context: Context?,
        map: GoogleMap,
        clusterManager: ClusterManager<Station>
    ) : DefaultClusterRenderer<Station>(context, map, clusterManager) {

        /**
         * The icon to use for each cluster item
         */
        private val bicycleIcon: BitmapDescriptor by lazy {
            //val color = ContextCompat.getColor(context,)
            val color = Color.parseColor("#0047AB")
            BitmapHelper.vectorToBitmap(
                context,
                R.drawable.ic_baseline_pedal_bike_24,
                color
            )
        }

        /**
         * Method called before the cluster item (the marker) is rendered.
         * This is where marker options should be set.
         */
        override fun onBeforeClusterItemRendered(
            item: Station,
            markerOptions: MarkerOptions
        ) {
            markerOptions.title(item.name)
                .position(item.position)
                .icon(bicycleIcon)
        }

        /**
         * Method called right after the cluster item (the marker) is rendered.
         * This is where properties for the Marker object should be set.
         */
        override fun onClusterItemRendered(clusterItem: Station, marker: Marker) {
            marker.tag = clusterItem
        }
    }
