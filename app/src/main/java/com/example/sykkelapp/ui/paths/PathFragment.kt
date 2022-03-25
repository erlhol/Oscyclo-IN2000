package com.example.sykkelapp.ui.paths

import android.os.Bundle
import android.util.Log
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
import com.example.sykkelapp.ui.Path

class PathFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding : FragmentPathBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
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
        var list = mutableListOf<Path>()
        val p = Path(500.0,3,9)
        list.add(p)
        recyclerView.adapter = PathAdapter(list)

        return root
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        println("hei")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Home","On destroy")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Home","On pause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Home","On resume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Dashboard", "on destroy")
    }
}