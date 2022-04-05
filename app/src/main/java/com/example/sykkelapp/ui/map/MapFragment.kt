package com.example.sykkelapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModelProvider
import com.example.sykkelapp.R
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.bysykkel.StationRenderer
import com.example.sykkelapp.data.parking.Feature
import com.example.sykkelapp.data.parking.FeatureRenderer
import com.example.sykkelapp.data.parking.Geometry
import com.example.sykkelapp.data.parking.Properties
import com.example.sykkelapp.databinding.FragmentMapBinding
import com.example.sykkelapp.ui.map.location.GpsUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import org.json.JSONObject

class MapFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView : MapView
    private var _binding: FragmentMapBinding? = null
    private lateinit var homeViewModel : MapViewModel
    private var isGPSEnabled = false

    private var bysykkelPaa = false
    private lateinit var listeBysykkel : List<Marker>

    private var parkeringPaa = false
    private var listeParkering = mutableListOf<Marker>()

    private var luftkvalitetPaa = false
    private var listeLuftkvalitet = mutableListOf<Marker>()

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

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = root.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync { map ->
            mMap = map
            initWeatherForecast(homeViewModel)
            initMap(map,homeViewModel)
            initAirQuality(map,homeViewModel)
            addBysykkelClusteredMarkers(map, homeViewModel)
            addParkingClusteredMarkers(map, homeViewModel)
        }

        binding.bysykkelButton.setOnClickListener {
            if (!bysykkelPaa) {
                listeBysykkel.forEach {
                    it.isVisible = true
                }
                binding.bysykkelButton.setColorFilter(Color.parseColor("#FF3700B3"))
                bysykkelPaa = true
            }
            else if (bysykkelPaa) {
                listeBysykkel.forEach{
                    it.isVisible = false
                }
                binding.bysykkelButton.clearColorFilter()
                bysykkelPaa = false
            }
        }

        binding.parkingButton.setOnClickListener {
            if (!parkeringPaa) {
                listeParkering.forEach {
                    it.isVisible = true
                }
                binding.parkingButton.setColorFilter(Color.parseColor("#FF3700B3"))
                parkeringPaa = true
            }
            else if (parkeringPaa) {
                listeParkering.forEach{
                    it.isVisible = false
                }
                binding.parkingButton.clearColorFilter()
                parkeringPaa = false
            }
        }

        binding.luftkvalitetButton.setOnClickListener {
            if (!luftkvalitetPaa) {
                listeLuftkvalitet.forEach {
                    it.isVisible = true
                }
                binding.luftkvalitetButton.setColorFilter(Color.parseColor("#FF3700B3"))
                luftkvalitetPaa = true
            }
            else if (luftkvalitetPaa) {
                listeLuftkvalitet.forEach{
                    it.isVisible = false
                }
                binding.luftkvalitetButton.clearColorFilter()
                luftkvalitetPaa = false
            }
        }

        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                isGPSEnabled = isGPSEnable
            }
        })

        return root
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        homeViewModel.locationData.observe(this) {
            if (isGPSEnabled) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            }
            else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
            }
            Log.d("Main activity", it.longitude.toString() + " "+ it.latitude.toString())
            homeViewModel.updateLocation() // TODO: update weather per location update is too much
        }
    }

    private fun invokeLocationAction() {
        when {
            !isGPSEnabled ->  Log.d("Map fragment",getString(R.string.enable_gps))

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> Log.d("Map fragment",getString(R.string.permission_request))

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
            val id = resources.getIdentifier(
                it.next_1_hours.summary.symbol_code,
                "drawable",
                context?.packageName
            )
            imageView.setImageResource(id)
            tempView.text = it.instant.details.air_temperature.toString() + "°"
            windView.text = it.instant.details.wind_speed.toString()
            DrawableCompat.setTint(
                uvView.drawable,
                uvColor(it.instant.details.ultraviolet_index_clear_sky, uvTextView)
            )
            //uvTextView.text = it.instant.details.ultraviolet_index_clear_sky.toString()
            //windRotation.animate().rotationBy(it.instant.details.wind_from_direction.toFloat())
            //    .start()
        }
    }

    private fun initAirQuality(mMap: GoogleMap, viewModel: MapViewModel) {
        viewModel.air.observe(viewLifecycleOwner) { list ->
            list.forEach {
                val airqualityIcon: BitmapDescriptor by lazy {
                    val color = Color.parseColor("#" + it.color)
                    BitmapHelper.vectorToBitmap(context, R.drawable.ic_baseline_eco_24, color)
                }
                val point = LatLng(it.latitude,it.longitude)
                val luftkvalitetMarker = mMap.addMarker(MarkerOptions()
                    .position(point)
                    .title(it.station)
                    .snippet("Svevestøvnivå: "+it.value + it.unit)
                    .icon(airqualityIcon)
                    .visible(false)
                )!!
                listeLuftkvalitet.add(luftkvalitetMarker)
            }
        }
        viewModel.airquality.observe(viewLifecycleOwner) {
            Log.d("Map fragment",it.toString())
        }
    }

    private fun initMap(mMap: GoogleMap, viewModel: MapViewModel) {
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

    private fun addBysykkelClusteredMarkers(mMap: GoogleMap, viewModel: MapViewModel) {
        // Create the ClusterManager class and set the custom renderer.
        val clusterManager = ClusterManager<Station>(context, mMap)
        clusterManager.renderer =
            StationRenderer(
                context,
                mMap,
                clusterManager
            )

        // Set custom info window adapter
        //clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))

        // Add the places to the ClusterManager.
        viewModel.station.observe(viewLifecycleOwner) {
            clusterManager.addItems(it)
            clusterManager.cluster()
            mMap.setOnCameraIdleListener {
                clusterManager.onCameraIdle()
            }
        }
    }

    private fun addParkingClusteredMarkers(mMap: GoogleMap, viewModel: MapViewModel) {
        // Create the ClusterManager class and set the custom renderer.
        val clusterManager = ClusterManager<Feature>(context, mMap)
        clusterManager.renderer =
            FeatureRenderer(
                context,
                mMap,
                clusterManager
            )

        // Set custom info window adapter
        //clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))

        // Add the places to the ClusterManager.
        viewModel.parking.observe(viewLifecycleOwner) {
            clusterManager.addItems(it)
            clusterManager.cluster()
            mMap.setOnCameraIdleListener {
                clusterManager.onCameraIdle()
            }
        }

    }
}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101