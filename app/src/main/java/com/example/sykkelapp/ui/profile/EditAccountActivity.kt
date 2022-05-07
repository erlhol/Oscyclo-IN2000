package com.example.sykkelapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.databinding.ActivityEditAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class EditAccountActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityEditAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.saveButton.setOnClickListener{
            updateUserInformation()
        }

//            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            finish()

        //Hide action bar
        supportActionBar?.hide()
    }

    private fun updateUserInformation() {
        val firstName = _binding.editFirstName.text.toString()
        val lastName = _binding.editLastName.text.toString()
        val email = _binding.editEmail.text.toString()

        when{
            TextUtils.isEmpty(firstName) -> _binding.editFirstName.error = "First name is required"
            TextUtils.isEmpty(lastName) -> _binding.editLastName.error = "Last name is required"
            TextUtils.isEmpty(email) -> _binding.editEmail.error = "Email address is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()-> _binding.editEmail.error  = "Please provide a valid email address"

            else -> {
                val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
                val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                val userID = firebaseUser.uid

                val userMap = HashMap<String, Any>()
                userMap ["firstname"] = _binding.editFirstName.text.toString()
                userMap ["lastname"]  = _binding.editLastName.text.toString()
                userMap ["email"]  = _binding.editEmail.text.toString()
                databaseReference.child(userID).updateChildren(userMap)

                Toast.makeText(this, "Your account information has been updated successfully!", Toast.LENGTH_LONG)
                    .show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                //finish()
            }
        }
    }
}