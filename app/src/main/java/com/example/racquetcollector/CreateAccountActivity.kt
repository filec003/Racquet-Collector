package com.example.racquetcollector   // ⚠️ use the same package name as your other activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val emailInput = findViewById<TextInputEditText>(R.id.etNewEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.etNewPassword)
        val confirmInput = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val createBtn = findViewById<MaterialButton>(R.id.btnCreate)
        val backBtn = findViewById<MaterialButton>(R.id.btnBackToLogin)

        // Handle "Create Account"
        createBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString()
            val confirm = confirmInput.text.toString()

            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else if (pass != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Account created for $email", Toast.LENGTH_SHORT).show()
                finish() // returns to LoginActivity
            }
        }

        // Handle "Back to Log In"
        backBtn.setOnClickListener {
            finish() // goes back to LoginActivity
        }
    }
}
