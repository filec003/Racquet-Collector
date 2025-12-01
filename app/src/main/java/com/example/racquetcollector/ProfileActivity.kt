package com.example.racquetcollector

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.racquetcollector.adapters.CollectionAdapter
import com.example.racquetcollector.api.ApiClient
import com.example.racquetcollector.api.ApiService
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            findTennisStores()
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<MaterialToolbar>(R.id.profile_toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token == null) {
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        apiService = ApiClient.getClient(token)

        val findStoresButton = findViewById<Button>(R.id.find_stores_button)
        findStoresButton.setOnClickListener {
            requestLocationPermission()
        }

        val collectionRecyclerView = findViewById<RecyclerView>(R.id.collection_recycler_view)
        collectionRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val userProfile = apiService.getProfile()
                val collection = apiService.getCollection()

                withContext(Dispatchers.Main) {
                    val profileName = findViewById<TextView>(R.id.profile_name)
                    val profileEmail = findViewById<TextView>(R.id.profile_email)

                    profileName.text = "${userProfile.first_name} ${userProfile.last_name}"
                    profileEmail.text = userProfile.email

                    collectionRecyclerView.adapter = CollectionAdapter(collection)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                findTennisStores()
            }
            else -> {
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))            }
        }
    }

    private fun findTennisStores() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val gmmIntentUri = Uri.parse("geo:${location.latitude},${location.longitude}?q=tennis+stores")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "Could not get location.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
