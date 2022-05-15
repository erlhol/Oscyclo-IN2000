package com.example.sykkelapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.ActivityAccountSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        // Navigate to Edit account settings
        _binding.settingsEditButton.setOnClickListener{
            startActivity(Intent(this, EditAccountActivity::class.java))
        }

        // Log out user and return to main
        _binding.settingsLogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }

        // Display user information in Account settings
        if (FirebaseAuth.getInstance().currentUser != null) {
            userInformation()
        }

        // Hide action bar
        supportActionBar?.hide()
    }

    //Code inspired from Firebase documentation:
    //1. https://firebase.google.com/docs/database/android/read-and-write,
    //2. https://firebase.google.com/docs/reference/android/com/google/firebase/database/DataSnapshot,
    //3. https://firebase.google.com/docs/reference/android/com/google/firebase/database/Query#addValueEventListener(com.google.firebase.database.ValueEventListener),
    //4. CodeWithMazn: https://www.youtube.com/watch?v=-plgl1EQ21Q&t=47s&ab_channel=CodeWithMazn,
    //5. Atif Pervaiz: https://www.youtube.com/watch?v=_J7q_qHC0YY&t=1380s&ab_channel=AtifPervaiz
    private fun userInformation() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.child(firebaseUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.value as HashMap<*, *>

                    if (user.size > 0) {
                        val profilePicture = user["image"].toString()
                        val email = user["email"].toString()
                        val firstName = user["firstname"].toString()
                        val lastName = user["lastname"].toString()

                        _binding.settingsFirstName.text = firstName
                        _binding.settingsLastName.text = lastName
                        _binding.settingsEmail.text = email
                        _binding.settingsProfileImage.let {
                            Glide.with(this@AccountSettingsActivity)
                                .load(profilePicture)
                                .placeholder(R.drawable.profile)
                                .into(it)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Datasnapshot", databaseError.message)
            }
        })
    }
}