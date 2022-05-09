package com.example.sykkelapp.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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

        // Update user information
        _binding.saveButton.setOnClickListener{
            updateUserInformation()
        }

        //Hide action bar
        supportActionBar?.hide()
    }
    // https://firebase.google.com/docs/database/android/read-and-write#updating_or_deleting_data
    private fun updateUserInformation() {
        val firstName = _binding.editFirstName.text.toString()
        val lastName = _binding.editLastName.text.toString()

        val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val userID = firebaseUser.uid
        val userHashMap = HashMap<String, Any>()

        when{
            TextUtils.isEmpty(firstName) and TextUtils.isEmpty(lastName) -> {
                Toast.makeText(this, "No changes made!", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(firstName).not() -> userHashMap ["firstname"] = _binding.editFirstName.text.toString()
            TextUtils.isEmpty(lastName).not() -> userHashMap ["lastname"]  = _binding.editLastName.text.toString()
        }
        databaseReference.child(userID).updateChildren(userHashMap)
        Toast.makeText(this, "Your account information has been updated successfully!", Toast.LENGTH_LONG).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}