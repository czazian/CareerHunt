package com.example.careerhunt

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.careerhunt.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var binding: ActivityMainBinding
    lateinit var sharedIDPreferences: SharedPreferences
    lateinit var sharedUserTypePreferences: SharedPreferences
    lateinit var sharedLangPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedIDPreferences = getSharedPreferences("userid", Context.MODE_PRIVATE)
        sharedUserTypePreferences = getSharedPreferences("userType", Context.MODE_PRIVATE)


        // retrieve the value from LoginPage.kt
        val userType = intent.getStringExtra("user_type")
        val userid = intent.getStringExtra("user_id")
        sharedIDPreferences.edit().putString("userid", userid.toString()).apply()
        Log.e(":", "USER ID FROM MAIN ACTIVITY : $userid.toString()")
        sharedUserTypePreferences.edit().putString("userType", userType.toString()).apply()



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
                    val fragment = InterviewPrep()
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
    }


}