package com.example.careerhunt

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentRegisterAccountBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class RegisterAccount : Fragment() {
    private lateinit var binding: FragmentRegisterAccountBinding
    private var database =
        FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private var myRef = database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.btnSubmit.setOnClickListener() {
            val name: String = binding.tfName.text.toString()
            val email: String = binding.tfEmail.text.toString()
            val password: String = binding.tfPass.text.toString()
            val phoneNum: String = binding.tfPhone.text.toString()
            val gender: String = when (binding.rgGEnder.checkedRadioButtonId) {
                R.id.rbMale -> "Male"
                R.id.rbFemale -> "Female"
                else -> ""
            }
            val jobField: String = binding.spJobField.selectedItem.toString()

            val errMsg = fieldValidation(name, email, password, gender, jobField,phoneNum)

            // Query to count the number of children under "Personal" node
            myRef.child("Personal").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Get the count of children nodes
                    val count = snapshot.childrenCount.toInt()

                    val personalID = count + 1 // Increment count for the new entry

                    if (errMsg.isEmpty()) {
                        // Check if email is unique
                        checkUniqueEmail(email) { isUnique ->
                            if (isUnique) {
                                val hashedPassword = hashPassword(password)

                                val personal = Personal(
                                    personalID,
                                    name.toString(),
                                    email.toString(),
                                    hashedPassword,
                                    "",
                                    "",
                                    phoneNum,
                                    gender.toString(),
                                    jobField.toString()
                                )

                                // Push the data to Firebase
                                myRef.child("Personal").child(personalID.toString())
                                    .setValue(personal)
                                    .addOnCompleteListener {
                                        assignDefaultProfileImage(personalID)

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
                                // Email already exists, show error message
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setTitle("Registration Failed")
                                    .setMessage("*Email already exists. Please enter a different email address")
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                builder.show()
                            }
                        }

                    } else {
                        // If any invalid input
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Registration Failed")
                            .setMessage(errMsg.toString())
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
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
    }

    fun fieldValidation(
        name: String,
        email: String,
        password: String,
        gender: String,
        jobField: String,
        phoneNum: String
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

        if (gender.isEmpty()) {
            errorMessage += "*Gender cannot be empty.\n"
        }


        if (jobField.isEmpty()) {
            errorMessage += "*Job Field cannot be empty.\n"
        }

        if(phoneNum.isEmpty()){
            errorMessage += "*Phone Number cannot be empty.\n"
        }else if(!isValidPhoneNum(phoneNum)){
            errorMessage += "*Invalid phone number format.\n"
        }

        return errorMessage
    }

    // Function to validate email format
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }

    private fun checkUniqueEmail(email: String, callback: (Boolean) -> Unit) {
        // Reference to the "Personal" node in Firebase database
        val personalRef = myRef.child("Personal")

        // Query to check if the email already exists
        personalRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // If a user with the given email already exists
                    val isUnique = snapshot.childrenCount == 0L
                    callback(isUnique)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    callback(false)
                }
            })
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

    // every personal that reguster will be assigned with a default profile pic
    private fun assignDefaultProfileImage(personalID:Int) {
        // Get the default profile image path from Firebase Storage
        val defaultProfileImagePath = "gs://careerhunt-e6787.appspot.com/imgProfile/default_profile.png"

        // Update the user's profileImg field with the default profile image path
        myRef.child("Personal").child(personalID.toString()).child("profileImg")
            .setValue(defaultProfileImagePath)
            .addOnSuccessListener {
                // Profile image path assigned successfully
                Log.d(TAG, "Default profile image path assigned successfully")
            }
            .addOnFailureListener { e ->
                // Error assigning default profile image path
                Log.e(TAG, "Error assigning default profile image path: ${e.message}")
            }
    }


}