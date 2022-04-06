package com.example.sykkelapp.ui.paths

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sykkelapp.R
import com.example.sykkelapp.data.bysykkelroutes.BysykkelItem

class PathAdapter(private val exampleList: List<BysykkelItem>) : RecyclerView.Adapter<PathAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        val from : TextView
        val to : TextView
        val duration: TextView
        val distance : TextView

        init {
            // Define click listener for the ViewHolder's View.
            imageView = view.findViewById(R.id.picture)
            distance = view.findViewById(R.id.length)
            from = view.findViewById(R.id.from)
            to = view.findViewById(R.id.to)
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
        viewHolder.from.text = exampleList[position].start_station_description
        viewHolder.to.text = exampleList[position].end_station_description
        viewHolder.duration.text = exampleList[position].duration.toString()


        val end_station_lat =  exampleList[position].end_station_latitude
        val end_station_lon = exampleList[position].end_station_longitude

        val end_coord = listOf(end_station_lon,end_station_lat)

        val start_station_lat = exampleList[position].start_station_latitude
        val start_station_lon = exampleList[position].start_station_longitude

        val start_coord = listOf(start_station_lon,start_station_lat)

        viewHolder.distance.text = findDistance(start_coord,end_coord)

        //Glide.with(holder.airQImg).load(currentItem).into(holder.airQImg)
        //Glide.with(holder.imageView).load(currentItem).into(holder.imageView)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = exampleList.size

    private fun findDistance(start: List<Double>, end : List<Double> ) : String {
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
        return totalLength.toString() + "metres"

    }
}
