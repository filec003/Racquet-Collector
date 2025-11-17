package com.example.racquetcollector   // ⚠️ use the same package name as your other activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.racquetcollector.api.ApiClient
import com.example.racquetcollector.api.ApiService
import com.example.racquetcollector.api.RegisterRequest
import kotlinx.coroutines.launch
import android.util.Log

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val emailInput = findViewById<TextInputEditText>(R.id.etNewEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.etNewPassword)
        val confirmInput = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val createBtn = findViewById<MaterialButton>(R.id.btnCreate)
        val backBtn = findViewById<MaterialButton>(R.id.btnBackToLogin)

        //initialize ApiService
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Handle "Create Account"
        createBtn.setOnClickListener {
            val firstName = findViewById<TextInputEditText>(R.id.etFirstName).text.toString()
            val lastName = findViewById<TextInputEditText>(R.id.etLastName).text.toString()
            val username = findViewById<TextInputEditText>(R.id.etUsername).text.toString()
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString()
            val confirm = confirmInput.text.toString()

            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else if (pass != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                try {
                    apiService.register(RegisterRequest(username,pass,firstName,lastName,email))
                    Toast.makeText(this@CreateAccountActivity, "Account created for $username", Toast.LENGTH_SHORT).show()
                    finish() // returns to LoginActivity
                } catch(e:Exception){
                    Log.e("CreateAccount", "Registration failed", e)
                    Toast.makeText(this@CreateAccountActivity,
                        "Account Creation Failed",
                        Toast.LENGTH_LONG
                        ).show()
                }
                }

            }
        }

        // Handle "Back to Log In"
        backBtn.setOnClickListener {
            finish() // goes back to LoginActivity
        }
    }
}
