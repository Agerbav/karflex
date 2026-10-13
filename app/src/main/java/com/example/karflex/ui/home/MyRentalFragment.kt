package com.example.karflex.ui.home

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.R
import com.example.karflex.adapter.RentalAdapter
import com.example.karflex.data.DatabaseHelper
import com.example.karflex.data.SessionManager
import com.example.karflex.data.model.Rental

class MyRentalFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: RentalAdapter
    private lateinit var emptyTV: TextView
    private var userId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_rental, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        userId = SessionManager(requireContext()).getUserId()
        emptyTV = view.findViewById(R.id.emptyTV)

        adapter = RentalAdapter(
            ArrayList(),
            onUpdate = { rental -> showUpdateDialog(rental) },
            onCancel = { rental -> showCancelDialog(rental) }
        )

        val rentalRV = view.findViewById<RecyclerView>(R.id.rentalRV)
        rentalRV.layoutManager = LinearLayoutManager(requireContext())
        rentalRV.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadRentals()
    }

    private fun loadRentals() {
        // di dalam getActiveRentals sudah ada pengecekan Active -> Completed
        val list = db.getActiveRentals(userId)
        adapter.setData(list)
        emptyTV.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showUpdateDialog(rental: Rental) {
        val durationET = EditText(requireContext())
        durationET.inputType = InputType.TYPE_CLASS_NUMBER
        durationET.hint = "Duration (days)"
        durationET.setText(rental.duration.toString())
        durationET.setSelection(durationET.text.length)

        // dikasih margin biar EditText-nya gak nempel ke pinggir dialog
        val container = FrameLayout(requireContext())
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val margin = (20 * resources.displayMetrics.density).toInt()
        params.setMargins(margin, 0, margin, 0)
        durationET.layoutParams = params
        container.addView(durationET)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Update Duration")
            .setMessage("Enter new rental duration for ${rental.modelName}")
            .setView(container)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // override tombol Update supaya dialog gak langsung ketutup kalau validasi gagal
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val input = durationET.text.toString().trim()

            if (input.isEmpty()) {
                Toast.makeText(requireContext(), "Duration must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = input.toIntOrNull()
            if (duration == null) {
                Toast.makeText(requireContext(), "Duration must be a number!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (duration <= 0) {
                Toast.makeText(requireContext(), "Duration must be more than 0!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.updateRentalDuration(rental.id, duration)) {
                Toast.makeText(requireContext(), "Rental duration updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update rental", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
            loadRentals()
        }
    }

    private fun showCancelDialog(rental: Rental) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Rental")
            .setMessage("Are you sure you want to cancel your rental for ${rental.modelName}?")
            .setPositiveButton("Yes") { _, _ ->
                if (db.deleteRental(rental.id)) {
                    Toast.makeText(requireContext(), "Rental has been cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to cancel rental", Toast.LENGTH_SHORT).show()
                }
                loadRentals()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
