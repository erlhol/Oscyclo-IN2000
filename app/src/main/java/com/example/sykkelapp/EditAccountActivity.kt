package com.example.sykkelapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sykkelapp.databinding.ActivityEditAccountBinding
import com.example.sykkelapp.ui.profile.ProfileFragment

class EditAccountActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityEditAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.saveButton.setOnClickListener{
            val intent = Intent(this@EditAccountActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
        }
    }
}