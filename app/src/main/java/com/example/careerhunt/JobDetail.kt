package com.example.careerhunt


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.careerhunt.data.Apply_Job
import com.example.careerhunt.data.Job
import com.example.careerhunt.databinding.FragmentJobDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.FileNotFoundException


class JobDetail : Fragment() {

    private lateinit var binding: FragmentJobDetailBinding
    private lateinit var dialogToDisplay: View
    private lateinit var uploadDialog: Dialog
    private lateinit var dbRef : DatabaseReference
    private lateinit var storageRef : StorageReference

    private var lastID: Long = 0
    private var jobID: Long = 0
    private var personalID: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobDetailBinding.inflate(layoutInflater, container, false)


        //Database initialization
        dbRef = FirebaseDatabase.getInstance().getReference()
        storageRef = FirebaseStorage.getInstance().getReference()

        val bundle = arguments
        val job = bundle!!.getSerializable("job") as? Job


        binding.jobCat.text = job!!.jobCategory.toString()
        binding.jobTy.text = job!!.jobType.toString()
        binding.jobTi.text = job!!.jobName.toString()
        binding.jobLoca.text = job!!.jobLocationState.toString() + ", " + job!!.jobLocationCity.toString()
        binding.lblSalaryResult.text = job!!.jobSalary.toString()
        binding.lvlPostDateResult.text = job!!.jobPostedDate.toString()
        binding.descResult.text = job!!.jobDescription.toString()

        //Get JobID and Personal ID
        jobID = job!!.jobID.toString().toLong()
        personalID = 1 //Hard code data


        //Back Button
        binding.btnBack.setOnClickListener() {
            //Back to previous page
            getActivity()?.onBackPressed();
        }


        //When user clicks on the apply button
        binding.btnApply.setOnClickListener() {
            dialogToDisplay =
                LayoutInflater.from(this.requireContext()).inflate(R.layout.upload_resume, null)
            uploadDialog = AlertDialog.Builder(this.requireContext(), R.style.CustomAlertDialog)
                .setView(dialogToDisplay)
                .create()

            uploadDialog.show()

            val uploadButton = dialogToDisplay.findViewById<Button>(R.id.btnSelectFile)
            uploadButton.setOnClickListener() {
                val intent = Intent()
                    .setType("application/pdf")
                    .setAction(Intent.ACTION_GET_CONTENT)

                //A custom request code of 100
                Toast.makeText(requireContext(), "Get Image", Toast.LENGTH_LONG)
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 100)
            }

            val cancelButton = dialogToDisplay.findViewById<ImageButton>(R.id.cancelUploadButton)
            cancelButton.setOnClickListener() {
                uploadDialog.dismiss()
            }
        }

        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //val selectedFile = data?.data // The URI with the location of the file
            val filename: String = getFilename(data)

            // Update the display filename
            val fileNameTextView = dialogToDisplay.findViewById<TextView>(R.id.fileName)
            fileNameTextView.text = filename
            fileNameTextView.setBackgroundResource(R.drawable.border)
            dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume).isClickable = true
            dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume).setBackgroundColor(resources.getColor(R.color.uploadResumeBtn))



            //Confirm to submit the resume
            val submitBtn = dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume)
            submitBtn.setOnClickListener() {
                if(data?.data != null){
                    //Get Count of Column -> For Last ID
                    dbRef = FirebaseDatabase.getInstance().getReference("Apply_Job")
                    dbRef.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()) {
                                lastID = snapshot.childrenCount

                                //Create a record in Apply_Job
                                //Create Object
                                val applyjob = Apply_Job(
                                    (lastID+1),
                                    jobID,
                                    personalID
                                )

                                //Insert into Firebase with Last ID + 1
                                dbRef.child((lastID+1).toString()).setValue(applyjob)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Job has inserted successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Job has fail inserted!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                //Define the file name and location -> Should define the Apply_Job ID as the reference key
                                val ref = storageRef.child("Resume/" + (lastID+1) + ".pdf")

                                //Insert file into firebase storage
                                ref.putFile(data.data!!)
                                    .addOnCompleteListener{
                                        Toast.makeText(requireContext(), "Resume has been uploaded successfully.", Toast.LENGTH_SHORT).show()
                                        uploadDialog.dismiss()
                                    }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
                        }
                    })

                } else {
                    Toast.makeText(requireContext(), "Please select a resume.", Toast.LENGTH_SHORT).show()
                    dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume).isClickable = false
                    dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume).setBackgroundColor(resources.getColor(R.color.lightGrey))
                }
            }

        }
    }

    fun getFilename(returnIntent: Intent?): String {
        var filename = ""

        returnIntent?.data?.let { returnUri ->
            requireContext().contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            filename = cursor.getString(nameIndex)
        }

        return filename
    }

}