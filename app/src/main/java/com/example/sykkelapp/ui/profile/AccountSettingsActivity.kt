package com.example.sykkelapp.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sykkelapp.databinding.ActivityAccountSettingsBinding

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.settingsEditButton.setOnClickListener{
            startActivity(Intent(this, EditAccountActivity::class.java))
        }
    }
}