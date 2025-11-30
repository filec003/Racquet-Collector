package com.example.racquetcollector

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.racquetcollector.adapters.CollectionAdapter
import com.example.racquetcollector.api.ApiClient
import com.example.racquetcollector.api.ApiService
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<MaterialToolbar>(R.id.profile_toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token == null) {
            // Handle the case where the token is not available
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        apiService = ApiClient.getClient(token)

        val collectionRecyclerView = findViewById<RecyclerView>(R.id.collection_recycler_view)
        collectionRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val userProfile = apiService.getProfile()
                val profileName = findViewById<TextView>(R.id.profile_name)
                val profileEmail = findViewById<TextView>(R.id.profile_email)

                profileName.text = "${userProfile.first_name} ${userProfile.last_name}"
                profileEmail.text = userProfile.email

                val collection = apiService.getCollection()
                collectionRecyclerView.adapter = CollectionAdapter(collection)

            } catch (e: Exception) {
                // Handle error
                Toast.makeText(this@ProfileActivity, "Failed to load profile.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
