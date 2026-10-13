package com.example.karflex.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.karflex.R
import com.example.karflex.adapter.VehicleAdapter
import com.example.karflex.data.DatabaseHelper
import com.example.karflex.data.model.Vehicle
import com.example.karflex.ui.vehicle.VehicleDetailActivity

class BrowseFragment : Fragment() {

    companion object {
        const val JSON_URL =
            "https://gist.githubusercontent.com/axelkrnwn/01c506ac60735f553af951e550634b59/raw/08489b2ff76e1356a6df38d09948716628024195/karflex.json"
    }

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: VehicleAdapter
    private lateinit var loadingPB: ProgressBar
    private lateinit var emptyTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        loadingPB = view.findViewById(R.id.loadingPB)
        emptyTV = view.findViewById(R.id.emptyTV)

        adapter = VehicleAdapter(ArrayList()) { vehicle ->
            val intent = Intent(requireContext(), VehicleDetailActivity::class.java)
            intent.putExtra("vehicle_id", vehicle.id)
            startActivity(intent)
        }

        val vehicleRV = view.findViewById<RecyclerView>(R.id.vehicleRV)
        vehicleRV.layoutManager = LinearLayoutManager(requireContext())
        vehicleRV.adapter = adapter

        fetchVehicles()
    }

    override fun onResume() {
        super.onResume()
        // refresh tiap balik ke halaman ini, biar status rented-nya update
        loadVehicles()
    }

    // ambil data dari JSON pakai Volley, lalu simpan ke SQLite
    private fun fetchVehicles() {
        loadingPB.visibility = View.VISIBLE

        val request = JsonArrayRequest(
            Request.Method.GET, JSON_URL, null,
            { response ->
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val vehicle = Vehicle(
                        obj.getInt("id"),
                        obj.getString("model_name"),
                        obj.getString("type"),
                        obj.getString("brand"),
                        obj.getInt("price_per_day"),
                        obj.getString("transmission"),
                        obj.getString("image_url"),
                        obj.getString("description")
                    )
                    db.saveVehicle(vehicle)
                }

                // fragment bisa aja sudah diganti waktu response datang
                if (!isAdded) return@JsonArrayRequest
                loadingPB.visibility = View.GONE
                loadVehicles()
            },
            { error ->
                Log.e("BrowseFragment", "Volley error: ${error.message}")
                if (!isAdded) return@JsonArrayRequest
                loadingPB.visibility = View.GONE
                // kalau gagal tetap tampilkan data yang sudah ada di database
                Toast.makeText(requireContext(), "Failed to fetch vehicle data, check your connection", Toast.LENGTH_SHORT).show()
                loadVehicles()
            }
        )

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun loadVehicles() {
        val list = db.getAllVehicles()
        adapter.setData(list)

        // empty text cuma muncul kalau sudah selesai loading tapi datanya kosong
        if (list.isEmpty() && loadingPB.visibility != View.VISIBLE) {
            emptyTV.visibility = View.VISIBLE
        } else {
            emptyTV.visibility = View.GONE
        }
    }
}
