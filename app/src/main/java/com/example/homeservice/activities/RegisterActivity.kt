package com.example.homeservice.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homeservice.R
import com.example.homeservice.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView
    private lateinit var rgRole: RadioGroup
    private lateinit var rbCustomer: RadioButton
    private lateinit var rbProvider: RadioButton

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etRegEmail)
        etPassword = findViewById(R.id.etRegPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginLink = findViewById(R.id.tvLoginLink)
        rgRole = findViewById(R.id.rgRole)
        rbCustomer = findViewById(R.id.rbCustomer)
        rbProvider = findViewById(R.id.rbProvider)

        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        val selectedRoleId = rgRole.checkedRadioButtonId
        val role = if (selectedRoleId == R.id.rbProvider) "provider" else "customer"

        if (name.isEmpty()) {
            etName.error = "Name is required"; return
        }
        if (phone.length != 10) {
            etPhone.error = "Enter valid 10 digit number"; return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid Email"; return
        }
        if (password.length < 6) {
            etPassword.error = "Password min 6 chars"; return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid

                    if (uid != null) {
                        saveUserToFirestore(uid, name, phone, email, role)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Registration Failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserToFirestore(
        uid: String,
        name: String,
        phone: String,
        email: String,
        role: String
    ) {
        val user = UserModel(
            uid = uid,
            name = name,
            email = email,
            phone = phone,
            role = role
        )

        db.collection("users").document(uid).set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                // [UPDATED] Direct Redirect based on Role
                val intent = when (role) {
                    "provider" -> Intent(
                        this,
                        ProviderHomeActivity::class.java
                    ) // Ensure this Activity exists
                    "admin" -> Intent(
                        this,
                        AdminHomeActivity::class.java
                    )       // Ensure this Activity exists
                    else -> Intent(this, UserHomeActivity::class.java)
                }

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}