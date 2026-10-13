package com.example.karflex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.karflex.data.SessionManager
import com.example.karflex.ui.auth.LoginActivity
import com.example.karflex.ui.home.BrowseFragment
import com.example.karflex.ui.home.MyRentalFragment
import com.example.karflex.ui.home.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

// Home page, isinya 3 section yang diganti lewat bottom navigation
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // jaga-jaga kalau belum login
        if (!SessionManager(this).isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_browse -> loadFragment(BrowseFragment())
                R.id.nav_my_rental -> loadFragment(MyRentalFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }

        // pertama kali buka langsung ke Browse
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_browse
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
