package com.example.sykkelapp.ui.profile

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment : Fragment() {

    private lateinit var _binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        val root: View = _binding.root

        // Navigate back to Sign in
        _binding.signUpSignInButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Create account
        _binding.createAccount.setOnClickListener {
            createAccount()
        }

        // Underline text under Sign in button
        val button = _binding.signUpSignInButton
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        return root
    }

    // Code inspired from
    // 1. Firebase documentation: https://firebase.google.com/docs/auth/android/start?authuser=0
    // 2. https://firebase.google.com/docs/auth/android/start?authuser=0#sign_up_new_users
    // 3. https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth#createUserWithEmailAndPassword(java.lang.String,%20java.lang.String)
    // 4. CodeWithMazn: https://www.youtube.com/watch?v=Z-RE1QuUWPg&t=371s&ab_channel=CodeWithMazn
    // 5. Stackoverflow: https://stackoverflow.com/questions/6290531/how-do-i-check-if-my-edittext-fields-are-empty
    private fun createAccount() {
        val firstName = _binding.signUpFirstName.text.toString()
        val lastName = _binding.signUpLastName.text.toString()
        val email = _binding.signUpEmail.text.toString()
        val password = _binding.signUpPassword.text.toString()
        val confirmPassword = _binding.signUpConfirmPassword.text.toString()

        when {
            TextUtils.isEmpty(firstName) -> _binding.signUpFirstName.error = "First name is required"
            TextUtils.isEmpty(lastName) -> _binding.signUpLastName.error = "Last name is required"
            TextUtils.isEmpty(email) -> _binding.signUpEmail.error = "Email address is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()-> _binding.signUpEmail.error = "Please provide a valid email address"
            TextUtils.isEmpty(password) -> _binding.signUpPassword.error = "Password is required"
            _binding.signUpPassword.length() < 6 -> _binding.signUpPassword.error = "Password should be at least 6 characters"
            TextUtils.isEmpty(confirmPassword) -> _binding.signUpConfirmPassword.error = "Confirm password is required"
            TextUtils.equals(password,confirmPassword).not() -> Toast.makeText(
                requireContext(),
                "Please enter similar passwords",
                Toast.LENGTH_LONG).show()

            else -> {
                _binding.signUpProgressBar.visibility = VISIBLE

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")
                            saveUserInformation(firstName,lastName,email)
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(requireContext(), "Failed to create account. Please try again!", Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            _binding.signUpProgressBar.visibility = GONE
                        }
                    }.addOnFailureListener {
                        it.stackTrace
                        Log.d(TAG, it.message.toString())
                    }
            }
        }
    }

    // Code inspired from
    // 1. Firebase documentation: https://firebase.google.com/docs/reference/android/com/google/firebase/database/DatabaseReference#setValue(java.lang.Object)
    // 2. Stackoverflow: https://stackoverflow.com/questions/59700667/unable-to-store-data-into-firebase-database-and-during-run-time-there-are-no-err
    private fun saveUserInformation(firstName: String, lastName: String, email: String){
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userHashMap = HashMap<String, Any>()
        userHashMap["email"] = email
        userHashMap["firstname"] = firstName
        userHashMap["image"] = "https://firebasestorage.googleapis.com/v0/b/sykkel-app-fc597.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=2c9e98b1-d7c8-49bb-9959-0ba46705e712"
        userHashMap["lastname"] = lastName
        userHashMap["uid"] = firebaseUser

         databaseReference.child(firebaseUser).setValue(userHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Your account has been created successfully!", Toast.LENGTH_LONG)
                        .show()
                    _binding.signUpProgressBar.visibility = VISIBLE

                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "Failed to create account. Please try again!", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    //Progress bar disappears
                    _binding.signUpProgressBar.visibility = GONE
                }
            }.addOnFailureListener{
                it.stackTrace
                Log.d(TAG, it.message.toString())
            }
    }
}