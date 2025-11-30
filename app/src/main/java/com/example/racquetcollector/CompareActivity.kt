package com.example.racquetcollector

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.racquetcollector.api.ApiClient
import com.example.racquetcollector.api.ApiService
import com.example.racquetcollector.api.Racquet
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompareActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val apiService by lazy { ApiClient.getClient() }
    private val brands = arrayOf("HEAD", "Wilson", "Babolat", "Prince")

    private var racquetOne: Racquet? = null
    private var racquetTwo: Racquet? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var buttonOne: ImageButton
    private lateinit var buttonTwo: ImageButton
    private lateinit var textOne: TextView
    private lateinit var textTwo: TextView
    private lateinit var compareTable: TableLayout
    private lateinit var buyButtonsContainer: LinearLayout
    private lateinit var buyButtonOne: Button
    private lateinit var buyButtonTwo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<MaterialToolbar>(R.id.compare_toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        buttonOne = findViewById(R.id.button_add_racquet_one)
        buttonTwo = findViewById(R.id.button_add_racquet_two)
        textOne = findViewById(R.id.text_racquet_one)
        textTwo = findViewById(R.id.text_racquet_two)
        compareTable = findViewById(R.id.table_compare)
        buyButtonsContainer = findViewById(R.id.buy_buttons_container)
        buyButtonOne = findViewById(R.id.buy_button_one)
        buyButtonTwo = findViewById(R.id.buy_button_two)

        buttonOne.setOnClickListener { selectRacquet(1) }
        buttonTwo.setOnClickListener { selectRacquet(2) }
        textOne.setOnClickListener { selectRacquet(1) }
        textTwo.setOnClickListener { selectRacquet(2) }

        buyButtonOne.setOnClickListener { buyRacquet(racquetOne) }
        buyButtonTwo.setOnClickListener { buyRacquet(racquetTwo) }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_compare -> {
                // Already on this screen, just close the drawer
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun selectRacquet(racquetNumber: Int) {
        AlertDialog.Builder(this)
            .setTitle("Select a Brand")
            .setItems(brands) { _, which ->
                showRacquetSearchDialog(brands[which], racquetNumber)
            }
            .show()
    }

    private fun showRacquetSearchDialog(brand: String, racquetNumber: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_racquet_search, null)
        val searchEditText = dialogView.findViewById<EditText>(R.id.search_edit_text)
        val racquetListView = dialogView.findViewById<ListView>(R.id.racquet_list_view)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Search for a Racquet")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val allRacquets = mutableListOf<Racquet>()
                var response = apiService.getRacquetsByBrand(brand)
                allRacquets.addAll(response.results)

                var nextUrl = response.next
                while (nextUrl != null) {
                    val correctedUrl = nextUrl.replace("http://127.0.0.1:8000", "http://10.0.2.2:8000")
                    response = apiService.getRacquetsNextPage(correctedUrl)
                    allRacquets.addAll(response.results)
                    nextUrl = if (response.next != correctedUrl) response.next else null
                }

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(this@CompareActivity, android.R.layout.simple_list_item_1, allRacquets.map { "${it.model_name} (${it.model_year})" })
                    racquetListView.adapter = adapter

                    racquetListView.setOnItemClickListener { _, _, position, _ ->
                        val selectedModelString = adapter.getItem(position)
                        val selectedRacquet = allRacquets.find { "${it.model_name} (${it.model_year})" == selectedModelString }

                        if (selectedRacquet != null) {
                            if (racquetNumber == 1) {
                                racquetOne = selectedRacquet
                                textOne.text = "${selectedRacquet.model_name}\n(${selectedRacquet.model_year})"
                                textOne.visibility = View.VISIBLE
                                buttonOne.visibility = View.GONE
                            } else {
                                racquetTwo = selectedRacquet
                                textTwo.text = "${selectedRacquet.model_name}\n(${selectedRacquet.model_year})"
                                textTwo.visibility = View.VISIBLE
                                buttonTwo.visibility = View.GONE
                            }
                            updateCompareTable()
                            dialog.dismiss()
                        }
                    }

                    searchEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            adapter.filter.filter(s)
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                }

            } catch (e: Exception) {
                Log.e("CompareActivity", "Error fetching racquets", e)
            }
        }

        dialog.show()
    }

    private fun updateCompareTable() {
        compareTable.removeAllViews()
        buyButtonsContainer.visibility = View.GONE

        if (racquetOne == null || racquetTwo == null) return

        buyButtonsContainer.visibility = View.VISIBLE

        // Header Row
        val headerRow = TableRow(this).apply {
            setBackgroundColor(Color.parseColor("#0E7A4F")) // Dark green from login
        }
        val headerPadding = dp(12)
        headerRow.addView(createTextView("Spec", Color.WHITE, Typeface.BOLD, Gravity.START, headerPadding))
        headerRow.addView(createTextView("${racquetOne!!.model_name}\n(${racquetOne!!.model_year})", Color.WHITE, Typeface.BOLD, Gravity.CENTER, headerPadding))
        headerRow.addView(createTextView("${racquetTwo!!.model_name}\n(${racquetTwo!!.model_year})", Color.WHITE, Typeface.BOLD, Gravity.CENTER, headerPadding))
        compareTable.addView(headerRow)

        // Data Rows
        var rowIndex = 0
        addRow("Head Size (in²)", racquetOne?.head_size_in2.toString(), racquetTwo?.head_size_in2.toString(), rowIndex++)
        addRow("Length (in)", racquetOne?.length_in.toString(), racquetTwo?.length_in.toString(), rowIndex++)
        addRow("Unstrung Weight (g)", racquetOne?.unstrung_weight_g.toString(), racquetTwo?.unstrung_weight_g.toString(), rowIndex++)
        addRow("Strung Weight (g)", racquetOne?.strung_weight_g.toString(), racquetTwo?.strung_weight_g.toString(), rowIndex++)
        addRow("Swing Weight", racquetOne?.swing_weight.toString(), racquetTwo?.swing_weight.toString(), rowIndex++)
        addRow("Twist Weight", racquetOne?.twist_weight.toString(), racquetTwo?.twist_weight.toString(), rowIndex++)
        addRow("Balance (mm)", racquetOne?.balance_mm.toString(), racquetTwo?.balance_mm.toString(), rowIndex++)
        addRow("String Pattern", "${racquetOne?.mains}x${racquetOne?.crosses}", "${racquetTwo?.mains}x${racquetTwo?.crosses}", rowIndex++)
    }

    private fun addRow(spec: String, value1: String, value2: String, rowIndex: Int) {
        val row = TableRow(this)
        val rowPadding = dp(10)

        // Zebra striping
        if (rowIndex % 2 == 0) {
            row.setBackgroundColor(Color.parseColor("#F3F1EC")) // Light cream from login
        } else {
            row.setBackgroundColor(Color.WHITE)
        }

        row.addView(createTextView(spec, Color.BLACK, Typeface.BOLD, Gravity.START, rowPadding))
        row.addView(createTextView(value1, Color.DKGRAY, Typeface.NORMAL, Gravity.CENTER, rowPadding))
        row.addView(createTextView(value2, Color.DKGRAY, Typeface.NORMAL, Gravity.CENTER, rowPadding))
        compareTable.addView(row)
    }

    private fun createTextView(text: String, textColor: Int, style: Int, gravity: Int, padding: Int): TextView {
        return TextView(this).apply {
            this.text = text
            setTextColor(textColor)
            setTypeface(null, style)
            this.gravity = gravity
            setPadding(padding, padding, padding, padding)
        }
    }

    private fun buyRacquet(racquet: Racquet?) {
        racquet?.let {
            val query = "${it.brand_name} ${it.model_name} ${it.model_year}"
            val url = "https://www.google.com/search?tbm=shop&q=${Uri.encode(query)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
