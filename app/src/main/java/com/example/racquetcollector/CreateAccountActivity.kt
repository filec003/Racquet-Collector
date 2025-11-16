package com.example.racquetcollector

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val emailInput = findViewById<TextInputEditText>(R.id.etNewEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.etNewPassword)
        val confirmInput = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val createBtn = findViewById<MaterialButton>(R.id.btnCreate)
        val backBtn = findViewById<MaterialButton>(R.id.btnBackToLogin)

        createBtn.setOnClickListener {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val pass = passwordInput.text?.toString().orEmpty()
            val confirm = confirmInput.text?.toString().orEmpty()

            when {
                email.isEmpty() || pass.isEmpty() || confirm.isEmpty() ->
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                pass != confirm ->
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                else -> {
                    Toast.makeText(this, "Account created for $email (mock)", Toast.LENGTH_SHORT).show()
                    finish() // go back to LoginActivity
                }
            }
        }

        backBtn.setOnClickListener { finish() }
    }
}
