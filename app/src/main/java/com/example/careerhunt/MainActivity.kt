package com.example.careerhunt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.careerhunt.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set initial fragment
        if (savedInstanceState == null) {
            val transaction = fragmentManager.beginTransaction()
            val initialFragment = JobListing()
            transaction.replace(binding.frameLayout.id, initialFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId){
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
                    val fragment = JobListing()
                    transaction.replace(binding.frameLayout.id, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                R.id.setting -> {
                    val transaction = fragmentManager.beginTransaction()
                    val fragment = Setting()
                    transaction.replace(binding.frameLayout.id, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            true
        }
    }
}