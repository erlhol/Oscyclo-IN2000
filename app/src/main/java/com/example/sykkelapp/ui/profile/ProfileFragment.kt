package com.example.sykkelapp.ui.profile

import android.content.ContentValues
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentProfileBinding
import com.example.sykkelapp.ui.route.RouteAdapter
import com.example.sykkelapp.ui.route.RouteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

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

        //Navigate to Account settings
        _binding!!.profileSettingsButton.setOnClickListener{
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        // Navigate to Sign up button
        _binding!!.signInSignUpButton.setOnClickListener{
            findNavController().navigate(R.id.signUpFragment)
        }

        //Signing in user
        _binding!!.signInButtonMain.setOnClickListener {
            signInUser()
        }

        //Underline text
        val button = _binding!!.signInSignUpButton
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val recyclerView = binding.recyclerView
        routeViewModel.routes.observe(viewLifecycleOwner) {
                routes ->
            if (routes != null) {
                recyclerView.adapter = RouteAdapter(routes, context) // filter by bookmarks
            }
        }

        //Get current user session
        if (FirebaseAuth.getInstance().currentUser != null) {
            setUserInformation()
        }
        isLoggedIn()
        return root
    }

    private fun setUserInformation() {
        val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users")
        userID = firebaseUser.uid

        reference.child(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("Datasnapshot",dataSnapshot.exists().toString())
                if (dataSnapshot.exists()) {
                    val firstName = "${dataSnapshot.child("firstname").value}"
                    val lastName = "${dataSnapshot.child("lastname").value}"
                    val email = "${dataSnapshot.child("email").value}"
                    val user = User(firstName,lastName,email)
                    val level = "Level: Beginner"

                    _binding?.profileFullName?.text = user.getFullName()
                    _binding?.profileUserLevel?.text = level
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Datasnapshot", databaseError.message)
            }
        })
    }

    private fun signInUser() {
        val email = _binding?.signInEmail?.text.toString()
        val password = _binding?.signInPassword?.text.toString()

        when{
            TextUtils.isEmpty(email) -> _binding?.signInEmail?.error = "Email is required"
            TextUtils.isEmpty(email) -> _binding?.signInEmail?.requestFocus()

            !Patterns.EMAIL_ADDRESS.matcher(email).matches()-> _binding?.signInEmail?.error  = "Please provide a valid email address"

            TextUtils.isEmpty(password) -> _binding?.signInPassword?.error = "Password is required"
            TextUtils.isEmpty(password) -> _binding?.signInPassword?.requestFocus()

            else -> {
                _binding?.signInProgressBar?.visibility  = View.VISIBLE

                //https://firebase.google.com/docs/auth/android/start?authuser=0#kotlin+ktx_3
                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            startActivity(Intent(context, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                            activity?.finish()
                            Log.d(ContentValues.TAG, "signInWithEmail:success")
                            Toast.makeText(this.requireActivity(), "Signed in successfully",
                                Toast.LENGTH_LONG).show()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this.requireActivity(), "Failed to sign in. Please try again!",
                                Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            //Progress bar disappears
                            _binding?.signInProgressBar?.visibility = View.GONE
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isLoggedIn() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            binding.profileInformation.visibility = View.GONE
            binding.signInLayout.visibility = View.VISIBLE
        }
        else {
            binding.profileInformation.visibility = View.VISIBLE
            binding.signInLayout.visibility = View.GONE
        }
    }


}