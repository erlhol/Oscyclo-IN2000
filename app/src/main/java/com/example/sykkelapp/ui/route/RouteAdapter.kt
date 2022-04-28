package com.example.sykkelapp.ui.route

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sykkelapp.BuildConfig
import com.example.sykkelapp.R
import com.example.sykkelapp.data.Route
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import kotlin.text.split as split

class RouteAdapter(private val exampleList: List<Route>) : RecyclerView.Adapter<RouteAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        val title : TextView
        val duration: TextView
        val distance : TextView
        val airQ : TextView
        val difficulty : TextView
        val bookmark : ImageButton
        var bookmarked : Boolean


        init {
            // Define click listener for the ViewHolder's View.
            imageView = view.findViewById(R.id.picture)
            distance = view.findViewById(R.id.length)
            title = view.findViewById(R.id.title)
            duration = view.findViewById(R.id.duration)
            airQ = view.findViewById(R.id.airQ)
            difficulty = view.findViewById(R.id.difficulty)
            bookmark = view.findViewById(R.id.bookmark)
            bookmarked = false
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
        viewHolder.duration.text = exampleList[position].directions.duration.text
        viewHolder.imageView.setImageDrawable(null)
        viewHolder.distance.text = exampleList[position].directions.distance.text
        viewHolder.airQ.text = String.format("%.2f",exampleList[position].air_quality) + exampleList[position].airq_unit
        setImage(viewHolder.imageView, exampleList[position])
        setDifficulty(viewHolder.difficulty, exampleList[position])

        viewHolder.bookmark.setOnClickListener{
            if(!viewHolder.bookmarked){
                viewHolder.bookmark.setBackgroundResource(R.drawable.ic_baseline_bookmark_24)
                viewHolder.bookmarked = true
            }else{
                viewHolder.bookmark.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24)
                viewHolder.bookmarked = false
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = exampleList.size

    private fun setDifficulty(diff: TextView, route: Route){
        val dur = route.directions.duration.text.split(" ")[0].toDouble()/60
        val dis = route.directions.distance.text.split(" ")[0].toDouble()
        val avgSpeed = dis/dur

        if(avgSpeed<15){
            diff.text = "Easy"
        }else if(avgSpeed in 15.0..20.0){
            diff.text = "Medium"
        }else if(avgSpeed>20){
            diff.text = "Hard"
        }else{
            diff.text = "FAILED"
        }
    }
    private fun setImage(imageView: ImageView, item: Route) {
        Places.initialize(imageView.context, BuildConfig.MAPS_API_KEY)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(imageView.context)

        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        val fields = listOf(Place.Field.PHOTO_METADATAS)

        val placeId = item.placeid ?: return

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                // Get the photo metadata.
                val metada = place.photoMetadatas
                if (metada == null || metada.isEmpty()) {
                    Log.w("TAG", "No photo metadata.")
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
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e("TAG", "Place not found: " + exception.message)
                            val statusCode = exception.statusCode
                            Log.w("Fail","Could not find the image:"+statusCode)
                        }
                    }
            }
    }
}
