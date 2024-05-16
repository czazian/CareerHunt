package com.example.careerhunt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.careerhunt.databinding.ActivityLoginContainerBinding
import java.util.Locale

//this is the correct and latest one

class LoginContainer : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var binding: ActivityLoginContainerBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedIDPreferences: SharedPreferences
    lateinit var sharedUserTypePreferences: SharedPreferences
    lateinit var sharedStatusPreference: SharedPreferences
    lateinit var sharedLogoutPreferences: SharedPreferences
    private var userId: String = ""
    private var userType: String = ""
    private var isFirstLogin: Boolean = true // false = aldy logged in
    private var logoutStatus: Boolean = false // false means not yet logged out

    override fun onResume() {
        super.onResume()
        // Check login status and navigate to main activity if logged in
        // If the user havent login and the havent logut
        if (!isFirstLogin && userId.isNotEmpty() && logoutStatus) {
           navigateToMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedIDPreferences = getSharedPreferences("userid", Context.MODE_PRIVATE)
        sharedUserTypePreferences = getSharedPreferences("userType", Context.MODE_PRIVATE)
        sharedLogoutPreferences = getSharedPreferences("logoutStatus", Context.MODE_PRIVATE)
        sharedStatusPreference = getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)
        sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE)

        // Retrieve SharedPreferences value
        isFirstLogin = sharedStatusPreference.getBoolean("isFirstTime", true)
        logoutStatus = sharedLogoutPreferences.getBoolean("logoutStatus", false)
        userId = sharedIDPreferences.getString("userid", "") ?: ""
        userType = sharedUserTypePreferences.getString("userType", "") ?: ""
        val isDarkMode = sharedPreferences.getBoolean("night", false)

      // Apply dark mode if enabled
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        if (!isFirstLogin && userId.isNotEmpty() && logoutStatus) {
            Toast.makeText(this, "laoding to main activity", Toast.LENGTH_SHORT).show()
            navigateToMainActivity()

        }else{
            val transaction = fragmentManager.beginTransaction()
            val initialFragment = LoginScreen()
            transaction.replace(binding.loginFrameLayout.id, initialFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }


    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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

/*
    //for changing the language of entire app
    fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        restartActivity()
    }

    fun restartActivity() {

        val intent = intent
        finish()
        startActivity(intent)
    }*/
}

