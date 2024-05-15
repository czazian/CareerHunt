package com.example.careerhunt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentBusinessAccountBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BusinessAccount : Fragment() {
    private lateinit var binding: FragmentBusinessAccountBinding
    private lateinit var sharedIDPreferences: SharedPreferences
    private lateinit var myRef: DatabaseReference
    lateinit var sharedUserTypePreferences: SharedPreferences
    lateinit var sharedStatusPreference: SharedPreferences
    lateinit var sharedLogoutPreferences: SharedPreferences


    //private var database =
        //FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/")

    //private var myRef = database.reference
    private var userId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBusinessAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        // Access SharedPreferences from MainActivity
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        // Retrieve userId from SharedPreferences
        userId = sharedIDPreferences.getString("userid","")?:""
        retrieveCompRecord()

        sharedLogoutPreferences = requireContext().getSharedPreferences("logoutStatus", Context.MODE_PRIVATE)
        sharedUserTypePreferences = requireContext().getSharedPreferences("userType", Context.MODE_PRIVATE)
        sharedStatusPreference = requireContext().getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)

        binding.btnFAQ.setOnClickListener() {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = faq()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.btnEditProfile.setOnClickListener() {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = EditBusinessAcc()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.btnJobListing.setOnClickListener() {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = ViewPublishedJob()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            Toast.makeText(requireContext(), "View Published Job", Toast.LENGTH_SHORT).show()
        }

        // For language change
        binding.spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var userSelected = false // Flag to track user selection

            // Inside the onItemSelected method of the spinner's OnItemSelectedListener
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (userSelected) {
                    val language: String = parent.getItemAtPosition(position).toString()

                    if (language == "Chinese") {
                        // Set locale to Chinese
                        (requireActivity() as MainActivity).setLocale()// based on the string value that created
                    } else {
                        // Set locale to English
                        (requireActivity() as MainActivity).setLocale()
                    }
                }
                userSelected = true // Set flag to true after user selection
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "No language selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout
        binding.btnLogout.setOnClickListener() {
            val intent = Intent(requireContext(), LoginContainer::class.java)
            sharedIDPreferences.edit().clear().apply()
            sharedUserTypePreferences.edit().clear().apply()
            sharedStatusPreference.edit().putBoolean("isFirstTime",true).apply()

            sharedLogoutPreferences.edit().putBoolean("logoutStatus",true).apply()
            startActivity(intent)
            requireActivity().finish() // this is to prevent user return back to login page
        }

        binding.swMode.setOnCheckedChangeListener() { buttonView, isChecked ->
            // isChecked will be true if the switch is turned on, false otherwise
            if (isChecked) {
                changeDarkMode()
            } else {
                changeDayMode()
            }
        }
        return view
    }

    private fun changeDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        Toast.makeText(requireContext(), "Change Dark Mode", Toast.LENGTH_SHORT).show()
    }

    private fun changeDayMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Toast.makeText(requireContext(), "Change Day Mode", Toast.LENGTH_SHORT).show()
    }

    private fun retrieveCompRecord() {
        val tvCompName: TextView = binding.tvCompName
        val tvCompEmail: TextView = binding.tvCompEmail
        val profileImg : ImageView = binding.profilePic

        myRef = FirebaseDatabase.getInstance().getReference("Company")

        myRef.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val comp = snapshot.getValue(Company::class.java)
                        if (comp != null) {
                            tvCompName.text = comp.compName
                            tvCompEmail.text = comp.email
                            // Load profile image
                            if(comp.compProfile!= ""){
                                val profileImageUrl = comp.compProfile
                                Glide.with(requireContext()).load(profileImageUrl).into(profileImg)
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "User with ID $userId not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

}