package com.example.karflex.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.karflex.MainActivity
import com.example.karflex.R
import com.example.karflex.data.SessionManager

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val userId = intent.getIntExtra("user_id", -1)
        val phone = intent.getStringExtra("phone") ?: ""
        val otpCode = intent.getStringExtra("otp") ?: ""

        val otpInfoTV = findViewById<TextView>(R.id.otpInfoTV)
        val otpET = findViewById<EditText>(R.id.otpET)
        val verifyBtn = findViewById<Button>(R.id.verifyBtn)

        otpInfoTV.text = "We have sent a 4 digit code to $phone"

        verifyBtn.setOnClickListener {
            val input = otpET.text.toString().trim()

            // OTP must be filled
            if (input.isEmpty()) {
                Toast.makeText(this, "OTP code must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // OTP must be the same as the one sent by SMS
            if (input != otpCode) {
                Toast.makeText(this, "Invalid OTP code!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SessionManager(this).saveLogin(userId)
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

            // clear back stack biar user gak bisa back ke login/otp
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
