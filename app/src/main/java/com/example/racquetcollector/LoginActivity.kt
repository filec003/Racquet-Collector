package com.example.racquetcollector

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.racquetcollector.api.ApiClient
import com.example.racquetcollector.api.ApiService
import com.example.racquetcollector.api.LoginRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize ApiService
        apiService = ApiClient.getClient()

        val usernameEditText = findViewById<TextInputEditText>(R.id.etUsername)
        val passwordEditText = findViewById<TextInputEditText>(R.id.etPassword)
        val loginBtn = findViewById<MaterialButton>(R.id.btnLogin)

        loginBtn.setOnClickListener {
            val userName = usernameEditText.text.toString()
            val userPassword = passwordEditText.text.toString()

            // Launch a coroutine for the network call
            lifecycleScope.launch {
                try {
                    val tokenResponse = apiService.login(
                        LoginRequest(userName, userPassword)
                    )
                    // Save the token
                    val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("token", tokenResponse.access).apply()

                    // Login successful, move to MainActivity
                    Toast.makeText(
                        this@LoginActivity,
                        "Login successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    // Handle errors (network failure, 401, etc.)
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Forgot password
        val forgot = findViewById<TextView>(R.id.forgotPassword)
        forgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Create account
        val createAccountBtn = findViewById<MaterialButton>(R.id.btnCreateAccount)
        createAccountBtn.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }
}
