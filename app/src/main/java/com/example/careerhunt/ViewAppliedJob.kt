package com.example.careerhunt

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Apply_Job
import com.example.careerhunt.data.Personal
import com.example.careerhunt.dataAdapter.AppliedJobListAdapter
import com.example.careerhunt.databinding.FragmentViewAppliedJobBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ViewAppliedJob : Fragment() {
    private lateinit var binding: FragmentViewAppliedJobBinding
    private lateinit var appliedJobListAdapter: AppliedJobListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedIDPreferences: SharedPreferences

    private var database =
        FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private var myRef = database.reference
    private var userId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewAppliedJobBinding.inflate(inflater, container, false)
        // Access SharedPreferences from MainActivity
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        // Retrieve userId from SharedPreferences
        userId = sharedIDPreferences.getString("userid", "") ?: ""

        recyclerView = binding.rvAppliedJob
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        appliedJobListAdapter = AppliedJobListAdapter()
        recyclerView.adapter = appliedJobListAdapter
        fetchAppliedJobData()
        return binding.root
    }

    private fun fetchAppliedJobData() {
        Toast.makeText(requireContext(), "fetchData", Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(), "fetchDID: " + userId, Toast.LENGTH_SHORT).show()

        val appliedJobRef = FirebaseDatabase.getInstance().getReference("Apply_Job")
            .orderByChild("personalID").equalTo("1")

        appliedJobRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Toast.makeText(requireContext(), "onDataChange", Toast.LENGTH_SHORT).show()

                val applyJobList = mutableListOf<Apply_Job>()
                for (snapshot in dataSnapshot.children) {
                    val applyJob = snapshot.getValue(Apply_Job::class.java)
                    applyJob?.let { applyJobList.add(it) }
                }
                appliedJobListAdapter.setData(applyJobList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error fetching applied jobs: ${databaseError.message}")
            }
        })
    }
}


/* private fun fetchAppliedJobData() {
     Toast.makeText(requireContext(), "fetchData", Toast.LENGTH_SHORT).show()
                val appliedJobRef = FirebaseDatabase.getInstance().getReference("Apply_Job")
                    .orderByChild("personalID").equalTo(userId)
                appliedJobRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Toast.makeText(requireContext(), "onDataChange", Toast.LENGTH_SHORT).show()

                        val applyJobList = mutableListOf<Apply_Job>()
                        for (snapshot in dataSnapshot.children) {
                            val applyJob = snapshot.getValue(Apply_Job::class.java)
                            applyJob?.let { applyJobList.add(it) }
                        }
                        appliedJobListAdapter.setData(applyJobList)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(requireContext(), "Error: " + databaseError, Toast.LENGTH_SHORT).show()
                    }
                })
            }

     try {
         // Check if userId is not null before converting it to Int
         val id: Int = userId?.toIntOrNull()
             ?: 0 // Default value is 0 if userId is null or not convertible to Int
         Toast.makeText(requireContext(), "Converting", Toast.LENGTH_SHORT).show()

         Toast.makeText(requireContext(), "uIDConvert: $id", Toast.LENGTH_SHORT).show()

         val appliedJobRef = FirebaseDatabase.getInstance().getReference("Apply_Job")
         appliedJobRef.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 Toast.makeText(requireContext(), "onDataChange", Toast.LENGTH_SHORT).show()
                 val applyJobList = mutableListOf<Apply_Job>()
                 for (snapshot in dataSnapshot.children) {
                     val applyJob = snapshot.getValue(Apply_Job::class.java)
                     applyJob?.let {
                         // Compare with applicantID after converting userId to Int
                         if (it.personalID == id) {
                             applyJobList.add(it)
                         }
                     }
                 }
                 appliedJobListAdapter.setData(applyJobList)
             }

             override fun onCancelled(databaseError: DatabaseError) {
                 // Handle error
             }
         })
     } catch (e: Exception) {
         // Handle exception
         Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
     }



 }*/
