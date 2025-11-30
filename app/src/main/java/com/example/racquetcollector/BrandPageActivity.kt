package com.example.racquetcollector

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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.racquetcollector.api.ApiClient
import com.example.racquetcollector.api.ApiService
import com.example.racquetcollector.api.Racquet
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class BrandPageActivity : AppCompatActivity() {

    private lateinit var racquetsAll: List<Racquet>
    private val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_brand_page)

        val brandName = intent.getStringExtra("brand_name") ?: "Brand"

        // Toolbar back arrow
        val toolbar = findViewById<MaterialToolbar>(R.id.brandToolbar)
        toolbar.title = "" // Big title below handles branding
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Big title
        val brandTitleView = findViewById<TextView>(R.id.brandTitle)
        brandTitleView.text = "$brandName • Racquets"

        // Render list + wire filter
        val list = findViewById<LinearLayout>(R.id.brandList)

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

        lifecycleScope.launch {
            try {
                val response = apiService.getRacquetsByBrand(brandName)
                racquetsAll = response.results
                render(racquetsAll)
            } catch (e: Exception) {
                Log.e("BrandPageActivity", "Error fetching racquets", e)
                // Optionally show an error message to the user
            }
        }

        val search = findViewById<EditText>(R.id.brandSearch)
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim()?.lowercase().orEmpty()
                val filtered = if (q.isEmpty()) racquetsAll else racquetsAll.filter { "${it.model_name} (${it.model_year})".lowercase().contains(q) }
                render(filtered)
            }
        })

        // Edge-to-edge insets on root scroll
        val scroll = findViewById<View>(R.id.scroll)
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }
    }

    /** Build one row (title + heart) and hook clicks to the detail page */
    private fun racquetRow(racquet: Racquet): View {
        val ctx = this
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_brand_item)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(12) }
            isClickable = true
            isFocusable = true
            contentDescription = "${racquet.model_name} (${racquet.model_year})"
            setOnClickListener { openDetailActivity(racquet) }
        }

        // Header row: title (left) + heart fav (right)
        val headerRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val itemTitle = TextView(ctx).apply {
            text = "${racquet.model_name} (${racquet.model_year})"
            textSize = 18f
            setTextColor(Color.parseColor("#333333"))
            setTypeface(Typeface.DEFAULT_BOLD)
        }
        headerRow.addView(itemTitle, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        val fav = TextView(ctx).apply {
            text = if (isFav(racquet.id.toString())) "♥" else "♡"
            textSize = 18f
            setTextColor(Color.parseColor("#D23F57"))
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setOnClickListener { view ->
                toggleFav(racquet.id.toString())
                this.text = if (isFav(racquet.id.toString())) "♥" else "♡"
                // Prevent this click from triggering the container's click listener
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

    private fun openDetailActivity(racquet: Racquet) {
        val intent = Intent(this, RacquetDetailActivity::class.java).apply {
            putExtra("racquet_details", racquet)
        }
        startActivity(intent)
    }

    /** Favorites stored locally */
    private fun isFav(modelId: String) =
        getSharedPreferences("fav", MODE_PRIVATE).getBoolean(modelId, false)

    private fun toggleFav(modelId: String) {
        val p = getSharedPreferences("fav", MODE_PRIVATE).edit()
        p.putBoolean(modelId, !isFav(modelId)).apply()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
