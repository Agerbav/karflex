package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.usernameET)
        val password = findViewById<EditText>(R.id.passwordET)
        val btn = findViewById<Button>(R.id.loginBtn)
        val registerBtn = findViewById<TextView>(R.id.registerTV)

        // Validation
        btn.setOnClickListener {
            // Username must be filled
            if(username.text.isEmpty()) {
                Toast.makeText(this, "Username Must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Password must be filled
            if(password.text.isEmpty()) {
                Toast.makeText(this, "Password Must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            };

            // TODO: Registered in database

            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }



    }
}