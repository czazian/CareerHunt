package com.example.careerhunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentBusinessAccountBinding


class BusinessAccount : Fragment() {
    private lateinit var binding: FragmentBusinessAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBusinessAccountBinding.inflate(inflater, container, false)
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

        binding.btnLogout.setOnClickListener() {
            val intent = Intent(requireContext(), LoginContainer::class.java)

            startActivity(intent)
            requireActivity().finish() // this is to prevent user return back to profile page
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


}