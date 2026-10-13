package com.example.karflex.utils

import com.example.karflex.R
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Utils {

    // format yang disimpan di database (StartDate)
    const val DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    fun formatRupiah(price: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        format.maximumFractionDigits = 0
        return format.format(price)
    }

    fun now(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DB_DATE_FORMAT, Locale.US))
    }

    fun parseDate(date: String): LocalDateTime {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DB_DATE_FORMAT, Locale.US))
    }

    // contoh: 13 Oct 2026
    fun displayDate(date: String): String {
        return try {
            parseDate(date).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US))
        } catch (e: Exception) {
            date
        }
    }

    // gambar dari URL JSON ada yang sudah tidak bisa diakses,
    // jadi pakai gambar lokal di drawable sesuai nama kendaraannya
    fun getVehicleImage(modelName: String): Int {
        val name = modelName.lowercase()
        return when {
            name.contains("vario") -> R.drawable.honda
            name.contains("nmax") -> R.drawable.yamaha
            name.contains("avanza") -> R.drawable.avanza
            name.contains("brio") -> R.drawable.brio
            else -> R.drawable.ic_vehicle
        }
    }
}
