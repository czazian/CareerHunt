package com.example.careerhunt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentLoginScreenBinding
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Personal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginScreen : Fragment() {
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var myRef: DatabaseReference
    lateinit var sharedStatusPreference: SharedPreferences
    lateinit var sharedIDPreferences: SharedPreferences
    lateinit var sharedUserTypePreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedStatusPreference = requireContext().getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        sharedUserTypePreferences = requireContext().getSharedPreferences("userType", Context.MODE_PRIVATE)
        //Database connection
        // database = FirebaseDatabase.getInstance().getReference("Personal")

        binding.btnLogin.setOnClickListener {

            val loginEmail: String = binding.tfEmail.text.toString()
            val loginPasswd: String = binding.tfPass.text.toString()
            val userType = when (binding.rgAccType.checkedRadioButtonId) {
                R.id.rbBusiness -> "Company"
                R.id.rbPersonal -> "Personal"
                else -> ""
            }

            performLogin(loginEmail, loginPasswd, userType)

        }
        return view

    }


    private fun navigateToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)

        startActivity(intent)
        requireActivity().finish() // this is to prevent user return back to login page
    }

    // Function to perform login
    private fun performLogin(email: String, password: String, userType: String) {
        // Determine which node to query based on the selected user type
        val accType = if (userType == "Personal") "Personal" else "Company"

        myRef = FirebaseDatabase.getInstance().getReference(accType)

        // Query the database for the user with the specified email and password
        myRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            // Check if the user is of type Personal
                            if (accType == "Personal") {
                                val user = userSnapshot.getValue(Personal::class.java)
                                if (user != null) {
                                    // Retrieve the hashed password from the firebase
                                    val hashedPassword = user.password

                                    // Hash the password that the user entered
                                    val hashedEnteredPassword = hashPassword(password)

                                    //compare both encrypted password
                                    if (hashedPassword == hashedEnteredPassword) {
                                        val userId = userSnapshot.key // Get the unique ID

                                        sharedStatusPreference.edit().putBoolean("isFirstTime", false).apply()
                                        sharedIDPreferences.edit().putString("userid",userSnapshot.key).apply()
                                        sharedUserTypePreferences.edit().putString("userType",userType).apply()

                                        Toast.makeText(
                                            requireContext(),
                                            "Login Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        navigateToMainActivity()
                                        return
                                    }

                                }
                            } else if (accType == "Company") {
                                // Check if the user is of type Company
                                val business = userSnapshot.getValue(Company::class.java)
                                if (business != null) {
                                    // Retrieve the hashed password from the firebase
                                    val hashedPassword = business.password

                                    // Hash the password that the user entered
                                    val hashedEnteredPassword = hashPassword(password)

                                    //compare both encrypted password
                                    if (hashedPassword == hashedEnteredPassword) {

                                        sharedStatusPreference.edit().putBoolean("isFirstTime", false).apply()
                                        sharedIDPreferences.edit().putString("userid",userSnapshot.key).apply()
                                        sharedUserTypePreferences.edit().putString("userType",userType).apply()

                                        Toast.makeText(
                                            requireContext(),
                                            "Login Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navigateToMainActivity()
                                        return
                                    }
                                }
                            }
                        }
                        // If no matching user found
                        Toast.makeText(
                            requireContext(),
                            "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // No user with the entered email found
                        Toast.makeText(
                            requireContext(),
                            "User not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database query cancellation or error
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // encrypt the password
    private fun hashPassword(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString(separator = "") { "%02x".format(it) }
        } catch (ex: NoSuchAlgorithmException) {
            ""// if error on converting to hash
        }
    }

}



