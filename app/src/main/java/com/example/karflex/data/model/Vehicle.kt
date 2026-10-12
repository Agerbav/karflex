package com.example.karflex.data.model

data class Vehicle (
    val id: Int,
    val modelName: String,
    val brand: String,
    val pricePerDay: Int,
    val transmission: String,
    val imageUrl: String,
    val description: String
)

