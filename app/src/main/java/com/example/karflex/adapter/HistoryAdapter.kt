package com.example.karflex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.R
import com.example.karflex.data.DatabaseHelper
import com.example.karflex.data.model.Rental
import com.example.karflex.utils.Utils

class HistoryAdapter(
    private var rentals: ArrayList<Rental>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vehicleIV: ImageView = view.findViewById(R.id.vehicleIV)
        val modelNameTV: TextView = view.findViewById(R.id.modelNameTV)
        val brandTV: TextView = view.findViewById(R.id.brandTV)
        val startDateTV: TextView = view.findViewById(R.id.startDateTV)
        val durationTV: TextView = view.findViewById(R.id.durationTV)
        val statusTV: TextView = view.findViewById(R.id.statusTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rental = rentals[position]
        val context = holder.itemView.context

        holder.vehicleIV.setImageResource(Utils.getVehicleImage(rental.modelName))
        holder.modelNameTV.text = rental.modelName
        holder.brandTV.text = rental.brand
        holder.startDateTV.text = "Start: ${Utils.displayDate(rental.startDate)}"
        holder.durationTV.text = "Duration: ${rental.duration} day(s)"
        holder.statusTV.text = rental.status

        // Active = hijau, Completed = abu-abu
        if (rental.status == DatabaseHelper.STATUS_ACTIVE) {
            holder.statusTV.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.statusTV.setTextColor(ContextCompat.getColor(context, R.color.gray))
        }
    }

    override fun getItemCount(): Int = rentals.size

    fun setData(newList: ArrayList<Rental>) {
        rentals = newList
        notifyDataSetChanged()
    }
}
