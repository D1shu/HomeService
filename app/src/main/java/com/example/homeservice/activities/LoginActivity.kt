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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    // Variables
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleLogin: Button
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    // Firebase and Google Variables
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Google Login mate no Request Code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase initialize
        auth = FirebaseAuth.getInstance()

        // --- Google Sign In Setup ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Aa string automatic google-services.json mathi aavse
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Jo user pehle thi login hoy, to sidho Home screen par mokli do
        if (auth.currentUser != null) {
            goToHomeActivity()
        }

        // Views Bind
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // 1. Simple Email Login Click
        btnLogin.setOnClickListener {
            loginUser()
        }

        // 2. Google Login Button Click
        btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }

        // Sign Up Text Click
        tvSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Forgot Password
        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Google Sign In Function (UPDATED FOR ACCOUNT CHOOSER) ---
    private fun signInWithGoogle() {
        // Pehla Google Client mathi Sign Out karo, jethi dar vakhte account list batave
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // --- Jyare Google Account Select kari ne pacha aave tyare ---
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google account success madyu, have Firebase sathe link karo
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In Failed
                Log.w("GoogleLogin", "Google sign in failed", e)
                Toast.makeText(this, "Google Sign In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- Firebase Auth with Google Token ---
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(this, "Google Login Successful!", Toast.LENGTH_SHORT).show()
                    goToHomeActivity()
                } else {
                    // If sign in fails
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // --- Simple Email/Password Login Function ---
    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter valid email"
            etEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    goToHomeActivity()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, UserHomeActivity::class.java)
        // Login thaya pachi Back dabave to fari login page na aave
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}