package com.example.careerhunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentForgetPasswordBinding

class ForgetPassword : Fragment() {
    private lateinit var binding:FragmentForgetPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.btnResetPass.setOnClickListener(){
            if (resetPassword()) {
                Toast.makeText(requireContext(), "Reset Successful", Toast.LENGTH_SHORT).show()
                backToLogin()
            } else {
                Toast.makeText(requireContext(), "Unable to reset", Toast.LENGTH_SHORT).show()
            }        }
        return view
    }

    private fun resetPassword():Boolean{
        return true
    }

    private fun backToLogin() {
        val intent = Intent(requireContext(), LoginScreen::class.java)
        startActivity(intent)
        requireActivity().finish() // this is to finish the hosting activity
    }
}