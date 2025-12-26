package com.example.homeservice.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homeservice.R
import com.example.homeservice.models.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleLogin: Button
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Google Sign In Setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check if already logged in
        if (auth.currentUser != null) {
            checkRoleAndRedirect(auth.currentUser!!.uid) // [Updated]
        }

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        btnLogin.setOnClickListener {
            loginUser()
        }

        btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }

        tvSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleLogin", "Google sign in failed", e)
                Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    checkUserInFirestore(user?.uid, user?.displayName, user?.email)
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserInFirestore(uid: String?, name: String?, email: String?) {
        if (uid == null) return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User exists, check role and redirect
                    checkRoleAndRedirect(uid) // [Updated]
                } else {
                    // New User -> Save as Customer
                    val newUser = UserModel(
                        uid = uid,
                        name = name ?: "Unknown",
                        email = email ?: "",
                        role = "customer"
                    )
                    db.collection("users").document(uid).set(newUser)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                            checkRoleAndRedirect(uid) // [Updated]
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error creating profile", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Email is required"; return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid Email"; return
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"; return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        checkRoleAndRedirect(uid) // [Updated]
                    }
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // --- [NEW MAIN FUNCTION] Role Check Logic ---
    private fun checkRoleAndRedirect(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")

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
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    auth.signOut() // Data nathi to logout karavo safety mate
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching user role", Toast.LENGTH_SHORT).show()
            }
    }
}