package com.example.careerhunt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.careerhunt.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



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


                    // retrieve the value from LoginPage.kt
                    val userType = intent.getStringExtra("user_type")

                    if (userType == "Business") {
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


}