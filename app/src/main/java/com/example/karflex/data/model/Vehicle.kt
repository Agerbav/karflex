package com.example.karflex.data.model

data class Vehicle (
    val id: Int,
    val modelName: String,
    val type: String,
    val brand: String,
    val pricePerDay: Int,
    val transmission: String,
    val image: String,
    val description: String,
    // bukan kolom di tabel, diisi dari cek tabel Rentals
    var isRented: Boolean = false
)
