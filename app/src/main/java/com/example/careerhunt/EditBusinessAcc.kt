package com.example.careerhunt

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentEditBusinessAccBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text


class EditBusinessAcc : Fragment() {
    private lateinit var binding: FragmentEditBusinessAccBinding
    private lateinit var myRef: DatabaseReference

    private lateinit var storageRef: StorageReference
    private lateinit var galleryUri: Uri
    private lateinit var sharedIDPreferences: SharedPreferences
    private var userId: String = ""
    private lateinit var img: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBusinessAccBinding.inflate(inflater, container, false)
        val view = binding.root

        // Access SharedPreferences from MainActivity
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        // Retrieve userId from SharedPreferences
        userId = sharedIDPreferences.getString("userid", "") ?: ""
        retrieveCompRecord()

        storageRef = FirebaseStorage.getInstance().getReference()

        img = view.findViewById(R.id.profilePic)


        //profilePicImageView = binding.profilePic as CircleImageView
        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.btnSaveChanges.setOnClickListener() {
            //Saving and update the user records
            saveChanges()
        }

        binding.profilePic.setOnClickListener() {
            galleryLauncher.launch("image/*")
        }

        return view

    }


    private fun retrieveCompRecord() {
        val tfEditCompEmail: TextView = binding.tvEditCompEmail
        val tfEditCompName: TextView = binding.tfEditCompName
        val tfEditCompPhone: TextView = binding.tfEditCompPhone
        val tfEditCompAdd: TextView = binding.tfEditCompAdd
        val profileImg: ImageView = binding.profilePic


        myRef = FirebaseDatabase.getInstance().getReference("Company")

        myRef.child(userId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val comp = snapshot.getValue(Company::class.java)
                        if (comp != null) {
                            //Assign the current value to the field (for showing purpose)
                            tfEditCompEmail.text = comp.email
                            tfEditCompName.text = comp.compName
                            tfEditCompPhone.text = comp.compPhoneNum
                            tfEditCompAdd.text = comp.compAddress

                            // Load profile image
                            if (comp.compProfile != "") {
                                val profileImageUrl = comp.compProfile
                                Glide.with(requireContext()).load(profileImageUrl).into(profileImg)
                            }
                        }
                    } else {
                        // if the user id not found in Firebase
                        Toast.makeText(
                            requireContext(),
                            "User with ID $userId not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun saveChanges() {
        val email: String = binding.tvEditCompEmail.text.toString()
        val name: String = binding.tfEditCompName.text.toString()
        val phoneNum: String = binding.tfEditCompPhone.text.toString()
        val address: String = binding.tfEditCompAdd.text.toString()

        val errMsg = fieldValidation(name, email, address, phoneNum)
        myRef = FirebaseDatabase.getInstance().getReference("Company")


        // Check if a new profile picture is selected
        val isNewProfilePictureSelected = ::galleryUri.isInitialized

        // store the changes into firebase
        if (errMsg.isEmpty()) {
            val updates = hashMapOf<String, Any>(
                "compName" to name,
                "compPhoneNum" to phoneNum,
                "compAddress" to address,
            )
            if (isNewProfilePictureSelected) {
                // If a new profile picture is selected, proceed with uploading it
                val ref = storageRef.child("compProfile/" + userId + ".png")

                ref.putFile(galleryUri)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            val profileImageUrl = uri.toString()
                            updates["profileImg"] = profileImageUrl

                            updateProfile(updates)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Fail to Update Profile Picture: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // If no new profile picture is selected, update other fields using the existing profile image
                updateProfile(updates)
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

private fun updateProfile(updates: HashMap<String, Any>) {
    myRef.child(userId.toString()).updateChildren(updates)
        .addOnSuccessListener {
            Toast.makeText(
                requireContext(),
                "Update Completed",
                Toast.LENGTH_SHORT
            ).show()
            val fragmentManager =
                requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = BusinessAccount()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        .addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Fail to Update: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
}


    fun fieldValidation(
        name: String,
        email: String,
        address: String,
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

        if (address.isEmpty()) {
            errorMessage += "*Address cannot be empty.\n"
        }

        if (phoneNum.isEmpty()) {
            errorMessage += "*Phone Number cannot be empty.\n"
        } else if (!isValidPhoneNum(phoneNum)) {
            errorMessage += "*Invalid phone number format.\n"
        }

        return errorMessage
    }

    // Function to validate email format
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }


    fun isValidPhoneNum(phone: String): Boolean {
        val phoneRegex = Regex("^\\d{3}-\\d{7}\$")
        return phoneRegex.matches(phone)
    }


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        galleryUri = it!!
        try {
            img.setImageURI(galleryUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}