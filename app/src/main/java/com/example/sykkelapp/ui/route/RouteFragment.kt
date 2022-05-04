package com.example.sykkelapp.ui.route

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.size
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

        // Just one example, has to be changed to MVVM
        val recyclerView = binding.recyclerView

        routeViewModel.routes.observe(viewLifecycleOwner) {
            routes ->
                if (routes == null) {
                    return@observe
                }
                recyclerView.adapter = RouteAdapter(routes)
        }
        return root
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        when (p2) {
            0 -> {
                routeViewModel.routes.value?.sortedBy {it.air_quality}
                binding.recyclerView.adapter?.notifyItemChanged(p2)
            }
            1 -> {
                Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
                //routeViewModel.routes.value?.sortedBy {}
            }
            2 -> {
                routeViewModel.routes.value?.sortedBy {it.difficulty}
                binding.recyclerView.adapter?.notifyItemChanged(p2)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}