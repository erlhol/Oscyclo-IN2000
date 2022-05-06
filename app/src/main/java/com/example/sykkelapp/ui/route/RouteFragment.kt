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
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentRouteBinding

class RouteFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding : FragmentRouteBinding
    private lateinit var routeViewModel : RouteViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        routeViewModel = ViewModelProvider(this)[RouteViewModel::class.java]
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

        val recyclerView = binding.recyclerView

        routeViewModel.routes.observe(viewLifecycleOwner) {
            routes ->
                if (routes != null) {
                    recyclerView.adapter = RouteAdapter(routes, context)
                }
        }

        return root
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        when (p2) {
            0 -> {
                val routesSorted = routeViewModel.routes.value?.sortedBy {it.air_quality}
                binding.recyclerView.adapter = routesSorted?.let { RouteAdapter(it, context) }
                // update routes function in RouteAdapter
            }
            1 -> {
                val routesSorted = routeViewModel.routes.value?.sortedBy {it.directions.legs[0].distance.value}
                binding.recyclerView.adapter = routesSorted?.let { RouteAdapter(it, context) }
            }
            2 -> {
                val routesSorted = routeViewModel.routes.value?.sortedBy {it.difficulty}
                binding.recyclerView.adapter = routesSorted?.let { RouteAdapter(it,context) }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}