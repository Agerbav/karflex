package com.example.karflex.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.karflex.R
import com.example.karflex.data.DatabaseHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = DatabaseHelper(this)

        val usernameET = findViewById<EditText>(R.id.usernameET)
        val passwordET = findViewById<EditText>(R.id.passwordET)
        val phoneET = findViewById<EditText>(R.id.phoneET)

        //validation
        val btn = findViewById<Button>(R.id.registerBtn)
        btn.setOnClickListener {
            val username = usernameET.text.toString().trim()
            val password = passwordET.text.toString()
            val phone = phoneET.text.toString().trim()

            // All fields must be filled
            if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Username must be unique
            if (db.isUsernameExists(username)) {
                Toast.makeText(this, "Username is already taken!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Password at least 6 characters
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Phone number only digits and at least 10 digits
            if (!phone.all { it.isDigit() }) {
                Toast.makeText(this, "Phone number must contain only digits!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phone.length < 10) {
                Toast.makeText(this, "Phone number must be at least 10 digits!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.insertUser(username, password, phone)) {
                Toast.makeText(this, "Register Successful, please login", Toast.LENGTH_SHORT).show()
                // balik ke login page (login activity masih ada di back stack)
                finish()
            } else {
                Toast.makeText(this, "Register failed, please try again", Toast.LENGTH_SHORT).show()
            }
        }

        //redirect to login
        val loginBtn = findViewById<TextView>(R.id.loginTV)
        loginBtn.setOnClickListener {
            finish()
        }
    }
}
