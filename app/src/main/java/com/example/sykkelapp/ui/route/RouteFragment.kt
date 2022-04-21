package com.example.sykkelapp.ui.route

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentRouteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RouteFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding : FragmentRouteBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val routeViewModel =
            ViewModelProvider(this)[RouteViewModel::class.java]
        binding = FragmentRouteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val spinner: Spinner = binding.spinner
        binding.spinner.onItemSelectedListener = this
        context?.let {
            ArrayAdapter.createFromResource(
                it, R.array.sortBy, android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }
        averageAirQuality(routeViewModel)
        // Just one example, has to be changed to MVVM
        val recyclerView = binding.recyclerView

        routeViewModel.routes.observe(viewLifecycleOwner) {
                routes -> recyclerView.adapter = RouteAdapter(routes)
        }
        return root
    }

    private fun averageAirQuality(viewModel: RouteViewModel){
        viewModel.routes.observe(viewLifecycleOwner){
            var i = 0
            while(i < it.size){
                val startLong = it[i].start_station_longitude
                val startLat = it[i].start_station_latitude

                val sluttLong = it[i].end_station_longitude
                val sluttLat = it[i].end_station_latitude

                val start = viewModel.getAirQ(startLat.toString(), startLong.toString())
                val end = viewModel.getAirQ(sluttLat.toString(), sluttLong.toString())

                val total = start!! + end!!
                println("total$total")
                it[i].air_qualtiy = total/2
                i += 1
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}