package com.example.careerhunt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
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
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentUserProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class UserProfile : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedIDPreferences: SharedPreferences
    private lateinit var sharedLangPreferences: SharedPreferences



    private var database =
        FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private var myRef = database.reference
    private var userId: String = ""


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Access SharedPreferences from MainActivity
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        // Retrieve userId from SharedPreferences
        userId = sharedIDPreferences.getString("userid","")?:""
        retrieveUserRecord()

        // Retrieve saved language preference from SharedPreferences
        sharedLangPreferences = requireContext().getSharedPreferences("language", Context.MODE_PRIVATE)
        val savedLanguage = sharedLangPreferences.getString("language", "en") // Default language is English

        // Set the spinner selection based on the saved language
        val languageArray = resources.getStringArray(R.array.language)
        val languageIndex = languageArray.indexOf(savedLanguage)
        if (languageIndex != -1) {
            binding.spLanguage.setSelection(languageIndex) // Set the selected language in the spinner
        }


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
            val newFragment = EditPersonalAcc()

            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.btnApplied.setOnClickListener() {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = ViewAppliedJob()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        // Retrieve saved language preference from SharedPreferences
        /*  sharedLangPreferences = requireContext().getSharedPreferences("language", Context.MODE_PRIVATE)
          val savedLanguage = sharedLangPreferences.getString("language", "en")// the default language is english


          // Set the spinner selection based on the saved language
          val languageArray = resources.getStringArray(R.array.language)
          val languageIndex = languageArray.indexOf(savedLanguage)
          if (languageIndex != -1) {
              binding.spLanguage.setSelection(languageIndex)// it will remain on the selected language
          }*/


        // For language change
        binding.spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var userSelected = false // Flag to track user selection

            // Inside the onItemSelected method of the spinner's OnItemSelectedListener
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (userSelected) {
                    val language: String = parent.getItemAtPosition(position).toString()

                    if (language == "Chinese") {
                        // Set locale to Chinese

                        //(requireActivity() as MainActivity).setLocale("ch")// based on the string value that created
                    } else {
                        // Set locale to English
                       // (requireActivity() as MainActivity).setLocale("en")
                    }

                    // Save selected language preference
                    sharedLangPreferences.edit().putString("language", language).apply()

                }
                userSelected = true // Set flag to true after user selection
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "No language selected", Toast.LENGTH_SHORT).show()
            }
        }



        binding.btnLogout.setOnClickListener() {
            val intent = Intent(requireContext(), LoginContainer::class.java)

            startActivity(intent)
            requireActivity().finish() // this is to prevent user return back to profile page
        }

        //For dark mode
        sharedPreferences = requireContext().getSharedPreferences("Mode", Context.MODE_PRIVATE)
        val nightMode = sharedPreferences.getBoolean("night", false) // false = day mode

        // means it is true = make it become night mode
        if (nightMode) {
            binding.swMode.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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
        saveDarkModeState(true) // Save dark mode state

    }

    private fun changeDayMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Toast.makeText(requireContext(), "Change Day Mode", Toast.LENGTH_SHORT).show()
        saveDarkModeState(false) // Save dark mode state
    }

    private fun saveDarkModeState(isDarkMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("night", isDarkMode)
        editor.apply()
    }

    private fun retrieveUserRecord() {
        Toast.makeText(requireContext(), "Userid : " + userId, Toast.LENGTH_SHORT).show()
        val tvName: TextView = binding.tvName
        val tvEmail: TextView = binding.tvEmail
        val profileImg : ImageView = binding.profilePic


        myRef.child("Personal").child(userId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(Personal::class.java)
                        if (user != null) {
                            Toast.makeText(
                                requireContext(),
                                "id real : " + userId.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                            tvName.text = user.name
                            tvEmail.text = user.email

                            // Load profile image
                            if(user.profileImg!= ""){
                                val profileImageUrl = user.profileImg
                                Glide.with(requireContext()).load(profileImageUrl).into(profileImg)
                            }
                        }
                    } else {
                        // if the user id not found in Firebase
                        Toast.makeText(
                            requireContext(),
                            "User with ID $userId not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /*private fun changeLanguage(language: String) {
      val locale = Locale(language)
      Locale.setDefault(locale)
      val config = Configuration(resources.configuration)
      config.setLocale(locale)
      resources.updateConfiguration(config, resources.displayMetrics)

      // Save selected language preference
      sharedLangPreferences.edit().putString("language", language).apply()

      // Recreate activity to apply changes
      requireActivity().recreate()
    }*/

}