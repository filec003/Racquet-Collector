package com.example.racquetcollector

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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Root scroll container
        val scroll = ScrollView(this).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.parseColor("#FFF8F0")) // bg
            isFillViewport = true
        }

        // Vertical content column
        val rootCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(24))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scroll.addView(rootCol)

        // “Menu” placeholder (just text so we avoid drawable deps)
        val menu = TextView(this).apply {
            text = "≡"
            textSize = 24f
            setTextColor(Color.parseColor("#104E3B"))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        rootCol.addView(menu)

        // Big two-line title
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

        // Fake search box
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

        // 2x2 grid made from two horizontal rows
        fun brandCard(label: String, tintHex: String): View {
            val cardBg = rounded(bg = Color.WHITE, stroke = Color.parseColor("#D9D4CC"), radius = 18f)
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = cardBg
                layoutParams = LinearLayout.LayoutParams(0, dp(220), 1f).apply {
                    marginStart = dp(8); marginEnd = dp(8); topMargin = dp(8)
                }

                // Emoji “icon” (no drawables needed)
                val icon = TextView(this@MainActivity).apply {
                    text = "🎾"
                    textSize = 64f
                    setTextColor(Color.parseColor(tintHex))
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
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

        fun rowOf(vararg views: View): View {
            return LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(12) }
                views.forEach { addView(it) }
            }
        }

        // Colors for each brand tint
        val head = "#F15A29"
        val wilson = "#1F7A4D"
        val babolat = "#F5A623"
        val prince = "#2A72B5"

        rootCol.addView(
            rowOf(
                brandCard("HEAD", head),
                brandCard("Wilson", wilson)
            )
        )
        rootCol.addView(
            rowOf(
                brandCard("Babolat", babolat),
                brandCard("Prince", prince)
            )
        )

        setContentView(scroll)

        // Edge-to-edge insets padding on the programmatic root
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }
    }

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun rounded(
        bg: Int,
        stroke: Int,
        radius: Float = 14f,
        strokeWidthDp: Int = 1
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(radius.toInt()).toFloat()
            setColor(bg)
            setStroke(dp(strokeWidthDp), stroke)
        }
    }
}
