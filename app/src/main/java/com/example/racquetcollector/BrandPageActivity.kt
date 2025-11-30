package com.example.racquetcollector

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.racquetcollector.api.AddToCollectionRequest
import com.example.racquetcollector.api.ApiClient
import com.example.racquetcollector.api.ApiService
import com.example.racquetcollector.api.Racquet
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrandPageActivity : AppCompatActivity() {

    private lateinit var racquetsAll: List<Racquet>
    private lateinit var apiService: ApiService
    private val userCollection = mutableMapOf<Int, Int>() // Map of Racquet ID to Collection Item ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_brand_page)

        val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        apiService = ApiClient.getClient(token)

        val brandName = intent.getStringExtra("brand_name") ?: "Brand"

        val toolbar = findViewById<MaterialToolbar>(R.id.brandToolbar)
        toolbar.title = ""
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val brandTitleView = findViewById<TextView>(R.id.brandTitle)
        brandTitleView.text = "$brandName • Racquets"

        val list = findViewById<LinearLayout>(R.id.brandList)
        val search = findViewById<EditText>(R.id.brandSearch)

        fun render(items: List<Racquet>) {
            list.removeAllViews()
            if (items.isEmpty()) {
                val empty = TextView(this).apply {
                    text = "No models found."
                    gravity = Gravity.CENTER_HORIZONTAL
                    setTextColor(Color.parseColor("#6B6B6B"))
                    textSize = 16f
                    setPadding(0, dp(32), 0, dp(8))
                }
                list.addView(empty)
            } else {
                items.forEach { racquet -> list.addView(racquetRow(racquet)) }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch user's collection to know which racquets are already favorited
                if (token != null) {
                    val collectionItems = apiService.getCollection()
                    userCollection.clear()
                    collectionItems.forEach { item ->
                        userCollection[item.racquet.id] = item.id
                    }
                }

                // Fetch all racquets for the brand
                val allRacquets = mutableListOf<Racquet>()
                var response = apiService.getRacquetsByBrand(brandName)
                allRacquets.addAll(response.results)

                var nextUrl = response.next
                while (nextUrl != null) {
                    val correctedUrl = nextUrl.replace("http://127.0.0.1:8000", "http://10.0.2.2:8000")
                    response = apiService.getRacquetsNextPage(correctedUrl)
                    allRacquets.addAll(response.results)
                    nextUrl = if (response.next != correctedUrl) response.next else null
                }

                racquetsAll = allRacquets
                withContext(Dispatchers.Main) {
                    render(racquetsAll)

                    search.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {}
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            val q = s?.toString()?.trim()?.lowercase().orEmpty()
                            val filtered = if (q.isEmpty()) racquetsAll else racquetsAll.filter { "${it.model_name} (${it.model_year})".lowercase().contains(q) }
                            render(filtered)
                        }
                    })
                }

            } catch (e: Exception) {
                Log.e("BrandPageActivity", "Error fetching data", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BrandPageActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        val scroll = findViewById<View>(R.id.scroll)
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }
    }

    private fun racquetRow(racquet: Racquet): View {
        val ctx = this
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_brand_item)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .apply { topMargin = dp(12) }
            isClickable = true
            isFocusable = true
            contentDescription = "${racquet.model_name} (${racquet.model_year})"
            setOnClickListener { openDetailActivity(racquet) }
        }

        val headerRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val itemTitle = TextView(ctx).apply {
            text = "${racquet.model_name} (${racquet.model_year})"
            textSize = 18f
            setTextColor(Color.parseColor("#333333"))
            setTypeface(Typeface.DEFAULT_BOLD)
        }
        headerRow.addView(itemTitle, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        val fav = TextView(ctx).apply {
            text = if (userCollection.containsKey(racquet.id)) "♥" else "♡"
            textSize = 18f
            setTextColor(Color.parseColor("#D23F57"))
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setOnClickListener { view ->
                toggleCollectionStatus(racquet, this)
                view.parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        headerRow.addView(fav)

        container.addView(headerRow)

        val itemSubtitle = TextView(ctx).apply {
            text = "Tap to view details"
            textSize = 14f
            setTextColor(Color.parseColor("#6B6B6B"))
        }
        container.addView(itemSubtitle)

        return container
    }

    private fun toggleCollectionStatus(racquet: Racquet, favIcon: TextView) {
        if (getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", null) == null) {
            Toast.makeText(this, "Please log in to manage your collection.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (userCollection.containsKey(racquet.id)) {
                    // Remove from collection
                    val collectionId = userCollection.remove(racquet.id)!!
                    apiService.removeFromCollection(collectionId)
                    withContext(Dispatchers.Main) {
                        favIcon.text = "♡"
                        Toast.makeText(this@BrandPageActivity, "Removed from collection", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Add to collection
                    val request = AddToCollectionRequest(racquet_id = racquet.id, notes = null)
                    val newItem = apiService.addToCollection(request)
                    userCollection[newItem.racquet.id] = newItem.id
                    withContext(Dispatchers.Main) {
                        favIcon.text = "♥"
                        Toast.makeText(this@BrandPageActivity, "Added to collection", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BrandPageActivity, "Failed to update collection", Toast.LENGTH_SHORT).show()
                    Log.e("BrandPageActivity", "Failed to update collection", e)
                }
            }
        }
    }

    private fun openDetailActivity(racquet: Racquet) {
        val intent = Intent(this, RacquetDetailActivity::class.java).apply {
            putExtra("racquet_details", racquet)
        }
        startActivity(intent)
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
