package com.example.careerhunt

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.careerhunt.data.Apply_Job
import com.example.careerhunt.data.Job
import com.example.careerhunt.data.Personal
import com.example.careerhunt.dataAdapter.AppliedJobListAdapter
import com.example.careerhunt.dataAdapter.ViewApplicantAdapter
import com.example.careerhunt.databinding.FragmentViewApplicantBinding
import com.example.careerhunt.interfaces.JobInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ViewApplicant : Fragment() ,JobInterface.RecyclerViewEvent {

    private var job: Job? = null
    private lateinit var myRef: DatabaseReference
    private lateinit var applicantList: ArrayList<Personal>
    private lateinit var recyclerView: RecyclerView
    private var totalApplicants: Int = 0
    private var fetchedApplicants: Int = 0
    private lateinit var storageRef : StorageReference
    private lateinit var tvTotalCount: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_applicant, container, false)

        myRef = FirebaseDatabase.getInstance().getReference()
        storageRef = FirebaseStorage.getInstance().getReference()


        // Retrieve job details from arguments
        job = arguments?.getSerializable("job") as? Job

        // Initialize RecycleView
        recyclerView = view.findViewById(R.id.rvApplicant)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext()) // Set layout manager here
        recyclerView.setHasFixedSize(true)

        applicantList = ArrayList()

        // Fetch data from Firebase
        fetchApplicants()

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener() {
            requireActivity().onBackPressed()
        }

        tvTotalCount = view.findViewById(R.id.tvTotalCount)


        return view
    }


   private fun fetchApplicants() {
        val applyJobRef = myRef.child("Apply_Job")

        val query = applyJobRef.orderByChild("jobID").equalTo(job?.jobID.toString().toDouble())

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fetchedApplicants = 0

                if (snapshot.exists()) {
                    totalApplicants = snapshot.childrenCount.toInt()

                    for (applicantSnap in snapshot.children) {
                        val applyJob = applicantSnap.getValue(Apply_Job::class.java)
                        // get the personalID from Apply_Job // so can know which person has applied for the job
                        fetchPersonalDetails(applyJob?.personalID)
                    }
                } else {
                    showErrorDialog()
                    tvTotalCount.text = "0"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchPersonalDetails(personalID: Int?) {
        val listerner = this

        val personalRef = FirebaseDatabase.getInstance().getReference().child("Personal")

        val query = personalRef.orderByChild("personalID").equalTo(personalID.toString().toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val fetchedApplicantsList =
                    ArrayList<Personal>() // Temporary list to hold fetched applicants

                if (snapshot.exists()) {
                    for (personalSnap in snapshot.children) {
                        val personal = personalSnap.getValue(Personal::class.java)
                        personal?.let {
                            fetchedApplicantsList.add(it)
                        }
                    }

                    // Update the total fetched applicants count
                    fetchedApplicants += fetchedApplicantsList.size

                    // Add fetched applicants to the main list
                    applicantList.addAll(fetchedApplicantsList)

                    // If all applicants are fetched, update the UI
                    if (fetchedApplicants >= totalApplicants) {
                        val adapter = ViewApplicantAdapter(listerner)
                        recyclerView.adapter = adapter
                        tvTotalCount.text = fetchedApplicants.toString()
                        adapter.setData(applicantList)
                    }
                }else{
                    showErrorDialog()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showErrorDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
            .setMessage("No Record(s) Found")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

    override fun onItemClick(position: Int) {
        val applicantObj: Personal = applicantList[position]

        val fragment = Applicant_details()

        //Add Result Object into Bundle
        val bundle = Bundle()
        bundle.putSerializable("applicant", applicantObj)
        fragment.arguments = bundle

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()

       /* val ref = storageRef.child("Resume").child(clickedPersonal.personalID.toString() + ".pdf")
        ref.downloadUrl
            .addOnSuccessListener { uri ->
                openPDF(uri.toString())
            }
            .addOnFailureListener { exception ->
                // Handle any errors that may occur during the download
                Log.e(TAG, "Error downloading PDF", exception)
                Toast.makeText(requireContext(), "Error downloading PDF", Toast.LENGTH_SHORT).show()
            }*/
    }

    /*private fun openPDF(downloadUrl: String) {
        val uri = Uri.parse(downloadUrl)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf") // Changed MIME type to application/pdf
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer installed", Toast.LENGTH_SHORT).show()
        }
    }*/
}
