package com.example.karflex.ui.vehicle

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.karflex.R
import com.example.karflex.data.DatabaseHelper
import com.example.karflex.data.SessionManager
import com.example.karflex.utils.Utils

class VehicleDetailActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Vehicle Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)

        val vehicleId = intent.getIntExtra("vehicle_id", -1)
        val vehicle = db.getVehicleById(vehicleId)
        if (vehicle == null) {
            Toast.makeText(this, "Vehicle not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val vehicleIV = findViewById<ImageView>(R.id.vehicleIV)
        val modelNameTV = findViewById<TextView>(R.id.modelNameTV)
        val brandTV = findViewById<TextView>(R.id.brandTV)
        val priceTV = findViewById<TextView>(R.id.priceTV)
        val typeTV = findViewById<TextView>(R.id.typeTV)
        val transmissionTV = findViewById<TextView>(R.id.transmissionTV)
        val descriptionTV = findViewById<TextView>(R.id.descriptionTV)
        val rentedNoticeTV = findViewById<TextView>(R.id.rentedNoticeTV)
        val rentFormLayout = findViewById<LinearLayout>(R.id.rentFormLayout)
        val durationET = findViewById<EditText>(R.id.durationET)
        val rentBtn = findViewById<Button>(R.id.rentBtn)

        vehicleIV.setImageResource(Utils.getVehicleImage(vehicle.modelName))
        modelNameTV.text = vehicle.modelName
        brandTV.text = vehicle.brand
        priceTV.text = "${Utils.formatRupiah(vehicle.pricePerDay)} / day"
        typeTV.text = "Type : ${vehicle.type}"
        transmissionTV.text = "Transmission : ${vehicle.transmission}"
        descriptionTV.text = vehicle.description

        // kalau sudah disewa, form rent disembunyikan
        if (vehicle.isRented) {
            rentFormLayout.visibility = View.GONE
            rentedNoticeTV.visibility = View.VISIBLE
        } else {
            rentFormLayout.visibility = View.VISIBLE
            rentedNoticeTV.visibility = View.GONE
        }

        rentBtn.setOnClickListener {
            val input = durationET.text.toString().trim()

            // Duration must be filled
            if (input.isEmpty()) {
                Toast.makeText(this, "Duration must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Duration must be a number and more than 0
            val duration = input.toIntOrNull()
            if (duration == null) {
                Toast.makeText(this, "Duration must be a number!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (duration <= 0) {
                Toast.makeText(this, "Duration must be more than 0!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // cek ulang, siapa tau barusan sudah disewa
            if (db.getVehicleById(vehicle.id)?.isRented == true) {
                Toast.makeText(this, "Sorry, this vehicle is already rented out", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = SessionManager(this).getUserId()
            if (db.insertRental(vehicle.id, userId, duration, Utils.now())) {
                val total = Utils.formatRupiah(vehicle.pricePerDay * duration)
                Toast.makeText(this, "Rent successful! Total: $total", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to rent vehicle", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
