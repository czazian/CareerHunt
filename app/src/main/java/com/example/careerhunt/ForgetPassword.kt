package com.example.careerhunt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentForgetPasswordBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class ForgetPassword : Fragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private var database =
        FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private var myRef = database.reference

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

        binding.btnResetPass.setOnClickListener() {
            val userType = when (binding.rgAccType.checkedRadioButtonId) {
                R.id.rbBusiness -> "Company"
                R.id.rbPersonal -> "Personal"
                else -> ""
            }
            val resetEmail: String = binding.tfEmail.text.toString()
            val resetPasswd: String = binding.tfNewPass.text.toString()
            val confirmPasswd: String = binding.tfConfirmPass.text.toString()

            validatePassword(resetPasswd, confirmPasswd, resetEmail, userType)
        }
        return view
    }

    private fun validatePassword(
        resetPasswd: String,
        confirmPasswd: String,
        resetEmail: String,
        userType: String
    ) {

        if (resetPasswd == confirmPasswd) {
            resetPassword(userType, resetEmail, confirmPasswd)
        } else {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Failed to reset password")
                .setMessage("Your new password should same with the confirm password")
                .setPositiveButton("OK") { dialog, _ ->
                    Toast.makeText(
                        requireContext(),
                        "Please follow the instruction",
                        Toast.LENGTH_LONG
                    ).show()
                }
            builder.show()
        }
    }

    private fun resetPassword(userType: String, resetEmail: String, confirmPasswd: String) {
        val accType = if (userType == "Personal") "Personal" else "Company"

        myRef.child(accType).orderByChild("email").equalTo(resetEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            // Check if the user is of type Personal
                            if (accType == "Personal") {
                                val user = userSnapshot.getValue(Personal::class.java)
                                if (user != null) {
                                    // Hash the new password
                                    val hashedPassword = hashPassword(confirmPasswd)

                                    // reset password must same as confirm password
                                    val updates = hashMapOf<String, Any>(
                                        "password" to hashedPassword,
                                    )

                                    myRef.child("Personal").child(userSnapshot.key.toString())
                                        .updateChildren(updates)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                requireContext(),
                                                "Your Password had been reset.",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            backToLogin()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                requireContext(),
                                                "Fail to Reset: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            } else if (accType == "Company") {
                                // Check if the user is of type Company
                                val business = userSnapshot.getValue(Company::class.java)
                                if (business != null) {
                                    // Hash the new password
                                    val hashedPassword = hashPassword(confirmPasswd)

                                    // reset password must same as confirm password
                                    val updates = hashMapOf<String, Any>(
                                        "password" to hashedPassword,
                                    )

                                    myRef.child("Company").child(business.companyID.toString())
                                        .updateChildren(updates)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                requireContext(),
                                                "Your Password had been reset.",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            backToLogin()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                requireContext(),
                                                "Fail to Reset: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        }
                    } else {
                        // No user with the entered email found
                        Toast.makeText(
                            requireContext(),
                            "Email account not exists.",
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

    private fun backToLogin() {
        val intent = Intent(requireContext(), LoginContainer::class.java)
        startActivity(intent)
        requireActivity().finish() // this is to finish the hosting activity
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