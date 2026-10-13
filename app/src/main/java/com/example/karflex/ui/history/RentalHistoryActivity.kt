package com.example.karflex.ui.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.R
import com.example.karflex.adapter.HistoryAdapter
import com.example.karflex.data.DatabaseHelper
import com.example.karflex.data.SessionManager

class RentalHistoryActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: HistoryAdapter
    private lateinit var emptyTV: TextView
    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rental_history)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Rental History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)
        userId = SessionManager(this).getUserId()
        emptyTV = findViewById(R.id.emptyTV)

        adapter = HistoryAdapter(ArrayList())
        val historyRV = findViewById<RecyclerView>(R.id.historyRV)
        historyRV.layoutManager = LinearLayoutManager(this)
        historyRV.adapter = adapter

        // filter list setiap kali user ngetik
        val searchET = findViewById<EditText>(R.id.searchET)
        searchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                loadHistory(s.toString().trim())
            }
        })

        loadHistory("")
    }

    private fun loadHistory(keyword: String) {
        val list = db.getRentalHistory(userId, keyword)
        adapter.setData(list)
        emptyTV.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
