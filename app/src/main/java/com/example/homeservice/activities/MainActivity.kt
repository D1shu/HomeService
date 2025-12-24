package com.example.homeservice.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.homeservice.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase initialize
        auth = FirebaseAuth.getInstance()

        // 3 Second no delay (3000 milliseconds)
        Handler(Looper.getMainLooper()).postDelayed({

            checkUserLoginStatus()

        }, 1000)
    }

    private fun checkUserLoginStatus() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // == Jo User Login hoy to ==

            // NOTE: Future ma ahiya check karsu ke user "Admin" che ke "Provider".
            // Hal purtu badha ne UserHomeActivity par mokaliye.
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
            finish() // Splash screen bandh kari devani

        } else {
            // == Jo Koi Login na hoy to ==
            // Login Screen par mokalo
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}