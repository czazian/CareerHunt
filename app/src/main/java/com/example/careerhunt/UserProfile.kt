package com.example.careerhunt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentUserProfileBinding

class UserProfile : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = binding.root



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


        binding.btnLogout.setOnClickListener() {
            val intent = Intent(requireContext(), LoginContainer::class.java)

            startActivity(intent)
            requireActivity().finish() // this is to prevent user return back to profile page
        }


        //For dark mode
        sharedPreferences = requireContext().getSharedPreferences("Mode",Context.MODE_PRIVATE)
        val nightMode = sharedPreferences.getBoolean("night",false) // false = day mode

        // means it is true = make it become night mode
        if(nightMode){
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


}