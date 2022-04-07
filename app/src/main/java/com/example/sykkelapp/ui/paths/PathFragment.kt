package com.example.sykkelapp.ui.paths

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
import com.example.sykkelapp.databinding.FragmentPathBinding

class PathFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding : FragmentPathBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pathViewModel =
            ViewModelProvider(this).get(PathViewModel::class.java)
        binding = FragmentPathBinding.inflate(inflater, container, false)
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

        pathViewModel.routes.observe(viewLifecycleOwner) {
                routes -> recyclerView.adapter = PathAdapter(routes)
        }
        return root
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}