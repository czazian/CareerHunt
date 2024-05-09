package com.example.careerhunt

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.careerhunt.databinding.ActivityLoginContainerBinding

class LoginContainer : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var binding: ActivityLoginContainerBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve dark mode state from SharedPreferences
        sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("night", false)

        // Apply dark mode if enabled
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //Set initial fragment
        if (savedInstanceState == null) {
            val transaction = fragmentManager.beginTransaction()
            val initialFragment = LoginScreen()
            transaction.replace(binding.loginFrameLayout.id, initialFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    // After click the Register Account Hyperlink
    fun onRegAccClicked(view: View) {
        // Show the pop out for choosing what type of account to register
        val dialogView = LayoutInflater.from(this).inflate(R.layout.register_popout, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnBusiness)?.setOnClickListener {
            // Handle click for Business Account
            Toast.makeText(this, "Register Business Account", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

            // Navigate to register for Business Account
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = RegisterBusiness()
            transaction.replace(R.id.loginFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        dialogView.findViewById<Button>(R.id.btnPersonal)?.setOnClickListener {
            // Handle click for Personal Account
            Toast.makeText(this, "Register Personal Account", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

            // Navigate to RegisterAccount fragment
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = RegisterAccount()
            transaction.replace(R.id.loginFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        dialog.show()
    }

    fun onForgetPasswdClicked(view: View) {
        // Navigate to the forget password fragment
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = ForgetPassword()
        transaction.replace(R.id.loginFrameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


        Toast.makeText(this, "Reset your Password", Toast.LENGTH_SHORT).show()
    }
}