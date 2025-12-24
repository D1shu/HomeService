package com.example.homeservice.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.homeservice.R
import com.google.firebase.auth.FirebaseAuth

class UserHomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nameTxt: TextView
    private lateinit var profileBtn: ImageView
    private lateinit var categoryList: RecyclerView

    // 1. Naya Button mate variable banavyo
    private lateinit var logoutBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Views Binding
        nameTxt = findViewById(R.id.nameTxt)
        profileBtn = findViewById(R.id.profileBtn)
        categoryList = findViewById(R.id.categoryList)

        // 2. XML mathi ID shodhyu (Junu step yaad rakhje XML ma ID add karvanu)
        logoutBtn = findViewById(R.id.logoutBtn)

        // User Name Set karvu
        if (currentUser != null) {
            val email = currentUser.email
            val name = email?.substringBefore("@")
            nameTxt.text = name?.capitalize()
        }

        // 3. New Logout Logic (Upar vala button mate)
        logoutBtn.setOnClickListener {
            auth.signOut() // Firebase logout
            Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()

            // Login Screen par java mate
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 4. Profile Button Logic (Bottom Nav) - Have ahiya logout nai thay
        profileBtn.setOnClickListener {
            Toast.makeText(this, "Profile Section Coming Soon...", Toast.LENGTH_SHORT).show()
        }

        // Home Button Click
        findViewById<ImageView>(R.id.homeBtn).setOnClickListener {
            Toast.makeText(this, "You are on Home", Toast.LENGTH_SHORT).show()
        }

        // Explore Button
        findViewById<ImageView>(R.id.exploreBtn).setOnClickListener {
            Toast.makeText(this, "Explore Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}