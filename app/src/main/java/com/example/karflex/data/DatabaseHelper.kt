package com.example.karflex.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.karflex.data.model.Rental
import com.example.karflex.data.model.User
import com.example.karflex.data.model.Vehicle
import com.example.karflex.utils.Utils
import java.time.LocalDateTime

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "KarFlex.db"
        const val DB_VERSION = 1

        const val TABLE_USERS = "Users"
        const val TABLE_VEHICLE = "Vehicle"
        const val TABLE_RENTALS = "Rentals"

        const val STATUS_ACTIVE = "Active"
        const val STATUS_COMPLETED = "Completed"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // AUTOINCREMENT supaya UserID selalu naik dan tidak dipakai ulang walaupun ada data yang dihapus
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                UserID INTEGER PRIMARY KEY AUTOINCREMENT,
                Username TEXT NOT NULL UNIQUE,
                Password TEXT NOT NULL,
                PhoneNumber TEXT NOT NULL
            )
            """.trimIndent()
        )

        // VehicleID tidak autoincrement karena id-nya ngikut dari data JSON
        db.execSQL(
            """
            CREATE TABLE $TABLE_VEHICLE (
                VehicleID INTEGER PRIMARY KEY,
                ModelName TEXT,
                Type TEXT,
                Brand TEXT,
                PricePerDay INTEGER,
                Transmission TEXT,
                Image TEXT,
                Description TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_RENTALS (
                RentalID INTEGER PRIMARY KEY AUTOINCREMENT,
                VehicleID INTEGER NOT NULL,
                UserID INTEGER NOT NULL,
                Duration INTEGER NOT NULL,
                StartDate TEXT NOT NULL,
                Status TEXT NOT NULL,
                FOREIGN KEY (VehicleID) REFERENCES $TABLE_VEHICLE(VehicleID),
                FOREIGN KEY (UserID) REFERENCES $TABLE_USERS(UserID)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RENTALS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VEHICLE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // ===================== USERS =====================

    fun insertUser(username: String, password: String, phoneNumber: String): Boolean {
        val values = ContentValues()
        values.put("Username", username)
        values.put("Password", password)
        values.put("PhoneNumber", phoneNumber)

        val result = writableDatabase.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun isUsernameExists(username: String): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT UserID FROM $TABLE_USERS WHERE Username = ?",
            arrayOf(username)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun login(username: String, password: String): User? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE Username = ? AND Password = ?",
            arrayOf(username, password)
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                cursor.getString(cursor.getColumnIndexOrThrow("Username")),
                cursor.getString(cursor.getColumnIndexOrThrow("Password")),
                cursor.getString(cursor.getColumnIndexOrThrow("PhoneNumber"))
            )
        }
        cursor.close()
        return user
    }

    fun getUserById(userId: Int): User? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE UserID = ?",
            arrayOf(userId.toString())
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                cursor.getString(cursor.getColumnIndexOrThrow("Username")),
                cursor.getString(cursor.getColumnIndexOrThrow("Password")),
                cursor.getString(cursor.getColumnIndexOrThrow("PhoneNumber"))
            )
        }
        cursor.close()
        return user
    }

    // ===================== VEHICLE =====================

    // data dari JSON disimpan ke database. Kalau id sudah ada cukup di-update,
    // gak pakai REPLACE karena REPLACE = delete + insert dan bisa bentrok sama foreign key di Rentals
    fun saveVehicle(vehicle: Vehicle) {
        val values = ContentValues()
        values.put("ModelName", vehicle.modelName)
        values.put("Type", vehicle.type)
        values.put("Brand", vehicle.brand)
        values.put("PricePerDay", vehicle.pricePerDay)
        values.put("Transmission", vehicle.transmission)
        values.put("Image", vehicle.image)
        values.put("Description", vehicle.description)

        val db = writableDatabase
        val updated = db.update(TABLE_VEHICLE, values, "VehicleID = ?", arrayOf(vehicle.id.toString()))
        if (updated == 0) {
            values.put("VehicleID", vehicle.id)
            db.insert(TABLE_VEHICLE, null, values)
        }
    }

    private fun cursorToVehicle(cursor: Cursor): Vehicle {
        return Vehicle(
            cursor.getInt(cursor.getColumnIndexOrThrow("VehicleID")),
            cursor.getString(cursor.getColumnIndexOrThrow("ModelName")),
            cursor.getString(cursor.getColumnIndexOrThrow("Type")),
            cursor.getString(cursor.getColumnIndexOrThrow("Brand")),
            cursor.getInt(cursor.getColumnIndexOrThrow("PricePerDay")),
            cursor.getString(cursor.getColumnIndexOrThrow("Transmission")),
            cursor.getString(cursor.getColumnIndexOrThrow("Image")),
            cursor.getString(cursor.getColumnIndexOrThrow("Description"))
        )
    }

    fun getAllVehicles(): ArrayList<Vehicle> {
        updateRentalStatus()

        val list = ArrayList<Vehicle>()
        val rentedIds = getRentedVehicleIds()
        val cursor = readableDatabase.rawQuery("SELECT * FROM $TABLE_VEHICLE ORDER BY VehicleID", null)
        while (cursor.moveToNext()) {
            val vehicle = cursorToVehicle(cursor)
            vehicle.isRented = rentedIds.contains(vehicle.id)
            list.add(vehicle)
        }
        cursor.close()
        return list
    }

    fun getVehicleById(vehicleId: Int): Vehicle? {
        updateRentalStatus()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_VEHICLE WHERE VehicleID = ?",
            arrayOf(vehicleId.toString())
        )
        var vehicle: Vehicle? = null
        if (cursor.moveToFirst()) {
            vehicle = cursorToVehicle(cursor)
            vehicle.isRented = getRentedVehicleIds().contains(vehicle.id)
        }
        cursor.close()
        return vehicle
    }

    // kendaraan dianggap sedang disewa kalau ada rental yang statusnya masih Active (dari user manapun)
    private fun getRentedVehicleIds(): HashSet<Int> {
        val ids = HashSet<Int>()
        val cursor = readableDatabase.rawQuery(
            "SELECT DISTINCT VehicleID FROM $TABLE_RENTALS WHERE Status = ?",
            arrayOf(STATUS_ACTIVE)
        )
        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(0))
        }
        cursor.close()
        return ids
    }

    // ===================== RENTALS =====================

    fun insertRental(vehicleId: Int, userId: Int, duration: Int, startDate: String): Boolean {
        val values = ContentValues()
        values.put("VehicleID", vehicleId)
        values.put("UserID", userId)
        values.put("Duration", duration)
        values.put("StartDate", startDate)
        values.put("Status", STATUS_ACTIVE)

        val result = writableDatabase.insert(TABLE_RENTALS, null, values)
        return result != -1L
    }

    private fun getRentals(where: String, args: Array<String>): ArrayList<Rental> {
        val list = ArrayList<Rental>()
        val cursor = readableDatabase.rawQuery(
            """
            SELECT r.*, v.ModelName, v.Brand
            FROM $TABLE_RENTALS r
            JOIN $TABLE_VEHICLE v ON r.VehicleID = v.VehicleID
            WHERE $where
            ORDER BY r.StartDate DESC, r.RentalID DESC
            """.trimIndent(),
            args
        )
        while (cursor.moveToNext()) {
            list.add(
                Rental(
                    cursor.getInt(cursor.getColumnIndexOrThrow("RentalID")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("VehicleID")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("Duration")),
                    cursor.getString(cursor.getColumnIndexOrThrow("StartDate")),
                    cursor.getString(cursor.getColumnIndexOrThrow("Status")),
                    cursor.getString(cursor.getColumnIndexOrThrow("ModelName")),
                    cursor.getString(cursor.getColumnIndexOrThrow("Brand"))
                )
            )
        }
        cursor.close()
        return list
    }

    fun getActiveRentals(userId: Int): ArrayList<Rental> {
        updateRentalStatus()
        return getRentals("r.UserID = ? AND r.Status = ?", arrayOf(userId.toString(), STATUS_ACTIVE))
    }

    // semua rental milik user (active + completed), urut dari yang terbaru
    fun getRentalHistory(userId: Int, keyword: String): ArrayList<Rental> {
        updateRentalStatus()
        return getRentals("r.UserID = ? AND v.ModelName LIKE ?", arrayOf(userId.toString(), "%$keyword%"))
    }

    fun updateRentalDuration(rentalId: Int, duration: Int): Boolean {
        val values = ContentValues()
        values.put("Duration", duration)
        val result = writableDatabase.update(TABLE_RENTALS, values, "RentalID = ?", arrayOf(rentalId.toString()))
        return result > 0
    }

    fun deleteRental(rentalId: Int): Boolean {
        val result = writableDatabase.delete(TABLE_RENTALS, "RentalID = ?", arrayOf(rentalId.toString()))
        return result > 0
    }

    // ubah status Active -> Completed kalau waktu sekarang sudah lewat dari StartDate + Duration (hari)
    fun updateRentalStatus() {
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT RentalID, Duration, StartDate FROM $TABLE_RENTALS WHERE Status = ?",
            arrayOf(STATUS_ACTIVE)
        )
        val now = LocalDateTime.now()
        val completedIds = ArrayList<Int>()
        while (cursor.moveToNext()) {
            val rentalId = cursor.getInt(0)
            val duration = cursor.getInt(1)
            val startDate = cursor.getString(2)
            try {
                val endDate = Utils.parseDate(startDate).plusDays(duration.toLong())
                if (now.isAfter(endDate)) {
                    completedIds.add(rentalId)
                }
            } catch (e: Exception) {
                // format tanggal gak valid, dilewati aja
            }
        }
        cursor.close()

        for (id in completedIds) {
            val values = ContentValues()
            values.put("Status", STATUS_COMPLETED)
            db.update(TABLE_RENTALS, values, "RentalID = ?", arrayOf(id.toString()))
        }
    }
}
