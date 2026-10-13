package com.example.karflex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.R
import com.example.karflex.data.model.Rental
import com.example.karflex.utils.Utils

// adapter untuk list My Rental (yang masih Active)
class RentalAdapter(
    private var rentals: ArrayList<Rental>,
    private val onUpdate: (Rental) -> Unit,
    private val onCancel: (Rental) -> Unit
) : RecyclerView.Adapter<RentalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vehicleIV: ImageView = view.findViewById(R.id.vehicleIV)
        val modelNameTV: TextView = view.findViewById(R.id.modelNameTV)
        val brandTV: TextView = view.findViewById(R.id.brandTV)
        val startDateTV: TextView = view.findViewById(R.id.startDateTV)
        val durationTV: TextView = view.findViewById(R.id.durationTV)
        val updateBtn: Button = view.findViewById(R.id.updateBtn)
        val cancelBtn: Button = view.findViewById(R.id.cancelBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rental, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rental = rentals[position]

        holder.vehicleIV.setImageResource(Utils.getVehicleImage(rental.modelName))
        holder.modelNameTV.text = rental.modelName
        holder.brandTV.text = rental.brand
        holder.startDateTV.text = "Start: ${Utils.displayDate(rental.startDate)}"
        holder.durationTV.text = "Duration: ${rental.duration} day(s)"

        holder.updateBtn.setOnClickListener { onUpdate(rental) }
        holder.cancelBtn.setOnClickListener { onCancel(rental) }
    }

    override fun getItemCount(): Int = rentals.size

    fun setData(newList: ArrayList<Rental>) {
        rentals = newList
        notifyDataSetChanged()
    }
}
