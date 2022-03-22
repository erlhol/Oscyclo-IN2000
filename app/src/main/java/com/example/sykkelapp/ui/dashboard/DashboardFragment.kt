package com.example.sykkelapp.ui.dashboard

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding : FragmentDashboardBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*
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
         */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}