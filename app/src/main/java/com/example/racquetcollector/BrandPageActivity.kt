package com.example.racquetcollector

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog

class BrandPageActivity : AppCompatActivity() {

    private lateinit var examplesAll: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_brand_page)

        val brandName = intent.getStringExtra("brand_name") ?: "Brand"

        // Toolbar with back arrow
        val toolbar = findViewById<MaterialToolbar>(R.id.brandToolbar)
        toolbar.title = "" // big title below handles branding
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Set big title
        val brandTitleView = findViewById<TextView>(R.id.brandTitle)
        brandTitleView.text = "$brandName • Racquets"

        // Mock data per brand
        examplesAll = when (brandName.lowercase()) {
            "head"    -> listOf("Head Speed MP", "Head Radical Pro", "Head Gravity Tour")
            "wilson"  -> listOf("Wilson Pro Staff 97", "Wilson Blade 98", "Wilson Ultra 100")
            "babolat" -> listOf("Babolat Pure Drive", "Babolat Pure Aero", "Babolat Pure Strike")
            "prince"  -> listOf("Prince Phantom 100", "Prince Tour 100", "Prince Legacy 105")
            else      -> listOf("Example A", "Example B", "Example C")
        }

        // Render list + wire filter
        val list = findViewById<LinearLayout>(R.id.brandList)
        fun render(items: List<String>) {
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
                items.forEach { model -> list.addView(exampleRow(model)) }
            }
        }
        render(examplesAll)

        val search = findViewById<EditText>(R.id.brandSearch)
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim()?.lowercase().orEmpty()
                val filtered = if (q.isEmpty()) examplesAll
                else examplesAll.filter { it.lowercase().contains(q) }
                render(filtered)
            }
        })

        // Edge-to-edge insets on root
        val scroll = findViewById<View>(R.id.scroll)
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }
    }

    /** Build one row (title + heart) and hook clicks to a BottomSheet */
    private fun exampleRow(modelName: String): View {
        val ctx = this
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            background = ResourcesCompat.getDrawable(resources, R.drawable.bg_brand_item, theme)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(12) }
            isClickable = true
            isFocusable = true
            contentDescription = modelName
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
            text = modelName
            textSize = 18f
            setTextColor(Color.parseColor("#333333"))
            setTypeface(Typeface.DEFAULT_BOLD)
        }
        headerRow.addView(itemTitle, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        val fav = TextView(ctx).apply {
            text = if (isFav(modelName)) "♥" else "♡"
            textSize = 18f
            setTextColor(Color.parseColor("#D23F57"))
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setOnClickListener {
                toggleFav(modelName)
                this.text = if (isFav(modelName)) "♥" else "♡"
            }
        }
        headerRow.addView(fav)

        container.addView(headerRow)

        val itemSubtitle = TextView(ctx).apply {
            text = "Tap to view details (mock)"
            textSize = 14f
            setTextColor(Color.parseColor("#6B6B6B"))
        }
        container.addView(itemSubtitle)

        // Open quick details
        container.setOnClickListener { showModelSheet(modelName) }

        return container
    }

    /** Quick mock details in a BottomSheet */
    private fun showModelSheet(modelName: String) {
        val dialog = BottomSheetDialog(this)
        val sheet = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }
        val h = TextView(this).apply {
            text = modelName
            textSize = 20f
            setTypeface(Typeface.DEFAULT_BOLD)
        }
        val sub = TextView(this).apply {
            text = "Specs (mock): 300 g • 100 sq in • 16×19 • 320 mm balance"
            textSize = 14f
            setTextColor(Color.parseColor("#555555"))
        }
        sheet.addView(h)
        sheet.addView(sub)
        dialog.setContentView(sheet)
        dialog.show()
    }

    /** Favorites stored locally */
    private fun isFav(model: String) =
        getSharedPreferences("fav", MODE_PRIVATE).getBoolean(model, false)

    private fun toggleFav(model: String) {
        val p = getSharedPreferences("fav", MODE_PRIVATE).edit()
        p.putBoolean(model, !isFav(model)).apply()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
