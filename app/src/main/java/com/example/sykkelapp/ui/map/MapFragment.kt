package com.example.sykkelapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentMapBinding
import com.example.sykkelapp.ui.map.location.GpsUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import org.json.JSONObject

class MapFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView : MapView
    private var _binding: FragmentMapBinding? = null
    private lateinit var homeViewModel : MapViewModel
    private var isGPSEnabled = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[MapViewModel::class.java]

        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                isGPSEnabled = isGPSEnable
            }
        })

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = root.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync { map ->
            initWeatherForecast(homeViewModel)
            initMap(map,homeViewModel)
            initAirQuality(map,homeViewModel)
            initBySykkel(map, homeViewModel)
            initParking(map,homeViewModel)
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun startLocationUpdate() {
        homeViewModel.locationData.observe(this, Observer {
            binding.latLong.text = "${it.longitude},${it.latitude}"
            Log.d("Main activity",it.longitude.toString() + it.latitude.toString())
        })
    }

    private fun invokeLocationAction() {
        when {
            !isGPSEnabled -> binding.latLong.text = getString(R.string.enable_gps)

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> binding.latLong.text = getString(R.string.permission_request)

            else -> ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST
            )
        }
    }


    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED


    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initWeatherForecast(viewModel: MapViewModel) {
        val imageView = binding.weatherIcon
        val tempView = binding.temperature
        val windView = binding.windSpeed
        val uvView = binding.uvIcon
        val uvTextView = binding.uvText
        val windRotation = binding.windDirection
        viewModel.data.observe(viewLifecycleOwner) {
            val id = resources.getIdentifier(it.next_1_hours.summary.symbol_code,"drawable",context?.packageName)
            imageView.setImageResource(id)
            tempView.text = it.instant.details.air_temperature.toString() + "°"
            windView.text = it.instant.details.wind_speed.toString()
            DrawableCompat.setTint(uvView.drawable,uvColor(it.instant.details.ultraviolet_index_clear_sky, uvTextView))
            //uvTextView.text = it.instant.details.ultraviolet_index_clear_sky.toString()
            println(it.instant.details.wind_from_direction)
            windRotation.animate().rotationBy(it.instant.details.wind_from_direction.toFloat()).start()
        }
    }

    private fun initAirQuality(mMap: GoogleMap,viewModel: MapViewModel) {
        viewModel.air.observe(viewLifecycleOwner) { list ->
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
        viewModel.airquality.observe(viewLifecycleOwner) {
            Log.d("Map fragment",it.toString())
        }
    }

    private fun initMap(mMap : GoogleMap, viewModel: MapViewModel) {
        // Add a marker in Oslo and move the camera
        val ojd = LatLng(59.94410, 10.7185)
        mMap.addMarker(MarkerOptions().position(ojd).title("Marker at OJD"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ojd))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ojd,15f))
        var layer : GeoJsonLayer
        viewModel.geo.observe(viewLifecycleOwner) {
                geo -> layer = GeoJsonLayer(mMap, JSONObject(geo))
            val layer_style = layer.defaultLineStringStyle
            layer_style.isClickable = true
            layer.setOnFeatureClickListener {
                Toast.makeText(context, it.getProperty("rute"), Toast.LENGTH_SHORT).show()
            }
            uniqueColor(layer)
            layer.addLayerToMap()
        }
    }
    private fun initBySykkel(mMap: GoogleMap, viewModel: MapViewModel) {
        viewModel.station.observe(viewLifecycleOwner) {
            it.forEach {
                val bysykkelStation: BitmapDescriptor by lazy {
                    val color = Color.parseColor("#0047AB")
                    BitmapHelper.vectorToBitmap(
                        context,
                        R.drawable.ic_baseline_pedal_bike_24,
                        color
                    )
                }
                val point = LatLng(it.lat, it.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(point)
                        .title(it.name)
                        .snippet("Capacity: " + it.capacity)
                        .icon(bysykkelStation)
                )
            }
        }
    }

    private fun initParking(mMap: GoogleMap,viewModel: MapViewModel) {
        viewModel.parking.observe(viewLifecycleOwner) {
            it.forEach {
                val parkeringsPlass: BitmapDescriptor by lazy {
                    val color = Color.parseColor("#0047AB")
                    BitmapHelper.vectorToBitmap(
                        context,
                        R.drawable.ic_baseline_local_parking_24,
                        color
                    )
                }
                if (it.geometry.coordinates.size == 2) {
                    val point = LatLng(it.geometry.coordinates[1], it.geometry.coordinates[0])
                    mMap.addMarker(
                        MarkerOptions()
                            .position(point)
                            .title(it.id) // TODO: change
                            .snippet("Antall parkeringsplasser: "+it.properties.antall_parkeringsplasser)
                            .icon(parkeringsPlass)
                    )
                }

            }
        }


    }

    private fun uniqueColor(layer: GeoJsonLayer) {
        val colors = listOf<Int>(Color.BLUE,Color.BLACK,Color.RED,Color.GREEN,
            Color.YELLOW,Color.GRAY,Color.LTGRAY,
            Color.rgb(255, 128, 0),Color.rgb(128, 0, 0))

        layer.features.forEach {
            val color : Int
            val route = it.getProperty("rute")
            val lineStringStyle = GeoJsonLineStringStyle()
            val routeNum = route.toInt()
            color = colors[routeNum % (colors.size-1)]
            lineStringStyle.color = color
            it.lineStringStyle = lineStringStyle
        }
        layer.addLayerToMap()
    }


    private fun uvColor(uvIndex : Double, view: TextView) : Int {
        if (uvIndex < 3) {
            view.text = "Low"
            return Color.rgb(79, 121, 66)
        } else if (uvIndex >= 3 && uvIndex < 6) {
            view.text = "Moderate"
            return Color.rgb(253, 218, 13)
        } else if (uvIndex >= 6 && uvIndex < 8) {
            view.text = "High"
            return Color.rgb(255, 128, 0)
        } else if (uvIndex >= 8 && uvIndex < 11) {
            view.text = "Very high"
            return Color.rgb(196, 30, 58)
        } else {
            view.text = "Extreme"
            return Color.rgb(199, 21, 133)
        }

    }
}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101