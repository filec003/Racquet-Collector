package com.example.racquetcollector

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            // Switch to MainActivity when button is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // prevents going back to login on Back press
        }
    }
}
