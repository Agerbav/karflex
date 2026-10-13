package com.example.karflex.data.model

data class Rental(
    val id: Int,
    val vehicleId: Int,
    val userId: Int,
    var duration: Int,
    val startDate: String,
    val status: String,
    // data kendaraan hasil JOIN, buat ditampilin di list
    val modelName: String,
    val brand: String
)
