package com.example.sykkelapp.ui.route

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentDirectionsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil


class DirectionsFragment() : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView : MapView
    private var _binding: FragmentDirectionsBinding? = null
    private lateinit var routeViewModel : RouteViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val decodedPath = PolyUtil.decode(SelectedRoute.currentPolyline)
        val card = SelectedRoute.currentView

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync { map ->
            mMap = map
            val end = decodedPath[0]
            val start = decodedPath[decodedPath.size-1]
            mMap.addMarker(MarkerOptions().position(end).title("End"))?.showInfoWindow()
            mMap.addMarker(MarkerOptions().position(start).title("Start"))?.showInfoWindow()
            mMap.addPolyline(PolylineOptions().addAll(decodedPath))
            val point_lat = decodedPath[decodedPath.size/2].latitude
            val point_long = decodedPath[decodedPath.size/2].longitude
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(point_lat,point_long),13f))
        }

        //Jeg henter imageviewet fra cardviewet jeg sender inn og prøver å ende det mini cardviewet
        val resource = card?.findViewById<ImageView>(R.id.picture)?.tag
        if (resource is Bitmap){
            binding.cardView.findViewById<ImageView>(R.id.image).setImageBitmap(resource)
        }else{
            binding.cardView.findViewById<ImageView>(R.id.image).setImageResource(R.drawable.oscyclo_logo)
        }
        binding.cardView.findViewById<TextView>(R.id.routeName).text = card?.findViewById<TextView>(R.id.title)?.text

        return root
    }



}