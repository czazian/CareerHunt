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
import com.example.careerhunt.session.loginSession
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var binding: ActivityMainBinding
    lateinit var sharedIDPreferences: SharedPreferences
    lateinit var sharedUserTypePreferences: SharedPreferences
    lateinit var sharedStatusPreference: SharedPreferences
    lateinit var sharedLogoutPreferences: SharedPreferences


    lateinit var loginSession: loginSession
    private var userId: String = ""
    private var userType: String = ""
    private var isFirstLogin: Boolean = true // false = aldy logged in
    private var logoutStatus: Boolean = false // false means not yet logged out

    override fun onResume() {
        super.onResume()
        // Check login status and navigate to main activity if logged in
        if (!isFirstLogin && userId.isNotEmpty() && logoutStatus) {
            val transaction = fragmentManager.beginTransaction()
            val initialFragment = JobListing()
            transaction.replace(binding.frameLayout.id, initialFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init()
        //checkLogin()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedIDPreferences = getSharedPreferences("userid", Context.MODE_PRIVATE)
        sharedUserTypePreferences = getSharedPreferences("userType", Context.MODE_PRIVATE)
        sharedLogoutPreferences = getSharedPreferences("logoutStatus", Context.MODE_PRIVATE)

        // sharedStatusPreference = getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)
        // Retrieve login status from SharedPreferences
        sharedStatusPreference = getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)
        isFirstLogin = sharedStatusPreference.getBoolean("isFirstTime", true)
        logoutStatus = sharedLogoutPreferences.getBoolean("logoutStatus", false)


        /*  // Retrieve user ID and type from SharedPreferences
          val userType = intent.getStringExtra("user_type")
          val userid = intent.getStringExtra("user_id")
          //val firstLogin = intent.getBooleanExtra("firstLogin",true)// true = not login before*/
        userId = sharedIDPreferences.getString("userid", "") ?: ""
        userType = sharedUserTypePreferences.getString("userType", "") ?: ""


      //  userid = sharedIDPreferences.getString("user_id", "").toString()
        //userType = sharedUserTypePreferences.getString("userType", "").toString()
        //val logoutStatus = intent.getBooleanExtra("logout", false) // false = aldy logged out


       // sharedIDPreferences.edit().putString("user_id", userid.toString()).apply()
        //sharedUserTypePreferences.edit().putString("userType", userType.toString()).apply()
        //sharedStatusPreference.edit().putBoolean("isFirstTime", firstLogin).apply()

        //   id = sharedIDPreferences.getString("userid", "") ?: ""
        // type = sharedUserTypePreferences.getString("userType", "") ?: ""
        //  isFirstTime = sharedStatusPreference.getBoolean("isFirstTime", true)
        Toast.makeText(this, "loginStatus: $isFirstLogin", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "USERID haha: $userId", Toast.LENGTH_SHORT).show()
        //val logoutStatus = intent.getBooleanExtra("logout", false) // false means havent logout
        Toast.makeText(this, "logout haha: $logoutStatus", Toast.LENGTH_SHORT).show()

        // Check if it's not the user's first time and the user is not logging out
        if (!isFirstLogin && userId.isNotEmpty() && logoutStatus) {
            Toast.makeText(this, "Suppose in main: $userId", Toast.LENGTH_SHORT).show()

            val transaction = fragmentManager.beginTransaction()
            val initialFragment = JobListing()
            transaction.replace(binding.frameLayout.id, initialFragment)
            transaction.addToBackStack(null)
            transaction.commit()

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
                        val fragment = Setting()
                        transaction.replace(binding.frameLayout.id, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }

                    R.id.setting -> {
                        // After a successful login, show the frameLayout and hide the businessProfileLayout
                        val transaction = fragmentManager.beginTransaction()


                        // retrieve the value from LoginPage.kt (mean they successfully logged in)
                        /*  val userType = intent.getStringExtra("user_type")
                          val userid = intent.getStringExtra("user_id")
                          sharedIDPreferences.edit().putString("userid", userid.toString()).apply()
                          sharedUserTypePreferences.edit().putString("userType", userType.toString()).apply()
                          sharedStatusPreference.edit().putBoolean("isFirstTime",true).apply()*/
                        Toast.makeText(this, "userTypoe " + userType, Toast.LENGTH_SHORT).show()

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


        } else {
            // If it's the first time or the user is logging out, navigate to the LoginContainer
            val intent = Intent(this, LoginContainer::class.java)
            startActivity(intent)
            finish()
        }
    }

        // Retrieve dark mode state from SharedPreferences
        /*sharedLangPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE)
        val isDarkMode = sharedLangPreferences.getBoolean("night", false)*/




        //TESTING - HARDCODED CREATION OF ONE COMPANY
//        var companyViewModel: CompanyViewModel
//        companyViewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)
//        val byteArray = byteArrayOf(65, 66, 67, 68, 69)
//        val company = Company(0, "company@gmail.com", "Uniqlo", "123", byteArray, "01234567890", "Hello World Street 4, Jalan 19/99.")
//        companyViewModel.addCompany(company)



/*
        //Set initial fragment
        if (savedInstanceState == null) {
            val transaction = fragmentManager.beginTransaction()
            val initialFragment = JobListing()
            transaction.replace(binding.frameLayout.id, initialFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }*/





    private fun logout() {
        // Clear login status on logout
       // loginSession.setLogin(false)
        loginSession.removeData()

        // Navigate back to login screen
        val intent = Intent(this, LoginContainer::class.java)
        startActivity(intent)
        finish() // Finish the current activity to prevent user from returning to it using back button
    }

   /* private fun init(){
        loginSession = loginSession(this)
        id = loginSession.getID().toString()
        type = loginSession.getType().toString()
    }

    private fun checkLogin(){
        if(loginSession.isLogin()==false){
            val intent = Intent(this,LoginContainer::class.java)
            startActivity(intent)
            finish()
        }
    }*/


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
