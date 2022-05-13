package com.example.sykkelapp.ui.profile

import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentProfileBinding
import com.example.sykkelapp.ui.route.RouteAdapter
import com.example.sykkelapp.ui.route.RouteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.math.BigDecimal
import java.math.RoundingMode

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

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

        // Navigate to Account settings
        _binding.profileSettingsButton.setOnClickListener{
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        // Navigate to Sign up fragment
        _binding.signInSignUpButton.setOnClickListener{
            findNavController().navigate(R.id.signUpFragment)
        }

        // Sign in user
        _binding.signInButtonMain.setOnClickListener {
            signInUser()
        }

        // Underline text under sign up button
        val button = _binding.signInSignUpButton
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val recyclerView = binding.recyclerView
        routeViewModel.routes.observe(viewLifecycleOwner) {
                routes ->
            if (routes != null) {
                recyclerView.adapter = RouteAdapter(routes, this) // filter by bookmarks
            }
        }

        // Get current user session
        if (FirebaseAuth.getInstance().currentUser != null) {
            setUserDistance()
            setUserInformation()
        }
        isLoggedIn()
        return root
    }

    // Code inspired from
    // 1. Firebase documentation: https://firebase.google.com/docs/auth/android/start?authuser=0
    // 2. https://firebase.google.com/docs/auth/android/start?authuser=0#sign_in_existing_users
    // 3. https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth?authuser=0#signInWithEmailAndPassword(java.lang.String,%20java.lang.String)
    // 4. CodeWithMazn: https://www.youtube.com/watch?v=KB2BIm_m1Os&ab_channel=CodeWithMazn
    private fun signInUser() {
        val email = _binding.signInEmail.text.toString()
        val password = _binding.signInPassword.text.toString()

        when{
            TextUtils.isEmpty(email) -> _binding.signInEmail.error = "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()-> _binding.signInEmail.error  = "Please provide a valid email address"
            TextUtils.isEmpty(password) -> _binding.signInPassword.error = "Password is required"
            _binding.signInPassword.length() < 6 -> _binding.signInPassword.error = "Password should be at least 6 characters"

            else -> {
                _binding.signInProgressBar.visibility  = View.VISIBLE

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
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
                            _binding.signInProgressBar.visibility = View.GONE
                        }
                    }
            }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    // Code inspired from
    //1. Firebase documentation: https://firebase.google.com/docs/database/android/read-and-write,
    //2. https://firebase.google.com/docs/reference/android/com/google/firebase/database/DataSnapshot,
    //3. https://firebase.google.com/docs/reference/android/com/google/firebase/database/Query#addValueEventListener(com.google.firebase.database.ValueEventListener),
    //4. CodeWithMazn: https://www.youtube.com/watch?v=-plgl1EQ21Q&t=47s&ab_channel=CodeWithMazn,
    //5. AtifPervaiz: https://www.youtube.com/watch?v=_J7q_qHC0YY&t=1380s&ab_channel=AtifPervaiz
    private fun setUserInformation() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.child(firebaseUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.value as HashMap<*, *>

                    if (user.size > 0) {
                        val profilePicture = user["image"].toString()
                        val firstName = user["firstname"].toString()
                        val lastName = user["lastname"].toString()
                        val level = "Level: Beginner"

                        _binding.profileFullName.text = "$firstName $lastName"
                        _binding.profileUserLevel.text = level
                        _binding.profileImageMain.let {
                            Glide.with(this@ProfileFragment)
                                .load(profilePicture)
                                .placeholder(R.drawable.profile)
                                .into(it)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Could not download information for user: $firebaseUser ", databaseError.message)
            }
        })
    }


    private fun setUserDistance() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("Distance")

        databaseReference.child(firebaseUser).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val kmValue = BigDecimal(dataSnapshot.value.toString().toDouble())
                        .setScale(2, RoundingMode.HALF_EVEN)
                        .toDouble()
                    _binding.profileTotalKm.text = kmValue.toString()
                    val image: ImageView = _binding.profileTrophy as ImageView
                    var level = ""

                    // Small number of kilometers for testing purposes
                    when {
                        (kmValue in 0.0..10.0) -> {
                            level = "Level: Beginner"
                            image.setImageResource(R.drawable.bronze_trophy)
                        }
                        (kmValue in 10.1..20.0) -> {
                            level = "Level: Intermediate"
                            image.setImageResource(R.drawable.silver_trophy)
                        }
                        (kmValue in 20.1..99999999.0) -> {
                            level = "Level: Advanced"
                            image.setImageResource(R.drawable.gold_trophy)
                        }
                    }
                    _binding.profileUserLevel.text = level
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Could not get distance for user: $firebaseUser ", databaseError.message)
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