package com.example.careerhunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentLoginScreenBinding

class LoginScreen : Fragment() {
    private lateinit var binding: FragmentLoginScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.btnLogin.setOnClickListener {
            val type: String = when (binding.rgAccType.checkedRadioButtonId) {
                R.id.rbBusiness -> "Business"
                R.id.rbPersonal -> "Personal"
                else -> ""
            }

            val loginEmail: String = binding.tfEmail.text.toString()
            val loginPasswd: String = binding.tfPass.text.toString()

            // check whether the entered email and password match or not
            if (validateUser(loginEmail, loginPasswd)) {
                //if they are correct, proceed to MainActivity
                Toast.makeText(requireContext(), "Successfully Login", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            } else {
                //IF the entered email or passwd incorrect, show error msg
                Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        return view

    }


    // return true if the entered email and password correct
    private fun validateUser(email: String, passwd: String): Boolean {
        return email == "123456" && passwd == "123456"
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        // passing the account type to MainActivity
        val userType = when (binding.rgAccType.checkedRadioButtonId) {
            R.id.rbBusiness -> "Business"
            R.id.rbPersonal -> "Personal"
            else -> ""
        }
        intent.putExtra("user_type", userType)

        startActivity(intent)
        requireActivity().finish() // this is to prevent user return back to login page
    }


}