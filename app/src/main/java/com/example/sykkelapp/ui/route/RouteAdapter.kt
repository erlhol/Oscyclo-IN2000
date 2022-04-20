package com.example.sykkelapp.ui.route

import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sykkelapp.BuildConfig
import com.example.sykkelapp.R
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem
import com.example.sykkelapp.data.placeid.PlaceName
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking

class RouteAdapter(private val exampleList: List<BysykkelItem>) : RecyclerView.Adapter<RouteAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        val title : TextView
        val duration: TextView
        val distance : TextView

        init {
            // Define click listener for the ViewHolder's View.
            imageView = view.findViewById(R.id.picture)
            distance = view.findViewById(R.id.length)
            title = view.findViewById(R.id.title)
            duration = view.findViewById(R.id.duration)
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
        viewHolder.title.text = exampleList[position].start_station_name + " til " + exampleList[position].end_station_name
        viewHolder.duration.text = String.format("%.2f",exampleList[position].duration.toDouble() / 60) + " min"


        val end_station_lat =  exampleList[position].end_station_latitude
        val end_station_lon = exampleList[position].end_station_longitude

        val end_coord = listOf(end_station_lon,end_station_lat)

        val start_station_lat = exampleList[position].start_station_latitude
        val start_station_lon = exampleList[position].start_station_longitude

        val start_coord = listOf(start_station_lon,start_station_lat)

        viewHolder.distance.text = String.format("%.1f",findDistance(start_coord,end_coord)/1000) + "km"
        setImage(viewHolder.imageView, exampleList[position].start_station_name)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = exampleList.size

    private fun findDistance(start: List<Double>, end : List<Double> ) : Double {
        var geometry: List<List<Double>> = listOf(start,end)
        val results = FloatArray(5)

        var totalLength: Double = 0.0
        //var firstLatitude = 0.0
        //var firstLongtitude = 0.0
        //val results = FloatArray(5)
        var i = 1
        var secondLongtitude = 0.0
        var secondLatitude = 0.0
        val x = geometry[0]
        var firstLongtitude: Double = x[0]
        var firstLatitude: Double = x[1]
        //Location.distanceBetween(firstLatitude, firstLongtitude, 59.92190524, 10.71821751, results)
        //totalLength += results
        while(i < geometry.size){
            secondLongtitude = geometry[i][0]
            secondLatitude = geometry[i][1]
            Location.distanceBetween(firstLatitude, firstLongtitude, secondLatitude, secondLongtitude, results)
            results.forEach {
                totalLength += it
            }
            i += 1
            firstLatitude = secondLatitude
            firstLongtitude = secondLongtitude
        }
        return totalLength

    }

    private fun setImage(imageView: ImageView, placeName : String) {
        Places.initialize(imageView.context, BuildConfig.MAPS_API_KEY)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(imageView.context)

        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        val fields = listOf(Place.Field.PHOTO_METADATAS)

        val placeId : String
        // TODO: Fix, only for testing purposes right now
        runBlocking {
            placeId = loadPlaceId(placeName)
        }

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

    suspend fun loadPlaceId(name : String) : String {
        val path = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?fields=place_id&input=$name&inputtype=textquery&key=${BuildConfig.MAPS_API_KEY}"
        val response : PlaceName = client.get(path)
        Log.d("Loaded placeId","Loaded: "+response)
        if (response.candidates.isNotEmpty()) {
            return response.candidates[0].place_id
        }
        return "ChIJOfBn8mFuQUYRmh4j019gkn4"
    }
}
