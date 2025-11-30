package com.example.racquetcollector

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Main content ScrollView
        val scroll = ScrollView(this).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.parseColor("#FFF8F0"))
            isFillViewport = true
        }

        // Root column inside ScrollView
        val rootCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(24))
        }
        scroll.addView(rootCol)

        // Hamburger menu icon
        val menu = TextView(this).apply {
            text = "≡"
            textSize = 30f // Made it a bit bigger
            setPadding(dp(8), dp(4), dp(8), dp(4))
            setTextColor(Color.parseColor("#104E3B"))
            setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        rootCol.addView(menu)

        // Title
        fun titleLabel(txt: String) = TextView(this).apply {
            text = txt
            textSize = 36f
            setTextColor(Color.parseColor("#104E3B"))
            typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
            isAllCaps = true
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = if (txt == "RACQUET") 0 else dp(2) }
        }
        rootCol.addView(titleLabel("RACQUET"))
        rootCol.addView(titleLabel("COLLECTOR"))

        // Search bar
        val searchBg = rounded(bg = Color.WHITE, stroke = Color.parseColor("#D9D4CC"))
        val search = EditText(this).apply {
            hint = "Search"
            inputType = InputType.TYPE_CLASS_TEXT
            background = searchBg
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
            ).apply { topMargin = dp(20) }
        }
        rootCol.addView(search)

        fun brandCard(label: String, tintHex: String): View {
            val cardBg = rounded(bg = Color.WHITE, stroke = Color.parseColor("#D9D4CC"), radius = 18f)
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = cardBg
                layoutParams = LinearLayout.LayoutParams(0, dp(220), 1f).apply {
                    marginStart = dp(8); marginEnd = dp(8); topMargin = dp(8)
                }

                isClickable = true
                isFocusable = true
                setOnClickListener { openBrandPage(label) }
                contentDescription = "$label brand"

                val icon = TextView(this@MainActivity).apply {
                    text = "🎾"
                    textSize = 64f
                    setTextColor(Color.parseColor(tintHex))
                    gravity = Gravity.CENTER
                }
                addView(icon)

                val name = TextView(this@MainActivity).apply {
                    text = label
                    textSize = 22f
                    setTypeface(typeface, Typeface.BOLD)
                    setTextColor(Color.parseColor("#333333"))
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = dp(8) }
                }
                addView(name)
            }
        }

        fun rowOf(vararg views: View) = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(12) }
            views.forEach { addView(it) }
        }

        val head = "#F15A29"
        val wilson = "#1F7A4D"
        val babolat = "#F5A623"
        val prince = "#2A72B5"

        rootCol.addView(rowOf(brandCard("HEAD", head), brandCard("Wilson", wilson)))
        rootCol.addView(rowOf(brandCard("Babolat", babolat), brandCard("Prince", prince)))

        // Navigation View
        val navigationView = NavigationView(this).apply {
            id = View.generateViewId()
            layoutParams = DrawerLayout.LayoutParams(
                DrawerLayout.LayoutParams.WRAP_CONTENT,
                DrawerLayout.LayoutParams.MATCH_PARENT,
                GravityCompat.START
            )
            inflateMenu(R.menu.nav_menu)
            setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    R.id.nav_profile -> {
                        startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                    }
                    R.id.nav_compare -> {
                        startActivity(Intent(this@MainActivity, CompareActivity::class.java))
                    }
                    R.id.nav_logout -> {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                menuItem.isChecked = true
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }

        // DrawerLayout as the root
        drawerLayout = DrawerLayout(this).apply {
            id = View.generateViewId()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Add main content
            addView(scroll)
            // Add navigation drawer
            addView(navigationView)
        }

        setContentView(drawerLayout)

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun openBrandPage(name: String) {
        val i = Intent(this, BrandPageActivity::class.java)
        i.putExtra("brand_name", name)
        startActivity(i)
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun rounded(
        bg: Int,
        stroke: Int,
        radius: Float = 14f,
        strokeWidthDp: Int = 1
    ): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(radius.toInt()).toFloat()
        setColor(bg)
        setStroke(dp(strokeWidthDp), stroke)
    }
}
