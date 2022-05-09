package com.example.sykkelapp.ui.route

import android.content.Context
import android.graphics.Color.rgb
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sykkelapp.BuildConfig
import com.example.sykkelapp.R
import com.example.sykkelapp.data.Route
import com.example.sykkelapp.ui.profile.ProfileFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.maps.android.PolyUtil


class RouteAdapter(private val exampleList: List<Route>, private val routeFragment: Fragment) : RecyclerView.Adapter<RouteAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    lateinit var card : View

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        val title : TextView
        val duration: TextView
        val distance : TextView
        val airQ : TextView
        val difficulty : TextView
        val bookmark : ImageButton

        init {
            // Define click listener for the ViewHolder's View.
            imageView = view.findViewById(R.id.picture)
            distance = view.findViewById(R.id.length)
            title = view.findViewById(R.id.title)
            duration = view.findViewById(R.id.duration)
            airQ = view.findViewById(R.id.airQ)
            difficulty = view.findViewById(R.id.difficulty)
            bookmark = view.findViewById(R.id.bookmark)
            view.setOnClickListener{
                openMapFromCoordinates(bindingAdapterPosition,view)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.element, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        // viewHolder.imageView = exampleList[position].
        viewHolder.title.text = exampleList[position].start_station_name + " to " + exampleList[position].end_station_name
        viewHolder.duration.text = exampleList[position].directions.legs[0].duration.text
        viewHolder.imageView.setImageDrawable(null)
        viewHolder.distance.text = exampleList[position].directions.legs[0].distance.text
        viewHolder.airQ.text = String.format("%.2f",exampleList[position].air_quality) + exampleList[position].airq_unit
        setImage(viewHolder.imageView, exampleList[position])
        viewHolder.difficulty.text = exampleList[position].difficulty
        displayDifficulty(viewHolder.difficulty.text as String, viewHolder.difficulty)

        // https://github.com/googlemaps/android-maps-utils/blob/main/demo/src/v3/java/com/google/maps/android/utils/demo/PolyDecodeDemoActivity.java

        if(exampleList[position].bookmarked){
            viewHolder.bookmark.setBackgroundResource(R.drawable.ic_baseline_bookmark_24)
        }else{
            viewHolder.bookmark.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24)
        }
        viewHolder.bookmark.setOnClickListener {
            updateBookmark(viewHolder, position)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = exampleList.size

    private fun displayDifficulty(diff: String, view: TextView){
        when (diff) {
            "Easy" -> {
                view.setBackgroundResource(R.drawable.easy)
            }
            "Medium" -> {
                view.setBackgroundResource(R.drawable.medium)
            }
            "Hard" -> {
                view.setBackgroundResource(R.drawable.hard)
            }
        }
    }

    private fun setImage(imageView: ImageView, item: Route) {
        Places.initialize(imageView.context, BuildConfig.MAPS_API_KEY)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(imageView.context)

        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        val fields = listOf(Place.Field.PHOTO_METADATAS)

        val placeId = item.placeid

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                // Get the photo metadata.
                val metada = place.photoMetadatas
                if (metada == null || metada.isEmpty()) {
                    Log.w("TAG", "No photo metadata.")
                    imageView.setImageResource(R.drawable.oscyclo_logo)
                    imageView.setBackgroundColor(rgb(173,216,230))
                    return@addOnSuccessListener
                }
                val photoMetadata = metada.first()

                // Get the attribution text.
                val attributions = photoMetadata?.attributions

                // Create a FetchPhotoRequest.
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build()
                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        imageView.setImageBitmap(bitmap)
                        imageView.tag = bitmap
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e("TAG", "Place not found: " + exception.message)
                            val statusCode = exception.statusCode
                            Log.w("Fail","Could not find the image:"+statusCode)
                        }
                    }
            }
    }

    private fun updateBookmark(viewHolder: ViewHolder, position: Int) {
        if(!exampleList[position].bookmarked){
            viewHolder.bookmark.setBackgroundResource(R.drawable.ic_baseline_bookmark_24)
            exampleList[position].bookmarked = true
        }else{
            viewHolder.bookmark.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24)
            exampleList[position].bookmarked = false
        }
    }

    fun openMapFromCoordinates(position: Int, card: View) {
        SelectedRoute.currentPolyline = exampleList[position].directions.overview_polyline.points
        SelectedRoute.currentView = card
        findNavController(routeFragment).navigate(R.id.directionsFragment)

    }
    // TODO: fix to get correct image on routes

    // TODO: add a new Livedata -list - containing favorites

}

class SelectedRoute {
    companion object {
        var currentPolyline : String = ""
        var currentView : View? = null
    }
}
