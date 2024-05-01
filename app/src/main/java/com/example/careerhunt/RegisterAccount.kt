package com.example.careerhunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentRegisterAccountBinding

class RegisterAccount : Fragment() {
    private lateinit var binding:FragmentRegisterAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnBack.setOnClickListener(){
            requireActivity().onBackPressed()
        }

        binding.btnSubmit.setOnClickListener(){
            Toast.makeText(requireContext(), "Register Successfully", Toast.LENGTH_SHORT).show()
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