package com.example.racquetcollector

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.racquetcollector.api.Racquet
import com.google.android.material.appbar.MaterialToolbar

class RacquetDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_racquet_detail)

        val racquet = intent.getParcelableExtra<Racquet>("racquet_details")

        val toolbar = findViewById<MaterialToolbar>(R.id.detailToolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        if (racquet != null) {
            val racquetName = findViewById<TextView>(R.id.racquetName)
            racquetName.text = "${racquet.model_name} (${racquet.model_year})"

            val specTable = findViewById<TableLayout>(R.id.specTable)
            addSpecRow(specTable, "Head Size", "${racquet.head_size_in2} in²")
            addSpecRow(specTable, "Length", "${racquet.length_in} in")
            addSpecRow(specTable, "Unstrung Weight", "${racquet.unstrung_weight_g} g")
            addSpecRow(specTable, "Strung Weight", "${racquet.strung_weight_g} g")
            addSpecRow(specTable, "Swing Weight", "${racquet.swing_weight}")
            addSpecRow(specTable, "Twist Weight", "${racquet.twist_weight}")
            addSpecRow(specTable, "Balance", "${racquet.balance_mm} mm")
            addSpecRow(specTable, "String Pattern", "${racquet.mains}x${racquet.crosses}")

            val buyButton = findViewById<Button>(R.id.buyButton)
            buyButton.setOnClickListener {
                val query = "${racquet.brand_name} ${racquet.model_name} ${racquet.model_year}"
                val url = "https://www.google.com/search?tbm=shop&q=${Uri.encode(query)}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }

    private fun addSpecRow(table: TableLayout, specName: String, specValue: String) {
        val row = TableRow(this)
        val nameView = TextView(this).apply {
            text = specName
            setPadding(8, 8, 8, 8)
            setTypeface(null, Typeface.BOLD)
        }
        val valueView = TextView(this).apply {
            text = specValue
            setPadding(8, 8, 8, 8)
        }
        row.addView(nameView)
        row.addView(valueView)
        table.addView(row)
    }
}