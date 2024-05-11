package com.example.careerhunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentLoginScreenBinding
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Personal
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginScreen : Fragment() {
    private lateinit var binding: FragmentLoginScreenBinding

    //private lateinit var database: DatabaseReference
    private var database =
        FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private var myRef = database.reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        val view = binding.root

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

            /*if(userType == "Company"){
                performCompLogin(loginEmail, loginPasswd, userType)
            }else{
                performPersonalLogin(loginEmail, loginPasswd, userType)
            }*/

            performLogin(loginEmail, loginPasswd, userType)


            /*if(validateUser(loginEmail,loginPasswd)){
                navigateToMainActivity(userType)
            }else{
                Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
            }*/

        }
        return view

    }


    /* // return true if the entered email and password correct
     private fun validateUser(email: String, passwd: String): Boolean {
         return email == "123456" && passwd == "123456"
     }*/

    private fun navigateToMainActivity(userType: String, userId:String?) {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        // passing the account type to MainActivity

        intent.putExtra("user_type", userType)
        intent.putExtra("user_id",userId.toString())

        startActivity(intent)
        requireActivity().finish() // this is to prevent user return back to login page
    }

    // Function to perform login
    private fun performLogin(email: String, password: String, userType: String) {
        // Determine which node to query based on the selected user type
        val accType = if (userType == "Personal") "Personal" else "Company"

        // Query the database for the user with the specified email and password
        myRef.child(accType).orderByChild("email").equalTo(email)
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
                                        // Passwords match, proceed with login
                                        Toast.makeText(
                                            requireContext(),
                                            "Login Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val userId = userSnapshot.key // Get the unique ID
                                        Toast.makeText(
                                            requireContext(),
                                            "ID: " + userId.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navigateToMainActivity(userType, userId.toString())
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
                                        // Passwords match, proceed with login
                                        Toast.makeText(
                                            requireContext(),
                                            "Login Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val userId = userSnapshot.key // Get the unique ID
                                        Toast.makeText(
                                            requireContext(),
                                            "ID: " + userId.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navigateToMainActivity(userType, userId.toString())
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



