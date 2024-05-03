package com.example.careerhunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentRegisterBusinessBinding

class RegisterBusiness : Fragment() {
    private lateinit var binding: FragmentRegisterBusinessBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBusinessBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.btnRegister.setOnClickListener(){
            Toast.makeText(requireContext(), "Successfully Registered", Toast.LENGTH_SHORT).show()
            backToLogin()
        }
        return view
    }

    private fun backToLogin() {
        val intent = Intent(requireContext(), LoginScreen::class.java)
        startActivity(intent)
        requireActivity().finish() // this is to finish the hosting activity
    }

}