package com.example.sykkelapp.ui.profile

import android.content.ContentValues
import android.content.Intent
import android.graphics.Paint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentSignInBinding
import com.example.sykkelapp.ui.route.RouteFragment
import com.google.android.gms.maps.MapFragment
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null

    private val binding get() = _binding!!

    private lateinit var savedStateHandle: SavedStateHandle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Sign In Fragment","On CreateView")
        val signInViewModel =
            ViewModelProvider(this)[SignInViewModel::class.java]

        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle

        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding!!.signInSignUpButton.setOnClickListener{
            startActivity(Intent(context, SignUpActivity::class.java))
        }
        _binding!!.signInButtonMain.setOnClickListener {
            signInUser()
        }

        //Underline text
        val button = _binding!!.signInSignUpButton
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        return root
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
                            findNavController().popBackStack()
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
}