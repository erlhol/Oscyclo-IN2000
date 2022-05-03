package com.example.sykkelapp.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sykkelapp.databinding.ActivityAccountSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.settingsEditButton.setOnClickListener{
            startActivity(Intent(this, EditAccountActivity::class.java))
        }

        _binding.settingsLogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this, SignInActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
        //Hide action bar
        supportActionBar?.hide()
    }


}