package com.example.sykkelapp.ui.profile

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.signInSignUpButton.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        _binding.signInButtonMain.setOnClickListener {
            signInUser()
        }

        //Underline text
        val button = findViewById<Button>(R.id.sign_in_sign_up_button);
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        //Hide action bar
        supportActionBar?.hide()
    }

    private fun signInUser() {
        val email = _binding.signInEmail.text.toString()
        val password = _binding.signInPassword.text.toString()

        when{
            TextUtils.isEmpty(email) -> _binding.signInEmail.error = "Email is required"
            TextUtils.isEmpty(email) -> _binding.signInEmail.requestFocus()

            !Patterns.EMAIL_ADDRESS.matcher(email).matches()-> _binding.signInEmail.error = "Please provide a valid email address"

            TextUtils.isEmpty(password) -> _binding.signInPassword.error = "Password is required"
            TextUtils.isEmpty(password) -> _binding.signInPassword.requestFocus()

            else -> {
                _binding.signInProgressBar.visibility = View.VISIBLE

                //https://firebase.google.com/docs/auth/android/start?authuser=0#kotlin+ktx_3
                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(baseContext, "Signed in successfully",
                                Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Failed to sign in. Please try again!",
                                Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            //Progress bar disappears
                            _binding.signInProgressBar.visibility = View.GONE
                        }
                    }
            }
        }
    }

    // https://firebase.google.com/docs/auth/android/start?authuser=0#kotlin+ktx_3
    override fun onStart(){
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }
}