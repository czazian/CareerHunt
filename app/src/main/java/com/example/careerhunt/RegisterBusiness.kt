package com.example.careerhunt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentRegisterBusinessBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class RegisterBusiness : Fragment() {
    private lateinit var binding: FragmentRegisterBusinessBinding

    //private lateinit var database: DatabaseReference
    private var database =
        FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var myRef = database.reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBusinessBinding.inflate(inflater, container, false)
        val view = binding.root

        //Database connection
        //database =FirebaseDatabase.getInstance().getReference("Company")

        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.btnRegister.setOnClickListener() {
            val name: String = binding.tfCompName.text.toString()
            val email: String = binding.tfCompEmail.text.toString()
            val password: String = binding.tfPass.text.toString()
            val phoneNum: String = binding.tfCompPhone.text.toString()
            val compAdd: String = binding.tfCompAddr.text.toString()


            val errMsg = fieldValidation(name, email, password, compAdd,phoneNum)

            myRef.child("Company").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Get the count of children nodes
                    val count = snapshot.childrenCount.toInt()

                    val companyID = count + 1 // Increment count for the new entry

                    if (errMsg.isEmpty()) {
                        val hashedPassword = hashPassword(password)

                        val company = Company(
                                companyID,
                                name.toString(),
                                email.toString(),
                                hashedPassword,
                                "",
                                phoneNum,
                                compAdd.toString(),
                            )
                            // Push the reservation data to Firebase
                            myRef.child("Company").child(companyID.toString())
                                .setValue(company)
                                .addOnCompleteListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Information Recorded",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Toast.makeText(
                                        requireContext(),
                                        "Register Successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    backToLogin()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error ${it.toString()}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                    } else {

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Registration Failed")
                            .setMessage(errMsg.toString())
                            .setPositiveButton("OK") { dialog, _ ->
                                Toast.makeText(
                                    requireContext(),
                                    "Please follow the instruction",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        builder.show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })
        }
        return view
    }

    private fun backToLogin() {
        requireActivity().onBackPressed()

        /* val intent = Intent(requireContext(), LoginScreen::class.java)
         startActivity(intent)
         requireActivity().finish() // this is to finish the hosting activity*/
    }

    // Function to validate email format
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }

    fun fieldValidation(
        name: String,
        email: String,
        password: String,
        compAdd: String,
        phoneNum:String
    ): String {
        var errorMessage = ""

        // Perform field validation
        if (name.isEmpty()) {
            errorMessage += "*Name cannot be empty.\n"
        }

        if (email.isEmpty()) {
            errorMessage += "*Email cannot be empty.\n"
        } else if (!isValidEmail(email)) {
            errorMessage += "*Invalid email format.\n"
        }

        if (password.isEmpty()) {
            errorMessage += "*Password cannot be empty.\n"
        }

        if (compAdd.isEmpty()) {
            errorMessage += "*Companny Address cannot be empty.\n"
        }


        if(phoneNum.isEmpty()){
            errorMessage += "*Phone Number cannot be empty.\n"
        }else if(!isValidPhoneNum(phoneNum)){
            errorMessage += "*Invalid phone number format.\n"
        }

        return errorMessage
    }

    fun isValidPhoneNum(phone: String): Boolean {
        val phoneRegex = Regex("^\\d{3}-\\d{7}\$")
        return phoneRegex.matches(phone)
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