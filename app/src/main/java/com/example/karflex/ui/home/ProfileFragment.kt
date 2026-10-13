package com.example.karflex.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.karflex.R
import com.example.karflex.data.DatabaseHelper
import com.example.karflex.data.SessionManager
import com.example.karflex.ui.auth.LoginActivity
import com.example.karflex.ui.history.RentalHistoryActivity
import com.example.karflex.ui.location.LocationActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        val user = DatabaseHelper(requireContext()).getUserById(session.getUserId())

        val usernameTV = view.findViewById<TextView>(R.id.usernameTV)
        val phoneTV = view.findViewById<TextView>(R.id.phoneTV)
        usernameTV.text = user?.username ?: "-"
        phoneTV.text = user?.phoneNumber ?: "-"

        view.findViewById<Button>(R.id.historyBtn).setOnClickListener {
            startActivity(Intent(requireContext(), RentalHistoryActivity::class.java))
        }

        view.findViewById<Button>(R.id.locationBtn).setOnClickListener {
            startActivity(Intent(requireContext(), LocationActivity::class.java))
        }

        view.findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            session.logout()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
