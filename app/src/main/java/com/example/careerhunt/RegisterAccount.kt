package com.example.careerhunt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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

    //private lateinit var database: DatabaseReference
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

        //Database connection
        //database =FirebaseDatabase.getInstance().getReference("Personal")// refer to the tree Person

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

                    // Now you can use the count value as needed, such as for assigning a new personalID
                    // For example:
                    val personalID = count + 1 // Increment count for the new entry

                    Toast.makeText(requireContext(),"personalID: " + personalID,Toast.LENGTH_SHORT).show()

                    if (errMsg.isEmpty()) {
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
                        // If any invalid input
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
        Toast.makeText(requireContext(), "back to login", Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressed()

        /*val intent = Intent(requireContext(), LoginContainer::class.java)
        startActivity(intent)
        requireActivity().finish() // this is to finish the hosting activity
         */
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

    /* fun isUniqueEmail(email:String): Task<DataSnapshot> {
         String uniqueEmail = database.child("Personal").child("Email").get()
         if(uniqueEmail == email)


     }*/

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