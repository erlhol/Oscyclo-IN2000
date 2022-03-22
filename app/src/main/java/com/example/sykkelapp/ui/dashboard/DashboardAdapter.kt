package com.example.sykkelapp.ui.dashboard

import android.content.ReceiverCallNotAllowedException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sykkelapp.R
import com.example.sykkelapp.data.geoserver.Geometry
import com.example.sykkelapp.ui.Path


abstract class DashboardAdapter(private val exampleList: List<Path>) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView = view.findViewById(R.id.picture)
        val lenghtNr: TextView = view.findViewById(R.id.lengthNr)
        val airQImg: ImageView = view.findViewById(R.id.airQ)
        val difficulty: TextView = view.findViewById(R.id.diff)
    }


        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.element, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val currentItem = exampleList[position]

            //Glide.with(holder.imageView).load(currentItem).into(holder.imageView)
            //holder.lengthNr = currentItem[position]
            //Glide.with(holder.airQImg).load(currentItem).into(holder.airQImg)
            //holder.difficulty = currentItem[position]
        }

        // Return the size of your dataset (invoked by the layout manager)


}
