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
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentEditPersonalAccBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditPersonalAcc : Fragment() {
  private lateinit var binding: FragmentEditPersonalAccBinding
    private var database =
        FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private var myRef = database.reference
    private lateinit var sharedIDPreferences: SharedPreferences
    private var userId: String = ""
    private lateinit var storageRef : StorageReference
    private lateinit var galleryUri : Uri
    private lateinit var img : ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPersonalAccBinding.inflate(inflater, container, false)
        val view = binding.root

        // Access SharedPreferences from MainActivity
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        // Retrieve userId from SharedPreferences
        userId = sharedIDPreferences.getString("userid","")?:""
        retrieveUserRecord()

        storageRef = FirebaseStorage.getInstance().getReference()

        img = view.findViewById(R.id.profilePic)

        binding.profilePic.setOnClickListener(){
            galleryLauncher.launch("image/*")
        }

        //profilePicImageView = binding.profilePic as CircleImageView
        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.btnSaveChanges.setOnClickListener(){
            //Saving and update the user records
                saveChanges()
        }
        return view
    }

    private fun retrieveUserRecord(){
        val tfEmail : TextView = binding.tvEditEmail
        val tfEditName : TextView = binding.tfEditName
        val tfEditPhone : TextView = binding.tfEditPhone
        val rgGender : RadioGroup = binding.rgEditGender
        val profileImg : ImageView = binding.profilePic

        val spEditJobField : Spinner = binding.spEditJobField
        val jobFieldArray = resources.getStringArray(R.array.jobCategory)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jobFieldArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEditJobField.adapter = adapter

        // Query the database for the user with the matched userID
        myRef.child("Personal").child(userId.toString())// use this userId to compare with the unique key
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(Personal::class.java)
                        if (user != null) {
                            //Assign the current value to the field (for showing purpose)
                            tfEmail.text = user.email
                            tfEditName.text = user.name
                            tfEditPhone.text = user.phoneNum

                            // Load profile image
                            if(user.profileImg!= ""){
                                val profileImageUrl = user.profileImg
                                Glide.with(requireContext()).load(profileImageUrl).into(profileImg)
                            }

                            // Set gender
                            if (user.gender == "Male") {
                                rgGender.check(R.id.rbEditMale)
                            } else if (user.gender == "Female") {
                                rgGender.check(R.id.rbEditFemale)
                            }

                            // Find the index of the job field value in the string array
                            val jobFieldIndex = jobFieldArray.indexOf(user.jobField)
                            if (jobFieldIndex != -1) {
                                // Set the selected item of the spinner to the job field value
                                spEditJobField.setSelection(jobFieldIndex)
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

    private fun saveChanges(){

        val name : String = binding.tfEditName.text.toString()
        val phoneNum: String = binding.tfEditPhone.text.toString()
        val rgGender : RadioGroup = binding.rgEditGender
        val gender: String = when (rgGender.checkedRadioButtonId) {
            R.id.rbEditMale -> "Male"
            R.id.rbEditFemale -> "Female"
            else -> ""
        }

        val spEditJobField : Spinner = binding.spEditJobField
        val jobField : String = spEditJobField.selectedItem.toString()

        val errMsg = fieldValidation(name,gender, jobField,phoneNum)

        // Check if a new profile picture is selected
        val isNewProfilePictureSelected = ::galleryUri.isInitialized

        // Store the changes into Firebase
        if (errMsg.isEmpty()) {
            val updates = hashMapOf<String, Any>(
                "name" to name,
                "phoneNum" to phoneNum,
                "gender" to gender,
                "jobField" to jobField
            )

            if (isNewProfilePictureSelected) {
                // If a new profile picture is selected, proceed with uploading it
                val ref = storageRef.child("imgProfile/" + userId + ".png")

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
            builder.setTitle("Update Failed")
                .setMessage(errMsg.toString())
                .setPositiveButton("OK") { dialog, _ ->
                    Toast.makeText(
                        requireContext(),
                        "Please follow the instructions",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            builder.show()
        }
    }

    private fun updateProfile(updates: HashMap<String, Any>) {
        myRef.child("Personal").child(userId.toString()).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Update Completed", Toast.LENGTH_SHORT)
                    .show()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val newFragment = UserProfile()
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
        gender: String,
        jobField: String,
        phoneNum : String
    ): String {
        var errorMessage = ""

        // Perform field validation
        if (name.isEmpty()) {
            errorMessage += "*Name cannot be empty.\n"
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

    fun isValidPhoneNum(phone: String): Boolean {
        val phoneRegex = Regex("^\\d{3}-\\d{7}\$")
        return phoneRegex.matches(phone)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        galleryUri = it!!
        try{
            img.setImageURI(galleryUri)
        }catch(e:Exception){
            e.printStackTrace()
        }

    }




}