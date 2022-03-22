package com.example.sykkelapp.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
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
        val imageView = binding.icon
        val tempView = binding.temperature
        val windView = binding.windSpeed

        mapView = root.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync { map ->
            mMap = map
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
                    layer_style.color = Color.BLUE
                    layer.setOnFeatureClickListener {
                        Toast.makeText(context, it.id, Toast.LENGTH_SHORT).show()
                    }
                    var color = Color.BLUE
                    layer.features.forEach {
                        val lineStringStyle = GeoJsonLineStringStyle()
                        color += 400
                        lineStringStyle.color = color
                        println(lineStringStyle.color)
                        it.lineStringStyle = lineStringStyle
                    }
                    layer.addLayerToMap()
                    
            }

            homeViewModel.air.observe(viewLifecycleOwner) { list ->
                list.forEach {

                    val airqualityIcon: BitmapDescriptor by lazy {
                        val color = Color.parseColor("#"+it.color)
                        BitmapHelper.vectorToBitmap(context, R.drawable.ic_baseline_eco_24, color)
                    }
                    
                    val point = LatLng(it.latitude,it.longitude)
                    mMap.addMarker(MarkerOptions()
                        .position(point)
                        .title(it.station)
                        .snippet("Svevestøvnivå: "+it.value + it.unit)
                        .icon(airqualityIcon)
                    )
                }
            }

        }

        homeViewModel.data.observe(viewLifecycleOwner) {
            println(it.next_1_hours.summary.symbol_code)
            val id = resources.getIdentifier(it.next_1_hours.summary.symbol_code,"drawable",context?.packageName)
            imageView.setImageResource(id)
            tempView.text = it.instant.details.air_temperature.toString() + "°"
            windView.text = it.instant.details.wind_speed.toString() + "m/s"
            // Add UV -index here
        }
        // bruke denne:
        //https://no.wikipedia.org/wiki/UV-indeks - fargene kan vi bruke - og tekstlige beskrivelsen
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}