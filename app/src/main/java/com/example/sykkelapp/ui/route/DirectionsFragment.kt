package com.example.sykkelapp.ui.route

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentDirectionsBinding
import com.example.sykkelapp.ui.map.BitmapHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

            val startIcon = BitmapHelper.vectorToBitmap(context, R.drawable.ic_baseline_directions_bike_24, Color.rgb(205, 18, 18))
            val startMark = MarkerOptions().position(start).icon(startIcon)
            mMap.addMarker(startMark)

            val endIcon = BitmapHelper.vectorToBitmap(context, R.drawable.ic_baseline_flag_24, Color.rgb(205, 18, 18))
            val endMark = MarkerOptions().position(end).icon(endIcon)
            mMap.addMarker(endMark)

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


        val button: Button = binding.complete

        button.setOnClickListener{
            button.text = "+" + card?.findViewById<TextView>(R.id.length)?.text
            button.setBackgroundColor(rgb(34, 139, 34))
            button.setOnClickListener{popBack()}

            val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("Distance")

            databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val distance = snapshot.value as HashMap<*, *>
                        val numbersOnly = button.text.toString().substring(1).dropLast(3).toDouble()
                        if (distance[firebaseUser] == null) {
                            databaseReference.child(firebaseUser).setValue(numbersOnly)
                        } else {
                            val previousValue = distance[firebaseUser] as Number
                            databaseReference.child(firebaseUser).setValue(numbersOnly.plus(previousValue.toDouble()))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error: unable to upload distance", error.message)
                }
            })
        }
        return root
    }

    private fun popBack(){
        findNavController().popBackStack()
    }
}