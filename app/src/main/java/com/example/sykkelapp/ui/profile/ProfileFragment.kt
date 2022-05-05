package com.example.sykkelapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentProfileBinding
import com.example.sykkelapp.ui.route.RouteAdapter
import com.example.sykkelapp.ui.route.RouteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var userID: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Profile Fragment","On CreateView")
        val routeViewModel =
            ViewModelProvider(this)[RouteViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding!!.profileSettingsButton.setOnClickListener{
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        val recyclerView = binding.recyclerView
        routeViewModel.routes.observe(viewLifecycleOwner) {
                routes ->
            if (routes != null) {
                recyclerView.adapter = RouteAdapter(routes) // filter by bookmarks
            }
        }

        if (FirebaseAuth.getInstance().currentUser != null) {
            userInformation()
        }
        isLoggedIn()
        return root
    }

    private fun userInformation() {
        user = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users")
        userID = user.uid

        reference.child(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("Datasnapshot",dataSnapshot.exists().toString())
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    val email = "${dataSnapshot.child("email").value}"
                    val firstName = "${dataSnapshot.child("firstname").value}"
                    val lastName = "${dataSnapshot.child("lastname").value}"
                    if (user != null) {
                        Log.d("FirstName", firstName)
                        Log.d("LastName", lastName)
                        _binding?.profileFullName?.text = "$firstName  $lastName"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Datasnapshot", databaseError.getMessage())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun isLoggedIn() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            findNavController().navigate(R.id.signInFragment,null)
        }
    }
}