package com.example.sykkelapp.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView : MapView
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = root.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync {
            mMap = it
            // Add a marker in Oslo and move the camera
            val ojd = LatLng(59.94410, 10.7185)
            mMap.addMarker(MarkerOptions().position(ojd).title("Marker at OJD"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ojd))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ojd,15f))
            var layer : GeoJsonLayer
            homeViewModel.geo.observe(viewLifecycleOwner) {
                    geo -> layer = GeoJsonLayer(mMap, JSONObject(geo))
                val layer_style = layer.defaultLineStringStyle
                layer_style.isClickable = true
                layer_style.color = Color.GREEN
                layer.addLayerToMap()
                // Only for debugging currently:
                layer.setOnFeatureClickListener {
                    println(it.id)
                }
            }

        }
        return root
    }

    // Vi maa selv velge hvordan haandtere lifecycle.

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}