package com.example.sykkelapp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.ActivityEditAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class EditAccountActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityEditAccountBinding
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var firebaseDatabase: FirebaseDatabase
//    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(_binding.root)

//        firebaseAuth = FirebaseAuth.getInstance()
//        firebaseDatabase = FirebaseDatabase.getInstance()
//        storage = FirebaseStorage.getInstance()

        //Code inspired from
        //1. Firebase documentation: https://firebase.google.com/docs/database/android/read-and-write,
        //2. https://firebase.google.com/docs/reference/android/com/google/firebase/database/Query#addListenerForSingleValueEvent(com.google.firebase.database.ValueEventListener),
        //3. Short Time Coding: https://www.youtube.com/watch?v=Ucm6O2IIAKs&ab_channel=ShortTimeCoding
        FirebaseAuth.getInstance().uid?.let {
            FirebaseDatabase.getInstance().reference.child("Users").child(it)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val user = dataSnapshot.value as HashMap<*, *>

                            if (user.size > 0) {
                                val profilePicture = user["image"].toString()

                                Glide.with(this@EditAccountActivity)
                                    .load(profilePicture)
                                    .placeholder(R.drawable.profile)
                                    .into(_binding.editProfileImage)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
        }

        // Change profile picture
        _binding.editChangeProfileImageButton.setOnClickListener {
            selectImage()
        }

        // Update user information
        _binding.saveButton.setOnClickListener{
            updateUserInformation()
        }

        // Hide action bar
        supportActionBar?.hide()
    }

    //Code inspired from
    //1. Short Time Coding: https://www.youtube.com/watch?v=TX6K1-v7Qpc&ab_channel=ShortTimeCoding
    private fun selectImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, 33)
    }

    //Code inspired from
    //1. Short Time Coding: https://www.youtube.com/watch?v=TX6K1-v7Qpc&ab_channel=ShortTimeCoding
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            val profileUri: Uri? = data.data
            _binding.editProfileImage.setImageURI(profileUri)

            val storageReference: StorageReference? =
                FirebaseAuth.getInstance().uid?.let {
                    FirebaseStorage.getInstance().reference.child("Profile picture").child(it)
                }


            if (storageReference != null) {
                if (profileUri != null) {
                    storageReference.putFile(profileUri).addOnSuccessListener {
                        Toast.makeText(this, "Image uploaded", Toast.LENGTH_LONG).show()

                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid!!)
                                .child("image").setValue(uri.toString())
                            Toast.makeText(this, "Profile picture uploaded", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    //Code inspired from
    //1. Firebase Documentation: https://firebase.google.com/docs/database/android/read-and-write#updating_or_deleting_data
    private fun updateUserInformation() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val userHashMap = HashMap<String, Any>()

        val firstName = _binding.editFirstName.text.toString()
        val lastName = _binding.editLastName.text.toString()
//        val image = _binding.editProfileImage.toString()

        when{
            TextUtils.isEmpty(firstName) and TextUtils.isEmpty(lastName) -> {
                Toast.makeText(this, "No changes made!", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(firstName).not() -> userHashMap ["firstname"] = _binding.editFirstName.text.toString()
            TextUtils.isEmpty(lastName).not() -> userHashMap ["lastname"]  = _binding.editLastName.text.toString()
//            TextUtils.isEmpty(image).not() -> userHashMap ["image"] = _binding.editProfileImage.toString()
        }
        databaseReference.child(firebaseUser).updateChildren(userHashMap)
        Toast.makeText(this, "Your account information has been updated successfully!", Toast.LENGTH_LONG).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}