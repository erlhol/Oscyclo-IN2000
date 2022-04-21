package com.example.sykkelapp

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sykkelapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.signInSignUpButton.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        val button = findViewById<Button>(R.id.sign_in_sign_up_button);
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}