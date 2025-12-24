package com.example.homeservice.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homeservice.R
import com.google.firebase.auth.FirebaseAuth
// Firestore no import hatavi didho che

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    // Only Auth instance (Firestore hatavi didhu)
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // db instance kadhi nakhyo

        // Bind Views
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etRegEmail)
        etPassword = findViewById(R.id.etRegPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        // Register Button Click
        btnRegister.setOnClickListener {
            registerUser()
        }

        // Login Link Click
        tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            etName.error = "Name is required"
            return
        }
        if (phone.length != 10) {
            etPhone.error = "Enter valid 10 digit number"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid Email"
            return
        }
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }

        // Create User in Firebase Auth Only
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                    // Redirect to Home
                    val intent = Intent(this, UserHomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    // saveUserToFirestore function hatavi didho che
}