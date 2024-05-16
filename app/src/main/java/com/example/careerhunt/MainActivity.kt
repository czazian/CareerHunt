package com.example.careerhunt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.careerhunt.data.Company
import com.example.careerhunt.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var binding: ActivityMainBinding
    lateinit var sharedIDPreferences: SharedPreferences
    lateinit var sharedUserTypePreferences: SharedPreferences
    lateinit var sharedStatusPreference: SharedPreferences
    lateinit var sharedLogoutPreferences: SharedPreferences
    lateinit var sharedLangPreferences: SharedPreferences


    private var userId: String = ""
    private var userType: String = ""
    private var language: String = ""
    private var isFirstLogin: Boolean = true // false = aldy logged in
    private var logoutStatus: Boolean = false // false means not yet logged out

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedIDPreferences = getSharedPreferences("userid", Context.MODE_PRIVATE)
        sharedUserTypePreferences = getSharedPreferences("userType", Context.MODE_PRIVATE)
        sharedLogoutPreferences = getSharedPreferences("logoutStatus", Context.MODE_PRIVATE)
        sharedStatusPreference = getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)
        sharedLangPreferences = getSharedPreferences("language", Context.MODE_PRIVATE)


        // Retrieve SharedPreferences value
        isFirstLogin = sharedStatusPreference.getBoolean("isFirstTime", true)
        logoutStatus = sharedLogoutPreferences.getBoolean("logoutStatus", false)
        userId = sharedIDPreferences.getString("userid", "") ?: ""
        userType = sharedUserTypePreferences.getString("userType", "") ?: ""
        language =
            sharedLangPreferences.getString("language", "") ?: "" // Default language is English


        //TESTING - HARDCODED CREATION OF ONE COMPANY
//        var companyViewModel: CompanyViewModel
//        companyViewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)
//        val byteArray = byteArrayOf(65, 66, 67, 68, 69)
//        val company = Company(0, "company@gmail.com", "Uniqlo", "123", byteArray, "01234567890", "Hello World Street 4, Jalan 19/99.")
//        companyViewModel.addCompany(company)


        //Set initial fragment
        if (savedInstanceState == null) {
            val transaction = fragmentManager.beginTransaction()
            val initialFragment = JobListing()
            transaction.replace(binding.frameLayout.id, initialFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    val transaction = fragmentManager.beginTransaction()
                    val fragment = JobListing()
                    transaction.replace(binding.frameLayout.id, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

                R.id.explore -> {
                    val transaction = fragmentManager.beginTransaction()
                    val fragment = SearchJob()
                    transaction.replace(binding.frameLayout.id, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

                R.id.interview -> {
                    val transaction = fragmentManager.beginTransaction()
                    val fragment = JobDetail()
                    transaction.replace(binding.frameLayout.id, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

                R.id.alumni -> {
                    val transaction = fragmentManager.beginTransaction()
                    val fragment = Alumni()
                    transaction.replace(binding.frameLayout.id, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

                R.id.setting -> {
                    // After a successful login, show the frameLayout and hide the businessProfileLayout
                    val transaction = fragmentManager.beginTransaction()

                    if (userType == "Company") {
                        val fragment = BusinessAccount()

                        transaction.replace(binding.frameLayout.id, fragment)
                    } else if (userType == "Personal") {
                        val fragment = UserProfile()

                        transaction.replace(binding.frameLayout.id, fragment)
                    }
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            true
        }
    }


    //for changing the language of entire app
    fun setLocale() {
        Toast.makeText(this, "Translating lang: " + language, Toast.LENGTH_SHORT).show()

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
    }


}
