package com.example.sykkelapp.ui.route

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentDirectionsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions


class DirectionsFragment(private val card: View, private val decodedPath: MutableList<LatLng>) : Fragment() {

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
    ): View {

        _binding = FragmentDirectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync { map ->
            mMap = map
            mMap.addPolyline(PolylineOptions().addAll(decodedPath))
            val point_lat = decodedPath[decodedPath.size/2].latitude
            val point_long = decodedPath[decodedPath.size/2].longitude
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(point_lat,point_long),13f))
        }


        //Jeg henter imageviewet fra cardviewet jeg sender inn og prøver å ende det mini cardviewet
        val resource = card.findViewById<ImageView>(R.id.picture).tag
        if (resource is Bitmap){
            binding.cardView.findViewById<ImageView>(R.id.image).setImageBitmap(resource)
        }else{
            binding.cardView.findViewById<ImageView>(R.id.image).setImageResource(R.drawable.oscyclo_logo)
        }
        binding.cardView.findViewById<TextView>(R.id.routeName).text = card.findViewById<TextView>(R.id.title).text

        return root
    }



}