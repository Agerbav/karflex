package com.example.karflex.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.karflex.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameET = findViewById<EditText>(R.id.usernameET)
        val passwordET = findViewById<EditText>(R.id.passwordET)
        val confirmPasswordET = findViewById<EditText>(R.id.confirmPasswordET)

        //validation
        val btn = findViewById<Button>(R.id.registerBtn)
        btn.setOnClickListener {
            val username = usernameET.text.toString().trim()
            val password = passwordET.text.toString().trim()
            val confirmPassword = confirmPasswordET.text.toString().trim()

            // Username must be filled
            if(username.isEmpty()) {
                Toast.makeText(this, "Username Must not be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Password must be filled
            if(password.isEmpty()) {
                Toast.makeText(this, "Password Must not be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            };

            // Password must match
            if(password != confirmPassword) {
                Toast.makeText(this, "Password must match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Registered in database

            Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show()

        }

        //redirect to login
        val loginBtn = findViewById<TextView>(R.id.loginTV)
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}