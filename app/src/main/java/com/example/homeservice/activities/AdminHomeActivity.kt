package com.example.homeservice.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homeservice.R
import com.google.firebase.auth.FirebaseAuth

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnAddService = findViewById<Button>(R.id.btnAddService)

        // Add Service Button Logic (Future ma ahin thi service add karshu)
        btnAddService.setOnClickListener {
            Toast.makeText(this, "Add Service Screen coming soon...", Toast.LENGTH_SHORT).show()
        }

        // Logout Logic
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}