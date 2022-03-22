package com.example.sykkelapp.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sykkelapp.R
import com.example.sykkelapp.ui.Path

class DashboardAdapter(private val exampleList: List<Path>) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        val lengthNr : TextView
        val airQImg : TextView
        val difficulty: TextView

        init {
            // Define click listener for the ViewHolder's View.
            imageView = view.findViewById(R.id.picture)
            lengthNr = view.findViewById(R.id.lengthNr)
            airQImg = view.findViewById(R.id.airQ)
            difficulty = view.findViewById(R.id.diff)
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
        viewHolder.lengthNr.text = exampleList[position].length.toString()
        viewHolder.difficulty.text = exampleList[position].difficulty.toString()
        //Glide.with(holder.airQImg).load(currentItem).into(holder.airQImg)
        //Glide.with(holder.imageView).load(currentItem).into(holder.imageView)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = exampleList.size
}
