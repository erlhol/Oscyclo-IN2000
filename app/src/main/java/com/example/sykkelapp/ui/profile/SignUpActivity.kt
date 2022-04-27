package com.example.sykkelapp.ui.profile

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.signUpSignInButton.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }

        val button = findViewById<Button>(R.id.sign_up_sign_in_button);
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}