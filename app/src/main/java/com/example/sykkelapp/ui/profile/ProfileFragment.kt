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
    private lateinit var databaseReference: DatabaseReference
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

        // Navigate to Sign up fragment
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
                recyclerView.adapter = RouteAdapter(routes, this) // filter by bookmarks
            }
        }

        //Get current user session
        if (FirebaseAuth.getInstance().currentUser != null) {
            setUserInformation()
        }
        isLoggedIn()
        return root
    }

    //https://firebase.google.com/docs/auth/android/start?authuser=0#sign_in_existing_users
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

    //Code inspired from Firebase documentation at
    //https://firebase.google.com/docs/database/android/read-and-write#java_4,
    //https://firebase.google.com/docs/reference/android/com/google/firebase/database/DataSnapshot,
    //CodeWithMazn https://www.youtube.com/watch?v=-plgl1EQ21Q&t=47s&ab_channel=CodeWithMazn and
    //https://www.youtube.com/watch?v=_J7q_qHC0YY&t=1380s&ab_channel=AtifPervaiz
    private fun setUserInformation() {
        val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        userID = firebaseUser.uid

        databaseReference.child(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.value as HashMap<*, *>

                    if (user.size > 0) {
                        val profilePicture = user["image"].toString()
                        val firstName = user["firstname"].toString()
                        val lastName = user["lastname"].toString()
                        val level = "Level: Beginner"

                        _binding?.profileFullName?.text = "$firstName $lastName"
                        _binding?.profileUserLevel?.text = level
//                        _binding?.profileImageMain?.let {
//                            Glide.with(this@ProfileFragment)
//                                .load(profilePicture)
//                                .placeholder(R.drawable.profile)
//                                .into(it)
//                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Datasnapshot", databaseError.message)
            }
        })
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