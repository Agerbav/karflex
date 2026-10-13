package com.example.karflex.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.R
import com.example.karflex.data.model.Vehicle
import com.example.karflex.utils.Utils
import com.google.android.material.card.MaterialCardView

class VehicleAdapter(
    private var vehicles: ArrayList<Vehicle>,
    private val onClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vehicleCard: MaterialCardView = view.findViewById(R.id.vehicleCard)
        val vehicleIV: ImageView = view.findViewById(R.id.vehicleIV)
        val modelNameTV: TextView = view.findViewById(R.id.modelNameTV)
        val brandTV: TextView = view.findViewById(R.id.brandTV)
        val transmissionTV: TextView = view.findViewById(R.id.transmissionTV)
        val priceTV: TextView = view.findViewById(R.id.priceTV)
        val rentedTV: TextView = view.findViewById(R.id.rentedTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = vehicles[position]
        val context = holder.itemView.context

        holder.vehicleIV.setImageResource(Utils.getVehicleImage(vehicle.modelName))
        holder.modelNameTV.text = vehicle.modelName
        holder.brandTV.text = vehicle.brand
        holder.transmissionTV.text = vehicle.transmission
        holder.priceTV.text = "${Utils.formatRupiah(vehicle.pricePerDay)} / day"

        // kalau lagi disewa, card jadi merah
        if (vehicle.isRented) {
            holder.vehicleCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red))
            setTextColor(holder, Color.WHITE)
            holder.rentedTV.visibility = View.VISIBLE
        } else {
            holder.vehicleCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            setTextColor(holder, ContextCompat.getColor(context, R.color.dark))
            holder.rentedTV.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onClick(vehicle)
        }
    }

    private fun setTextColor(holder: ViewHolder, color: Int) {
        holder.modelNameTV.setTextColor(color)
        holder.brandTV.setTextColor(color)
        holder.transmissionTV.setTextColor(color)
        holder.priceTV.setTextColor(color)
        holder.rentedTV.setTextColor(color)
    }

    override fun getItemCount(): Int = vehicles.size

    fun setData(newList: ArrayList<Vehicle>) {
        vehicles = newList
        notifyDataSetChanged()
    }
}
