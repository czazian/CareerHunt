package com.example.careerhunt

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.careerhunt.data.Job
import com.example.careerhunt.data.Personal
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Applicant_details : Fragment() {
    private lateinit var storageRef : StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_applicant_details, container, false)
        // Retrieve the Job object from arguments
        val applicant: Personal? = arguments?.getSerializable("applicant") as? Personal
        storageRef = FirebaseStorage.getInstance().getReference()


        val tvApplName : TextView = view.findViewById(R.id.tvApplName)
        val tvApplEmail : TextView = view.findViewById(R.id.tvApplEmail)
        val tvApplGender : TextView = view.findViewById(R.id.tvApplGender)
        val tvApplPhone : TextView = view.findViewById(R.id.tvApplPhone)
        val btnViewResume : Button = view.findViewById(R.id.btnViewResume)
        val btnAccept : Button = view.findViewById(R.id.btnAccept)
        val btnReject : Button = view.findViewById(R.id.btnReject)


        tvApplName.text = applicant?.name.toString()
        tvApplEmail.text = applicant?.email.toString()
        tvApplGender.text = applicant?.gender.toString()
        tvApplPhone.text = applicant?.phoneNum.toString()


        btnViewResume.setOnClickListener(){
            val ref = storageRef.child("Resume").child(applicant?.personalID.toString() + ".pdf")

            ref.downloadUrl
                .addOnSuccessListener { uri ->
                    openPDF(uri.toString())
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that may occur during the download
                    Log.e(ContentValues.TAG, "Error downloading PDF", exception)
                    Toast.makeText(requireContext(), "Error downloading PDF", Toast.LENGTH_SHORT).show()
                }
        }

        btnAccept.setOnClickListener(){

            val email = Uri.parse("mailto:${applicant?.email}")
            val intent = Intent(Intent.ACTION_SENDTO, email).apply {
                putExtra(Intent.EXTRA_SUBJECT, "Congratulation, your application have been approved.") // Add subject
                putExtra(Intent.EXTRA_TEXT, "you are invited to join an interview section.") // Add email body
            }
            startActivity(intent)
        }

        btnReject.setOnClickListener(){
            val email = Uri.parse("mailto:${applicant?.email}")
            val intent = Intent(Intent.ACTION_SENDTO, email).apply {
                putExtra(Intent.EXTRA_SUBJECT, "Sorry, your application is rejected.") // Add subject
            }
            startActivity(intent)
        }





        return view
    }

    private fun openPDF(downloadUrl: String) {
        val uri = Uri.parse(downloadUrl)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf") // Changed MIME type to application/pdf
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer installed", Toast.LENGTH_SHORT).show()
        }
    }
}