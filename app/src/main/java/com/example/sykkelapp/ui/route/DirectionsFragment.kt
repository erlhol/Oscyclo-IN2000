package com.example.sykkelapp.ui.route

import android.graphics.Bitmap
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentDirectionsBinding
import com.example.sykkelapp.databinding.FragmentMapBinding
import com.example.sykkelapp.ui.map.MapViewModel
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class DirectionsFragment(private val card: View, private val decodedPath: MutableList<LatLng>) : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mapView : MapView
    private var _binding: FragmentDirectionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDirectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync { map ->
            mMap = map
        }


        //Jeg henter imageviewet fra cardviewet jeg sender inn og prøver å ende det mini cardviewet
        val resource = card.findViewById<ImageView>(R.id.picture).tag
        if (resource is Bitmap){
            binding.cardView.findViewById<ImageView>(R.id.image).setImageBitmap(resource)
        }else{
            binding.cardView.findViewById<ImageView>(R.id.image).setImageResource(R.drawable.oscyclo_logo)
        }
        binding.cardView.findViewById<TextView>(R.id.routeName).text = card.findViewById<TextView>(R.id.title).text

        return inflater.inflate(R.layout.fragment_directions, container, false)
    }
    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
     */



}