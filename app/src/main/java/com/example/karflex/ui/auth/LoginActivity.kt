package com.example.karflex.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.karflex.MainActivity
import com.example.karflex.R
import com.example.karflex.data.DatabaseHelper
import com.example.karflex.data.SessionManager
import com.example.karflex.data.model.User
import java.util.Locale
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    // user yang lagi nunggu izin SMS
    private var pendingUser: User? = null

    private val smsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val user = pendingUser
            if (granted && user != null) {
                sendOtp(user)
            } else {
                Toast.makeText(this, "SMS permission is required to send the OTP code", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // kalau sudah pernah login (sudah lewat OTP), langsung ke home
        if (SessionManager(this).isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val usernameET = findViewById<EditText>(R.id.usernameET)
        val passwordET = findViewById<EditText>(R.id.passwordET)

        val btn = findViewById<Button>(R.id.loginBtn)
        val registerBtn = findViewById<TextView>(R.id.registerTV)

        // Validation
        btn.setOnClickListener {
            val username = usernameET.text.toString().trim()
            val password = passwordET.text.toString()

            // Username must be filled
            if (username.isEmpty()) {
                Toast.makeText(this, "Username must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Password must be filled
            if (password.isEmpty()) {
                Toast.makeText(this, "Password must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Username & password must be registered
            val user = db.login(username, password)
            if (user == null) {
                Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // cek permission SMS dulu sebelum kirim OTP
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                sendOtp(user)
            } else {
                pendingUser = user
                smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendOtp(user: User) {
        // 4 digit random, bisa diawali 0 (contoh: 0427)
        val otp = String.format(Locale.US, "%04d", Random.nextInt(0, 10000))
        val message = "Your KarFlex OTP code is $otp. Do not share this code with anyone."

        try {
            val smsManager = getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(user.phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send OTP: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("LoginActivity", "OTP for ${user.username}: $otp")

        Toast.makeText(this, "OTP code has been sent to ${user.phoneNumber}", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra("user_id", user.id)
        intent.putExtra("phone", user.phoneNumber)
        intent.putExtra("otp", otp)
        startActivity(intent)
        pendingUser = null
    }
}
