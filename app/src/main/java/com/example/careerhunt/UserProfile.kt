package com.example.careerhunt

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentUserProfileBinding
import com.example.careerhunt.interfaces.LocaleChangeListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class UserProfile : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedIDPreferences: SharedPreferences
    private lateinit var sharedLangPreferences: SharedPreferences
    lateinit var sharedUserTypePreferences: SharedPreferences
    lateinit var sharedStatusPreference: SharedPreferences
    private lateinit var myRef: DatabaseReference
    private var userId: String = ""
    private var languageSelected: String=""
    private var isLanguageSelectionByUser = false // Set initially to true
    lateinit var sharedLogoutPreferences: SharedPreferences

  /*  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveUserRecord()
    }*/

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize SharedPreferences
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        sharedUserTypePreferences = requireContext().getSharedPreferences("userType", Context.MODE_PRIVATE)
        sharedStatusPreference = requireContext().getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)
        sharedLogoutPreferences = requireContext().getSharedPreferences("logoutStatus", Context.MODE_PRIVATE)
        sharedLangPreferences = requireContext().getSharedPreferences("language", Context.MODE_PRIVATE)

        // Retrieve userId from SharedPreferences
        userId = sharedIDPreferences.getString("userid", "") ?: ""

        // Retrieve saved language preference from SharedPreferences
        languageSelected = sharedLangPreferences.getString("language", "") ?: ""
        Toast.makeText(requireContext(), "languageSelected: $languageSelected", Toast.LENGTH_SHORT).show()

        retrieveUserRecord()

        // Initialize Spinner
        val spLangType : Spinner = binding.spLanguage
        val langArray = resources.getStringArray(R.array.language)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, langArray)
        spLangType.adapter = adapter

        // Set the spinner selection based on the saved language
        val languageIndex = langArray.indexOf(languageSelected)
        if (languageIndex != -1) {
            spLangType.setSelection(languageIndex)
        }

        /*
        // Language Selection Listener
        spLangType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val language: String = parent.getItemAtPosition(position).toString()
                if (isLanguageSelectionByUser) {
                    // Set locale based on language selection
                    when (language) {
                        "Chinese" -> {
                            Toast.makeText(requireContext(), "Chinese", Toast.LENGTH_SHORT).show()
                            setLocale("ch")
                        }
                        "English" -> {
                            Toast.makeText(requireContext(), "English", Toast.LENGTH_SHORT).show()
                            setLocale("en")
                        }
                    }
                    // Update SharedPreferences with selected language
                    sharedLangPreferences.edit().putString("language", language).apply()
                }
                // Reset the flag after user selection
                isLanguageSelectionByUser = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }*/

        /*Latest
        // Remain selected language
         binding.spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
             override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                 val language: String = parent.getItemAtPosition(position).toString()

                 /*when (language) {
                     "Chinese" -> {
                         Toast.makeText(requireContext(), "Chinese", Toast.LENGTH_SHORT).show()
                         setLocale("ch")
                     }

                     "English" -> {
                         Toast.makeText(requireContext(), "English", Toast.LENGTH_SHORT).show()
                         setLocale("en")
                     }
                 }*/

            if (language == "Chinese") {
                     Toast.makeText(requireContext(), "Chinese", Toast.LENGTH_SHORT).show()

                     // Set locale to Chinese
                    // (requireActivity() as LoginContainer).setLocale("ch")
                 } else if(language=="English") {
                     Toast.makeText(requireContext(), "English", Toast.LENGTH_SHORT).show()

                     // Set locale to English
                    // (requireActivity() as LoginContainer).setLocale("en")
                 }
                 // Save selected language preference
                 sharedLangPreferences.edit().putString("language", language).apply()
             }

             override fun onNothingSelected(parent: AdapterView<*>?) {
                 Toast.makeText(requireContext(), "No language selected", Toast.LENGTH_SHORT).show()
             }
         }
*/
/*
        binding.spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val language: String = parent.getItemAtPosition(position).toString()

                // Save selected language preference
                sharedLangPreferences.edit().putString("language", language).apply()

                // Set the new locale configuration
                val locale = Locale(language)
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)

                // Recreate the activity to apply the language changes
                requireActivity().recreate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "No language selected", Toast.LENGTH_SHORT).show()
            }
        }*/

        /*---------------------------------
        // Retrieve saved language preference from SharedPreferences
        sharedLangPreferences =
            requireContext().getSharedPreferences("language", Context.MODE_PRIVATE)
        languageSelected = sharedLangPreferences.getString("language", "") ?: ""

        Toast.makeText(requireContext(), "languagueSected : " + languageSelected, Toast.LENGTH_SHORT).show()

        val spLangType : Spinner = binding.spLanguage
        val langArray = resources.getStringArray(R.array.language)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, langArray)
        spLangType.adapter = adapter


// For language change
        binding.spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val language: String = parent.getItemAtPosition(position).toString()
                if (isLanguageSelectionByUser) {
                    // Set locale based on language selection
                    when (language) {
                        "English" -> {
                            Toast.makeText(requireContext(), "English", Toast.LENGTH_SHORT).show()
                            setLocale("en")
                        }
                        "Chinese" -> {
                            Toast.makeText(requireContext(), "Chinese", Toast.LENGTH_SHORT).show()
                            setLocale("ch")
                        }

                    }
                    // Update SharedPreferences with selected language
                    sharedLangPreferences.edit().putString("language", language).apply()
                    val languageIndex = langArray.indexOf(language)
                    if (languageIndex != -1) {
                        spLangType.setSelection(languageIndex)
                    }
                }
                // Reset the flag after user selection
                isLanguageSelectionByUser = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }-----------------------------------*/

        // Navigate to FAQ Fragment
        binding.btnFAQ.setOnClickListener() {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = faq()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // NAvigate to Edit Profile FRagment
        binding.btnEditProfile.setOnClickListener() {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = EditPersonalAcc()

            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // NAvigate to Applied Job Fragment
        binding.btnApplied.setOnClickListener() {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = ViewAppliedJob()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        /* // For language change
         binding.spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
             // Inside the onItemSelected method of the spinner's OnItemSelectedListener
             override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                 // Check if the language selection is made by the user
                 if (isLanguageSelectionByUser) {
                     val language: String = parent.getItemAtPosition(position).toString()

                     if (language == "Chinese") {
                         // Set locale to Chinese
                         (requireActivity() as LoginContainer).setLocale("ch")
                     } else {
                         // Set locale to English
                         (requireActivity() as LoginContainer).setLocale("en")
                     }


                     // Save selected language preference
                     sharedLangPreferences.edit().putString("language", language).apply()
                 }
                 // Reset the flag after user selection
                 isLanguageSelectionByUser = false
             }

             override fun onNothingSelected(parent: AdapterView<*>?) {
                 Toast.makeText(requireContext(), "No language selected", Toast.LENGTH_SHORT).show()
             }
         }
         */


        // Logout
        binding.btnLogout.setOnClickListener() {
            val intent = Intent(requireContext(), LoginContainer::class.java)
            sharedIDPreferences.edit().clear().apply()
            sharedUserTypePreferences.edit().clear().apply()
            sharedStatusPreference.edit().putBoolean("isFirstTime", true).apply()
            sharedPreferences.edit().putBoolean("night",false).apply()

            sharedLogoutPreferences.edit().putBoolean("logoutStatus", true).apply()
            startActivity(intent)
            requireActivity().finish() // this is to prevent user return back to login page
        }

      /*  //DeleteAccount
        binding.btnDeleteAcc.setOnClickListener(){
            // If any invalid input
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Delete Account")
                .setMessage("Confirm to Delete")
                .setPositiveButton("YES") { dialog, _ ->
                    deleteAccount()
                }
                .setNegativeButton("NO"){dialog,_ ->
                    Toast.makeText(requireContext(), "Account not deleted.", Toast.LENGTH_SHORT).show()
                }
            builder.show()
        }*/

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

      /* ------------------------NEEED  TO UNCOMMENT LATER
      binding.btnBookmark.setOnClickListener(){
            val fragment = BookmarkListing()

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }*/
        return view
    }

    private fun setLocale(language: String) {
        if(language!=""){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        // Recreate activity to apply changes
        requireActivity().recreate()}
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
        Toast.makeText(requireContext(), "Retrieve Record : " + userId, Toast.LENGTH_SHORT).show()
        val tvName: TextView = binding.tvName
        val tvEmail: TextView = binding.tvEmail
        val profileImg: ImageView = binding.profilePic

        myRef = FirebaseDatabase.getInstance().getReference("Personal")

        myRef.child(userId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(Personal::class.java)
                        if (user != null) {

                            tvName.text = user.name
                            tvEmail.text = user.email

                            // Load profile image
                            if (user.profileImg != "") {
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



    /*
    // Function to delete the user account
    private fun deleteAccount() {
        // Reference to the "Personal" node in Firebase database
        val personalRef = myRef.child("Personal")
        // Query to check if the user ID exists
        personalRef.orderByChild("personalID").equalTo(userId.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // User with the specified ID exists, delete the account
                        snapshot.ref.removeValue()
                            .addOnSuccessListener {
                                val intent = Intent(requireContext(), LoginContainer::class.java)
                                sharedIDPreferences.edit().clear().apply()
                                sharedUserTypePreferences.edit().clear().apply()
                                sharedStatusPreference.edit().putBoolean("isFirstTime", true).apply()

                                sharedLogoutPreferences.edit().putBoolean("logoutStatus", true).apply()
                                startActivity(intent)
                                requireActivity().finish() // this is to prevent user return back to login page
                            }
                            .addOnFailureListener { exception ->
                                // Error occurred while deleting account
                                Toast.makeText(requireContext(), "Failed to delete account: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // User with the specified ID does not exist
                        Toast.makeText(requireContext(), "User with ID $userId does not exist", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }*/

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