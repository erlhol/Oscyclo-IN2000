package com.example.sykkelapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.databinding.ActivityAccountSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityAccountSettingsBinding

    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.settingsEditButton.setOnClickListener{
            startActivity(Intent(this, EditAccountActivity::class.java))
        }

        _binding.settingsLogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }

        if (FirebaseAuth.getInstance().currentUser != null) {
            userInformation()
        }

        //Hide action bar
        supportActionBar?.hide()
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
                        _binding.settingsFirstName.text = firstName
                        _binding.settingsLastName.text = lastName
                        _binding.settingsEmail.text = email
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Datasnapshot", databaseError.getMessage())
            }
        })
    }


}